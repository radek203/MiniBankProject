package me.radek203.branchservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.radek203.branchservice.entity.Client;
import me.radek203.branchservice.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class ClientController {

    private KafkaTemplate<String, String> consumer;
    private ClientService clientService;

    @PostMapping("/create")
    public ResponseEntity<String> create(@Validated @RequestBody Client client) {

        ObjectMapper objectMapper = new ObjectMapper();
        String clientJson;
        try {
            clientJson = objectMapper.writeValueAsString(client);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error converting client to JSON");
        }
        consumer.send("headquarter-client-create", clientJson);
        return ResponseEntity.ok("Message sent to Kafka topic");
    }

}
