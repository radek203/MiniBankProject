package me.radek203.branchservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
    private ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/create")
    public ResponseEntity<String> create(@Validated @RequestBody Client client) throws JsonProcessingException {
        consumer.send("headquarter-client-create", mapper.writeValueAsString(client));
        return ResponseEntity.ok("Message sent to Kafka topic");
    }

}
