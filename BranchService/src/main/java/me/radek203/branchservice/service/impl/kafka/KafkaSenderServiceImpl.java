package me.radek203.branchservice.service.impl.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import me.radek203.branchservice.config.AppProperties;
import me.radek203.branchservice.service.KafkaSenderService;
import me.radek203.branchservice.service.KafkaTopicErrorHandler;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class KafkaSenderServiceImpl implements KafkaSenderService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AppProperties appProperties;
    private final Map<String, KafkaTopicErrorHandler> handlers = new HashMap<>();

    @PostConstruct
    public void addHandlers() {
        handlers.put("branch-" + appProperties.getBranchId() + "-transfer-create", KafkaTransferErrorHandlers.getKafkaTransferErrorHandler(this));
    }

    @Override
    public void sendMessage(String topic, String key, Object payload) {
        String message;
        try {
            message = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            handleKafkaError(topic, key, payload.toString(), e.getMessage(), Direction.OUT);
            return;
        }
        kafkaTemplate.send(topic, key, message).whenComplete((result, exception) -> {
            if (exception != null) {
                handleKafkaError(topic, key, message, exception.getMessage(), Direction.OUT);
            }
        });
    }

    @Override
    public void handleKafkaError(String topic, String key, String value, String error, Direction direction) {
        if (direction == Direction.IN) {
            if (getHandler(topic).handleError(key, value)) {
                return;
            }
        }
        System.out.println("Error on Kafka topic: " + topic + ", Key: " + key + ", Value: " + value + " , Error: " + error + ", Direction: " + direction);
    }

    private KafkaTopicErrorHandler getHandler(String topic) {
        return handlers.getOrDefault(topic, (key, value) -> false);
    }

}
