package me.radek203.branchservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import me.radek203.branchservice.entity.Client;
import me.radek203.branchservice.entity.Message;
import me.radek203.branchservice.entity.Transfer;
import me.radek203.branchservice.entity.TransferStatus;
import me.radek203.branchservice.repository.ClientRepository;
import me.radek203.branchservice.repository.TransferRepository;
import me.radek203.branchservice.service.KafkaSenderService;
import me.radek203.branchservice.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Optional;

@AllArgsConstructor
@Component
public class KafkaListeners {

    private ClientRepository clientRepository;
    private PaymentService paymentService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "branch-${branch.id}", groupId = "group_id")
    void listener(String data) throws JsonProcessingException {
        Message message = objectMapper.readValue(data, Message.class);
        System.out.println("Listener received: " + message);
    }

    @KafkaListener(topics = "branch-${branch.id}-client-create-error", groupId = "group_id")
    void listenerClientError(String data) throws JsonProcessingException {
        Client client = objectMapper.readValue(data, Client.class);
        clientRepository.deleteById(client.getId());
    }

    @KafkaListener(topics = "branch-${branch.id}-client-create-active", groupId = "group_id")
    void listenerClientCreate(String data) throws JsonProcessingException {
        Client client = objectMapper.readValue(data, Client.class);
        clientRepository.save(client);
    }

    @KafkaListener(topics = "branch-${branch.id}-transfer-create", groupId = "group_id")
    void listenerTransferCreate(String data) throws JsonProcessingException {
        Transfer transfer = objectMapper.readValue(data, Transfer.class);
        paymentService.createTransfer(transfer);
    }

    @KafkaListener(topics = "branch-${branch.id}-transfer-failed", groupId = "group_id")
    void listenerTransferFailed(String data) throws JsonProcessingException {
        Transfer transfer = objectMapper.readValue(data, Transfer.class);
        paymentService.failedTransfer(transfer);
    }

    @KafkaListener(topics = "branch-${branch.id}-transfer-completed", groupId = "group_id")
    void listenerTransferCompleted(String data) throws JsonProcessingException {
        Transfer transfer = objectMapper.readValue(data, Transfer.class);
        paymentService.completedTransfer(transfer);
    }

}
