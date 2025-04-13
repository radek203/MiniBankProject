package me.radek203.branchservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.radek203.branchservice.entity.BalanceChange;
import me.radek203.branchservice.entity.Client;
import me.radek203.branchservice.entity.Transfer;
import me.radek203.branchservice.repository.ClientRepository;
import me.radek203.branchservice.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class KafkaListeners {

    private final ClientRepository clientRepository;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "branch-${branch.id}-client-create-error", groupId = "branch_group")
    void listenerClientError(String data) throws JsonProcessingException {
        Client client = objectMapper.readValue(data, Client.class);
        clientRepository.deleteById(client.getId());
    }

    @KafkaListener(topics = "branch-${branch.id}-client-create-active", groupId = "branch_group")
    void listenerClientCreate(String data) throws JsonProcessingException {
        Client client = objectMapper.readValue(data, Client.class);
        clientRepository.save(client);
    }

    @KafkaListener(topics = "branch-${branch.id}-transfer-create", groupId = "branch_group")
    void listenerTransferCreate(String data) throws JsonProcessingException {
        Transfer transfer = objectMapper.readValue(data, Transfer.class);
        paymentService.createTransfer(transfer);
    }

    @KafkaListener(topics = "branch-${branch.id}-transfer-failed", groupId = "branch_group")
    void listenerTransferFailed(String data) throws JsonProcessingException {
        Transfer transfer = objectMapper.readValue(data, Transfer.class);
        paymentService.failedTransfer(transfer);
    }

    @KafkaListener(topics = "branch-${branch.id}-transfer-completed", groupId = "branch_group")
    void listenerTransferCompleted(String data) throws JsonProcessingException {
        Transfer transfer = objectMapper.readValue(data, Transfer.class);
        paymentService.completedTransfer(transfer);
    }

    @KafkaListener(topics = "branch-${branch.id}-balance-change-completed", groupId = "branch_group")
    void listenerBalanceChangeCompleted(String data) throws JsonProcessingException {
        BalanceChange balanceChange = objectMapper.readValue(data, BalanceChange.class);
        paymentService.completedBalanceChange(balanceChange);
    }

    @KafkaListener(topics = "branch-${branch.id}-balance-change-failed", groupId = "branch_group")
    void listenerBalanceChangeFailed(String data) throws JsonProcessingException {
        BalanceChange balanceChange = objectMapper.readValue(data, BalanceChange.class);
        paymentService.failedBalanceChange(balanceChange);
    }

}
