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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    @Override
    @Transactional
    public Transfer makeTransfer(String fromAccount, String toAccount, String message, double amount) {
        if (amount <= 0) {
            throw new ResourceInvalidException("error/invalid-amount");
        }

        Client fromClient = clientRepository.findByAccountNumber(fromAccount).orElseThrow(() -> new ResourceNotFoundException("error/account-not-found", fromAccount));

        if (fromClient.getBalance() < amount) {
            throw new ResourceInvalidException("error/insufficient-balance");
        }

        ResponseEntity<Integer> branchId = hqClient.getBranchId(toAccount);
        if (branchId.getStatusCode() != HttpStatus.OK || branchId.getBody() == null) {
            throw new ResourceInvalidException("error/invalid-account");
        }

        fromClient.setBalance(fromClient.getBalance() - amount);
        fromClient.setBalanceReserved(fromClient.getBalanceReserved() + amount);
        clientRepository.save(fromClient);

        Transfer transfer = new Transfer(null, fromAccount, toAccount, amount, message, TransferStatus.STARTED, appProperties.getBranchId(), branchId.getBody(), System.currentTimeMillis());
        transfer.setId(UUID.randomUUID());
        Transfer transferSaved = transferRepository.save(transfer);
        kafkaSenderService.sendMessage("branch-" + branchId.getBody() + "-transfer-create", String.valueOf(transferSaved.getId()), transferSaved);

        return transferSaved;
    }

    @Override
    @Transactional
    public Transfer makePaymentTransfer(String fromAccount, UUID service, String message, double amount) {
        ResponseEntity<String> toAccount = hqClient.getAccountNumber(service);
        if (toAccount.getStatusCode() != HttpStatus.OK || toAccount.getBody() == null) {
            throw new ResourceInvalidException("error/invalid-account");
        }
        return makeTransfer(fromAccount, toAccount.getBody(), message, amount);
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
        transferRepository.deleteById(transfer.getId());

        Optional<Client> clientOptional;
        if (transfer.getFromBranchId() == appProperties.getBranchId()) {
            clientOptional = clientRepository.findByAccountNumber(transfer.getFromAccount());
        } else {
            clientOptional = clientRepository.findByAccountNumber(transfer.getToAccount());
        }
        if (clientOptional.isEmpty()) {
            return;
        }
        Client client = clientOptional.get();

        if (transfer.getFromBranchId() == appProperties.getBranchId()) {
            client.setBalance(client.getBalance() + transfer.getAmount());
            client.setBalanceReserved(client.getBalanceReserved() - transfer.getAmount());
        } else {
            client.setBalanceReserved(client.getBalanceReserved() + transfer.getAmount());
        }
        clientRepository.save(client);
    }

    @Override
    @Transactional
    public void completedTransfer(Transfer transfer) {
        Optional<Transfer> transferFound = transferRepository.findById(transfer.getId());
        if (transferFound.isPresent() && transferFound.get().getStatus() == TransferStatus.COMPLETED) {
            return;
        }
        transferRepository.save(transfer);

        Optional<Client> clientOptional;
        if (transfer.getFromBranchId() == appProperties.getBranchId()) {
            clientOptional = clientRepository.findByAccountNumber(transfer.getFromAccount());
        } else {
            clientOptional = clientRepository.findByAccountNumber(transfer.getToAccount());
        }
        if (clientOptional.isEmpty()) {
            return;
        }
        Client client = clientOptional.get();

        if (transfer.getFromBranchId() == appProperties.getBranchId()) {
            client.setBalanceReserved(client.getBalanceReserved() - transfer.getAmount());
        } else {
            client.setBalance(client.getBalance() + transfer.getAmount());
            client.setBalanceReserved(client.getBalanceReserved() + transfer.getAmount());
        }
        clientRepository.save(client);
    }

    @Override
    @Transactional
    public BalanceChange makeDeposit(String account, double amount) {
        Client client = clientRepository.findByAccountNumber(account).orElseThrow(() -> new ResourceNotFoundException("error/invalid-account"));

        client.setBalanceReserved(client.getBalanceReserved() - amount);
        clientRepository.save(client);

        BalanceChange balanceChange = new BalanceChange(UUID.randomUUID(), account, amount, BalanceChangeStatus.STARTED, appProperties.getBranchId());
        balanceChange = balanceChangeRepository.save(balanceChange);

        kafkaSenderService.sendMessage("headquarter-balance-deposit", String.valueOf(balanceChange.getId()), balanceChange);

        return balanceChange;
    }

    @Override
    @Transactional
    public BalanceChange makeWithdraw(String account, double amount) {
        Client client = clientRepository.findByAccountNumber(account).orElseThrow(() -> new ResourceNotFoundException("error/invalid-account"));

        if (client.getBalance() < amount) {
            throw new ResourceInvalidException("error/insufficient-balance");
        }

        client.setBalance(client.getBalance() - amount);
        client.setBalanceReserved(client.getBalanceReserved() + amount);
        clientRepository.save(client);

        BalanceChange balanceChange = new BalanceChange(UUID.randomUUID(), account, -amount, BalanceChangeStatus.STARTED, appProperties.getBranchId());
        balanceChange = balanceChangeRepository.save(balanceChange);

        kafkaSenderService.sendMessage("headquarter-balance-withdraw", String.valueOf(balanceChange.getId()), balanceChange);

        return balanceChange;
    }

    @Override
    @Transactional
    public void completedBalanceChange(BalanceChange balanceChange) {
        Optional<Client> clientOptional = clientRepository.findByAccountNumber(balanceChange.getAccount());
        if (clientOptional.isEmpty()) {
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
        Client client = clientOptional.get();
        client.setBalanceReserved(client.getBalanceReserved() + balanceChange.getAmount());
        if (balanceChange.getAmount() < 0) {
            client.setBalance(client.getBalance() - balanceChange.getAmount());
        }
        clientRepository.save(client);

        balanceChangeRepository.deleteById(balanceChange.getId());
    }

}
