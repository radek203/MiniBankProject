package me.radek203.headquarterservice.service.impl.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.radek203.headquarterservice.entity.Client;
import me.radek203.headquarterservice.service.KafkaSenderService;
import me.radek203.headquarterservice.service.KafkaTopicErrorHandler;

public class KafkaClientErrorHandlers {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static KafkaTopicErrorHandler getKafkaClientCreateHandler(KafkaSenderService sender) {
        return (key, value) -> {
            try {
                Client client = OBJECT_MAPPER.readValue(value, Client.class);
                sender.sendMessage("branch-" + client.getBranch() + "-client-create-error", String.valueOf(client.getId()), client);
                return true;
            } catch (JsonProcessingException ignored) {
            }
            return false;
        };
    }

}
