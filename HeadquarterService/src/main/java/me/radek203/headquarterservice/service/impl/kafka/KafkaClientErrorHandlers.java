package me.radek203.headquarterservice.service.impl.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.radek203.headquarterservice.entity.Client;
import me.radek203.headquarterservice.service.KafkaSenderService;
import me.radek203.headquarterservice.service.KafkaTopicErrorHandler;

public class KafkaClientErrorHandlers {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    public static KafkaTopicErrorHandler getKafkaClientCreateHandler(KafkaSenderService sender) {
        return (key, value) -> {
            try {
                Client client = OBJECT_MAPPER.readValue(value, Client.class);
                sender.sendMessage("branch-" + client.getBranch() + "-client-create-error", String.valueOf(client.getId()), client.getId());
                return true;
            } catch (JsonProcessingException ignored) {
            }
            return false;
        };
    }

}
