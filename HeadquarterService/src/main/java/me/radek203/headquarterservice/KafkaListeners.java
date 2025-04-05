package me.radek203.headquarterservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.radek203.headquarterservice.entity.Client;
import me.radek203.headquarterservice.entity.ClientStatus;
import me.radek203.headquarterservice.entity.Transfer;
import me.radek203.headquarterservice.repository.ClientRepository;
import me.radek203.headquarterservice.service.KafkaSenderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class KafkaListeners {

    private KafkaSenderService kafkaSenderService;
    private ClientRepository clientRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "headquarter-client-create", groupId = "group_id")
    void listenerClients(String data) throws JsonProcessingException {
        Client client = objectMapper.readValue(data, Client.class);
        client.setStatus(ClientStatus.ACTIVE);
        try {
            clientRepository.save(client);
        } catch (Exception e) {
            kafkaSenderService.sendMessage("branch-" + client.getBranch() + "-client-create-error", String.valueOf(client.getId()), client);
            return;
        }
        kafkaSenderService.sendMessage("branch-" + client.getBranch() + "-client-create-active", String.valueOf(client.getId()), client);
    }

    @KafkaListener(topics = "headquarter-transfer-create", groupId = "group_id")
    void listenerTransfers(String data) throws JsonProcessingException {
        Transfer transfer = objectMapper.readValue(data, Transfer.class);
        System.out.println("Listener transfers received: " + transfer);
    }

}
