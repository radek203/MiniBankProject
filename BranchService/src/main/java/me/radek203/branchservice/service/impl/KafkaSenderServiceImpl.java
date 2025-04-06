package me.radek203.branchservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.radek203.branchservice.config.AppProperties;
import me.radek203.branchservice.entity.Transfer;
import me.radek203.branchservice.entity.TransferStatus;
import me.radek203.branchservice.service.KafkaSenderService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaSenderServiceImpl implements KafkaSenderService {

    private ObjectMapper mapper = new ObjectMapper();
    private KafkaTemplate<String, String> kafkaTemplate;
    private AppProperties appProperties;

    @Override
    public void sendMessage(String topic, String key, Object payload) {
        String message;
        try {
            message = mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            saveDeadLetter(topic, key, payload.toString(), e.getMessage(), Direction.OUT);
            return;
        }
        kafkaTemplate.send(topic, key, message).whenComplete((result, exception) -> {
            if (exception != null) {
                saveDeadLetter(topic, key, message, exception.getMessage(), Direction.OUT);
            }
        });
    }

    @Override
    public void saveDeadLetter(String topic, String key, String value, String error, Direction direction) {
        // Implement the logic to save the dead letter message
        // For example, you can log it or store it in a database
        if (direction == Direction.IN) {
            if (topic.equals("branch-" + appProperties.getBranchId() + "-transfer-create")) {
                try {
                    Transfer transfer = mapper.readValue(value, Transfer.class);
                    transfer.setStatus(TransferStatus.FAILED);
                    sendMessage("branch-" + transfer.getFromBranchId() + "-transfer-failed", key, transfer);
                    return;
                } catch (JsonProcessingException ignored) {
                }
            }
        }
        System.out.println("Dead letter saved: Topic: " + topic + ", Key: " + key + ", Value: " + value + " , Error: " + error + ", Direction: " + direction);
    }

}
