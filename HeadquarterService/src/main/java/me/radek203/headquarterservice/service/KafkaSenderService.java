package me.radek203.headquarterservice.service;

public interface KafkaSenderService {

    void sendMessage(String topic, String key, Object payload);

    void handleKafkaError(String topic, String key, String value, String error, Direction direction);

    enum Direction {
        IN, OUT
    }

}
