package me.radek203.branchservice.service;

public interface KafkaSenderService {

    void sendMessage(String topic, String key, Object payload);

    void saveDeadLetter(String topic, String key, String value, String error, Direction direction);

    enum Direction {
        IN, OUT
    }

}
