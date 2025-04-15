package me.radek203.headquarterservice.service.impl.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import me.radek203.headquarterservice.service.KafkaSenderService;
import me.radek203.headquarterservice.service.KafkaTopicErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class KafkaSenderServiceImpl implements KafkaSenderService {

    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaSenderServiceImpl.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Map<String, KafkaTopicErrorHandler> handlers = new HashMap<>();

    @PostConstruct
    public void addHandlers() {
        handlers.put("headquarter-client-create", KafkaClientErrorHandlers.getKafkaClientCreateHandler(this));
        handlers.put("headquarter-transfer-create", KafkaTransferErrorHandlers.getKafkaTransferCreateHandler(this));
        handlers.put("headquarter-balance-deposit", KafkaTransferErrorHandlers.getKafkaTransferErrorHandler(this));
        handlers.put("headquarter-balance-withdraw", KafkaTransferErrorHandlers.getKafkaTransferErrorHandler(this));
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
        kafkaTemplate.send(topic, message).whenComplete((result, exception) -> {
            if (exception != null) {
                handleKafkaError(topic, key, message, exception.getMessage(), Direction.OUT);
            }
        });
    }

    @Override
    public void handleKafkaError(String topic, String key, String value, String error, KafkaSenderService.Direction direction) {
        if (direction == KafkaSenderService.Direction.IN) {
            if (getHandler(topic).handleError(key, value)) {
                return;
            }
        }
        LOGGER.error("Error on Kafka topic: {}, Key: {}, Value: {} , Error: {}, Direction: {}", topic, key, value, error, direction);
    }

    private KafkaTopicErrorHandler getHandler(String topic) {
        return handlers.getOrDefault(topic, (key, value) -> false);
    }

}
