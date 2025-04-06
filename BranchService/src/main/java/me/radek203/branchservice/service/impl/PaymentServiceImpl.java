package me.radek203.branchservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import me.radek203.branchservice.client.HeadquarterClient;
import me.radek203.branchservice.config.AppProperties;
import me.radek203.branchservice.entity.Client;
import me.radek203.branchservice.entity.Transfer;
import me.radek203.branchservice.entity.TransferStatus;
import me.radek203.branchservice.exception.ResourceInvalidException;
import me.radek203.branchservice.exception.ResourceNotFoundException;
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

    @Transactional
    void completeTransfer(Client client, Transfer transfer) {
        client.setBalance(client.getBalance() + transfer.getAmount());
        transfer.setStatus(TransferStatus.COMPLETED);
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

        completeTransfer(toClient, transfer);

        kafkaSenderService.sendMessage("branch-" + transfer.getFromBranchId() + "-transfer-completed", String.valueOf(transfer.getId()), transfer);
    }

    @Override
    @Transactional
    public void failedTransfer(Transfer transfer) {
        Optional<Transfer> transferFound = transferRepository.findById(transfer.getId());
        if (transferFound.isEmpty()) {
            return;
        }
        transferRepository.deleteById(transfer.getId());

        Optional<Client> fromClient = clientRepository.findByAccountNumber(transfer.getFromAccount());
        if (fromClient.isEmpty()) {
            return;
        }
        Client client = fromClient.get();
        client.setBalance(client.getBalance() + transfer.getAmount());
        client.setBalanceReserved(client.getBalanceReserved() - transfer.getAmount());
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

        Optional<Client> fromClient = clientRepository.findByAccountNumber(transfer.getFromAccount());
        if (fromClient.isEmpty()) {
            return;
        }
        Client client = fromClient.get();
        client.setBalanceReserved(client.getBalanceReserved() - transfer.getAmount());
        clientRepository.save(client);
    }

}
