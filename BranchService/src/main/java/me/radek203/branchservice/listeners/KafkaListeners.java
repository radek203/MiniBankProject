package me.radek203.branchservice.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.radek203.branchservice.entity.BalanceChange;
import me.radek203.branchservice.entity.Transfer;
import me.radek203.branchservice.service.ClientService;
import me.radek203.branchservice.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * KafkaListeners is a component that listens to various Kafka topics and processes the messages.
 * It handles client creation, transfer creation, and balance change events.
 */
@AllArgsConstructor
@Component
public class KafkaListeners {

    private final PaymentService paymentService;
    private final ClientService clientService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "branch-${branch.id}-client-create-error", groupId = "branch_group")
    void listenerClientError(String data) throws JsonProcessingException {
        UUID id = objectMapper.readValue(data, UUID.class);
        clientService.failedClient(id);
    }

    @KafkaListener(topics = "branch-${branch.id}-client-create-active", groupId = "branch_group")
    void listenerClientCreate(String data) throws JsonProcessingException {
        UUID id = objectMapper.readValue(data, UUID.class);
        clientService.completedClient(id);
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
