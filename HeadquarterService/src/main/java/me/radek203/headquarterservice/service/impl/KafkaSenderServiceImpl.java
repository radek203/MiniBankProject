package me.radek203.headquarterservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.radek203.headquarterservice.service.KafkaSenderService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaSenderServiceImpl implements KafkaSenderService {

    private ObjectMapper mapper = new ObjectMapper();
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendMessage(String topic, String key, Object payload) {
        String message;
        try {
            message = mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            saveDeadLetter(topic, key, payload.toString(), e.getMessage(), "SEND");
            return;
        }
        kafkaTemplate.send(topic, message).whenComplete((result, exception) -> {
            if (exception != null) {
                saveDeadLetter(topic, key, message, exception.getMessage(), "SEND");
            }
        });
    }

    @Override
    public void saveDeadLetter(String topic, String key, String value, String error, String direction) {
        // Implement the logic to save the dead letter message
        // For example, you can log it or store it in a database
        System.out.println("Dead letter saved: Topic: " + topic + ", Key: " + key + ", Value: " + value + " , Error: " + error + ", Direction: " + direction);
    }

}
