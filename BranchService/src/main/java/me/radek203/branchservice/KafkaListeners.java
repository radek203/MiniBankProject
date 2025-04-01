package me.radek203.branchservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.radek203.branchservice.entity.Message;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class KafkaListeners {

    private ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "branch-${branch.id}", groupId = "group_id")
    void listener(String data) throws JsonProcessingException {
        Message message = objectMapper.readValue(data, Message.class);
        System.out.println("Listener received: " + message);
    }

}
