package me.radek203.branchservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.radek203.branchservice.entity.Client;
import me.radek203.branchservice.entity.Message;
import me.radek203.branchservice.repository.ClientRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class KafkaListeners {

    private ClientRepository clientRepository;
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

}
