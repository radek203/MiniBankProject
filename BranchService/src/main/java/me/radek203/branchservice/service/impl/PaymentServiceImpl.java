package me.radek203.branchservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import me.radek203.branchservice.client.HeadquarterClient;
import me.radek203.branchservice.config.AppProperties;
import me.radek203.branchservice.entity.*;
import me.radek203.branchservice.exception.ResourceInvalidException;
import me.radek203.branchservice.exception.ResourceNotFoundException;
import me.radek203.branchservice.repository.BalanceChangeRepository;
import me.radek203.branchservice.repository.ClientRepository;
import me.radek203.branchservice.repository.TransferRepository;
import me.radek203.branchservice.service.KafkaSenderService;
import me.radek203.branchservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final KafkaSenderService kafkaSenderService;
    private final TransferRepository transferRepository;
    private final BalanceChangeRepository balanceChangeRepository;
    private final ClientRepository clientRepository;
    private final AppProperties appProperties;
    private final HeadquarterClient hqClient;
    private final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private Client getClientByAccountAndUserId(String account, int userId) {
        return clientRepository.findByAccountNumberAndUserId(account, userId).orElseThrow(() -> new ResourceNotFoundException("error/invalid-account"));
    }

    @Override
    public Transfer getTransfer(String account, UUID id, int userId) {
        getClientByAccountAndUserId(account, userId);
        return transferRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("error/transfer-not-found", String.valueOf(id)));
    }

    @Override
    @Transactional
    public Transfer makeTransfer(String fromAccount, String toAccount, double amount, int userId) {
        if (amount <= 0) {
            throw new ResourceInvalidException("error/invalid-amount");
        }
        if (fromAccount.equals(toAccount)) {
            throw new ResourceInvalidException("error/invalid-account");
        }

        Client fromClient = getClientByAccountAndUserId(fromAccount, userId);

        if (fromClient.getBalance() < amount) {
            throw new ResourceInvalidException("error/insufficient-balance");
        }

        Integer branchId = hqClient.getResponse("error/invalid-account", hqClient::getBranchId, toAccount);

        fromClient.setBalance(fromClient.getBalance() - amount);
        fromClient.setBalanceReserved(fromClient.getBalanceReserved() + amount);
        clientRepository.save(fromClient);

        if (appProperties.getBranchId() == branchId) {
            Client toClient = clientRepository.findByAccountNumber(toAccount).orElseThrow(() -> new ResourceNotFoundException("error/invalid-account"));
            toClient.setBalanceReserved(toClient.getBalanceReserved() - amount);
            clientRepository.save(toClient);
        }

        Transfer transfer = new Transfer(null, fromAccount, toAccount, amount, TransferStatus.STARTED, appProperties.getBranchId(), branchId, System.currentTimeMillis());
        transfer.setId(UUID.randomUUID());
        Transfer transferSaved = transferRepository.save(transfer);

        if (appProperties.getBranchId() == branchId) {
            kafkaSenderService.sendMessage("headquarter-transfer-create", String.valueOf(transfer.getId()), transfer);
        } else {
            kafkaSenderService.sendMessage("branch-" + branchId + "-transfer-create", String.valueOf(transfer.getId()), transfer);
        }

        return transferSaved;
    }

    @Override
    @Transactional
    public Transfer makePaymentTransfer(String fromAccount, UUID service, double amount) {
        String toAccount = hqClient.getResponse("error/invalid-account", hqClient::getAccountNumber, service);
        Client fromClient = clientRepository.findByAccountNumber(fromAccount).orElseThrow(() -> new ResourceNotFoundException("error/invalid-account"));
        return makeTransfer(fromAccount, toAccount, amount, fromClient.getUserId());
    }

    @Override
    public List<Transfer> getTransfersByAccount(String account, int userId) {
        getClientByAccountAndUserId(account, userId);
        return transferRepository.findAllByFromAccountOrToAccount(account, account);
    }

    @Transactional
    void acceptTransfer(Client client, Transfer transfer) {
        client.setBalanceReserved(client.getBalanceReserved() - transfer.getAmount());
        clientRepository.save(client);
        transferRepository.save(transfer);
    }

    @Override
    @Transactional
    public void createTransfer(Transfer transfer) {
        Optional<Transfer> transferFound = transferRepository.findById(transfer.getId());
        if (transferFound.isPresent()) {
            return;
        }
        Client toClient = clientRepository.findByAccountNumber(transfer.getToAccount()).orElseThrow(() -> new ResourceNotFoundException("error/invalid-account"));

        acceptTransfer(toClient, transfer);

        kafkaSenderService.sendMessage("headquarter-transfer-create", String.valueOf(transfer.getId()), transfer);
    }

    @Override
    @Transactional
    public void failedTransfer(Transfer transfer) {
        Optional<Transfer> transferFound = transferRepository.findById(transfer.getId());
        if (transferFound.isEmpty()) {
            return;
        }
        logger.warn("Transfer failed: {} cancelled.", transfer.getId());
        transferRepository.deleteById(transfer.getId());

        if (transfer.getFromBranchId() == appProperties.getBranchId()) {
            Optional<Client> clientOptional = clientRepository.findByAccountNumber(transfer.getFromAccount());
            if (clientOptional.isPresent()) {
                Client client = clientOptional.get();
                client.setBalance(client.getBalance() + transfer.getAmount());
                client.setBalanceReserved(client.getBalanceReserved() - transfer.getAmount());
                clientRepository.save(client);
            }
        }
        if (transfer.getToBranchId() == appProperties.getBranchId()) {
            Optional<Client> clientOptional = clientRepository.findByAccountNumber(transfer.getToAccount());
            if (clientOptional.isPresent()) {
                Client client = clientOptional.get();
                client.setBalanceReserved(client.getBalanceReserved() + transfer.getAmount());
                clientRepository.save(client);
            }
        }
    }

    @Override
    @Transactional
    public void completedTransfer(Transfer transfer) {
        Optional<Transfer> transferFound = transferRepository.findById(transfer.getId());
        if (transferFound.isPresent() && transferFound.get().getStatus() == TransferStatus.COMPLETED) {
            return;
        }
        transferRepository.save(transfer);

        if (transfer.getFromBranchId() == appProperties.getBranchId()) {
            Optional<Client> clientOptional = clientRepository.findByAccountNumber(transfer.getFromAccount());
            if (clientOptional.isPresent()) {
                Client client = clientOptional.get();
                client.setBalanceReserved(client.getBalanceReserved() - transfer.getAmount());
                clientRepository.save(client);
            }
        }
        if (transfer.getToBranchId() == appProperties.getBranchId()) {
            Optional<Client> clientOptional = clientRepository.findByAccountNumber(transfer.getToAccount());
            if (clientOptional.isPresent()) {
                Client client = clientOptional.get();
                client.setBalance(client.getBalance() + transfer.getAmount());
                client.setBalanceReserved(client.getBalanceReserved() + transfer.getAmount());
                clientRepository.save(client);
            }
        }
    }

    @Override
    public BalanceChange getBalanceChange(String account, UUID id, int userId) {
        getClientByAccountAndUserId(account, userId);
        return balanceChangeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("error/balance-change-not-found", String.valueOf(id)));
    }

    @Override
    @Transactional
    public BalanceChange makeDeposit(String account, double amount, int userId) {
        Client client = getClientByAccountAndUserId(account, userId);

        client.setBalanceReserved(client.getBalanceReserved() - amount);
        clientRepository.save(client);

        BalanceChange balanceChange = new BalanceChange(UUID.randomUUID(), account, amount, BalanceChangeStatus.STARTED, appProperties.getBranchId(), System.currentTimeMillis());
        balanceChange = balanceChangeRepository.save(balanceChange);

        kafkaSenderService.sendMessage("headquarter-balance-deposit", String.valueOf(balanceChange.getId()), balanceChange);

        return balanceChange;
    }

    @Override
    @Transactional
    public BalanceChange makeWithdraw(String account, double amount, int userId) {
        Client client = getClientByAccountAndUserId(account, userId);

        if (client.getBalance() < amount) {
            throw new ResourceInvalidException("error/insufficient-balance");
        }

        client.setBalance(client.getBalance() - amount);
        client.setBalanceReserved(client.getBalanceReserved() + amount);
        clientRepository.save(client);

        BalanceChange balanceChange = new BalanceChange(UUID.randomUUID(), account, -amount, BalanceChangeStatus.STARTED, appProperties.getBranchId(), System.currentTimeMillis());
        balanceChange = balanceChangeRepository.save(balanceChange);

        kafkaSenderService.sendMessage("headquarter-balance-withdraw", String.valueOf(balanceChange.getId()), balanceChange);

        return balanceChange;
    }

    @Override
    public List<BalanceChange> getBalanceChanges(String account, int userId) {
        getClientByAccountAndUserId(account, userId);
        return balanceChangeRepository.getBalanceChangesByAccount(account);
    }

    @Override
    @Transactional
    public void completedBalanceChange(BalanceChange balanceChange) {
        Optional<Client> clientOptional = clientRepository.findByAccountNumber(balanceChange.getAccount());
        if (clientOptional.isEmpty()) {
            return;
        }
        Optional<BalanceChange> balanceChangeOptional = balanceChangeRepository.findById(balanceChange.getId());
        if (balanceChangeOptional.isPresent() && balanceChangeOptional.get().getStatus() == BalanceChangeStatus.COMPLETED) {
            return;
        }
        Client client = clientOptional.get();
        client.setBalanceReserved(client.getBalanceReserved() + balanceChange.getAmount());
        if (balanceChange.getAmount() > 0) {
            client.setBalance(client.getBalance() + balanceChange.getAmount());
        }
        clientRepository.save(client);

        balanceChange.setStatus(BalanceChangeStatus.COMPLETED);
        balanceChangeRepository.save(balanceChange);
    }

    @Override
    @Transactional
    public void failedBalanceChange(BalanceChange balanceChange) {
        Optional<Client> clientOptional = clientRepository.findByAccountNumber(balanceChange.getAccount());
        if (clientOptional.isEmpty()) {
            return;
        }
        Optional<BalanceChange> balanceChangeOptional = balanceChangeRepository.findById(balanceChange.getId());
        if (balanceChangeOptional.isEmpty() || balanceChangeOptional.get().getStatus() == BalanceChangeStatus.COMPLETED) {
            return;
        }
        logger.warn("Balance Change failed: {} cancelled.", balanceChange.getId());
        Client client = clientOptional.get();
        client.setBalanceReserved(client.getBalanceReserved() + balanceChange.getAmount());
        if (balanceChange.getAmount() < 0) {
            client.setBalance(client.getBalance() - balanceChange.getAmount());
        }
        clientRepository.save(client);

        balanceChangeRepository.deleteById(balanceChange.getId());
    }

}
