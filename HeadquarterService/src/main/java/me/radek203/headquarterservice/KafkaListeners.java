package me.radek203.headquarterservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.radek203.headquarterservice.entity.Client;
import me.radek203.headquarterservice.entity.Transfer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class KafkaListeners {

    private ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "headquarter-client-create", groupId = "group_id")
    void listenerClients(String data) throws JsonProcessingException {
        Client client = objectMapper.readValue(data, Client.class);
        System.out.println("Listener clients received: " + client);
    }

    @KafkaListener(topics = "headquarter-transfer-create", groupId = "group_id")
    void listenerTransfers(String data) throws JsonProcessingException {
        Transfer transfer = objectMapper.readValue(data, Transfer.class);
        System.out.println("Listener transfers received: " + transfer);
    }

}
