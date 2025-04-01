package me.radek203.branchservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.radek203.branchservice.entity.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class BranchController {

    private KafkaTemplate<String, String> consumer;
    private ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/send")
    public ResponseEntity<String> send() throws JsonProcessingException {
        consumer.send("branch-1", mapper.writeValueAsString(new Message("Hello, Kafka!")));
        consumer.send("headquarter", mapper.writeValueAsString(new Message("Hello, Kafka!")));
        return ResponseEntity.ok("Message sent to Kafka topic");
    }

}
