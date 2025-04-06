package me.radek203.headquarterservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import me.radek203.headquarterservice.entity.Client;
import me.radek203.headquarterservice.entity.Transfer;
import me.radek203.headquarterservice.entity.TransferStatus;
import me.radek203.headquarterservice.exception.ResourceInvalidException;
import me.radek203.headquarterservice.repository.ClientRepository;
import me.radek203.headquarterservice.service.KafkaSenderService;
import me.radek203.headquarterservice.service.PaymentService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final ClientRepository clientRepository;
    private final KafkaSenderService kafkaSenderService;

    @Override
    @Transactional
    public void makeTransfer(Transfer transfer) {
        Optional<Client> fromClient = clientRepository.findByAccountNumber(transfer.getFromAccount());
        Optional<Client> toClient = clientRepository.findByAccountNumber(transfer.getToAccount());
        if (fromClient.isEmpty() || fromClient.get().getBalance() < transfer.getAmount() || toClient.isEmpty()) {
            throw new ResourceInvalidException("error/invalid-transfer");
        }

        makeTransaction(fromClient.get(), toClient.get(), transfer);

        kafkaSenderService.sendMessage("branch-" + transfer.getFromBranchId() + "-transfer-completed", String.valueOf(transfer.getId()), transfer);
        kafkaSenderService.sendMessage("branch-" + transfer.getToBranchId() + "-transfer-completed", String.valueOf(transfer.getId()), transfer);
    }

    @Transactional
    void makeTransaction(Client fromClientEntity, Client toClientEntity, Transfer transfer) {
        fromClientEntity.setBalance(fromClientEntity.getBalance() - transfer.getAmount());
        toClientEntity.setBalance(toClientEntity.getBalance() + transfer.getAmount());
        transfer.setStatus(TransferStatus.COMPLETED);

        clientRepository.save(fromClientEntity);
        clientRepository.save(toClientEntity);
    }
}
