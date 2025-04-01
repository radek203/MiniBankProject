package me.radek203.branchservice.controller;

import lombok.AllArgsConstructor;
import me.radek203.branchservice.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class BranchController {

    private KafkaTemplate<String, String> consumer;

    @GetMapping("/send")
    public ResponseEntity<String> send() {
        consumer.send("branch-1", "Hello, Kafka!");
        consumer.send("headquarter", "Hello, Kafka!");
        return ResponseEntity.ok("Message sent to Kafka topic");
    }

}
