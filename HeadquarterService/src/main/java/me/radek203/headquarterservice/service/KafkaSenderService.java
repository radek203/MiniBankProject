package me.radek203.headquarterservice.service;

public interface KafkaSenderService {

    void sendMessage(String topic, String key, Object payload);
    void saveDeadLetter(String topic, String key, String value, String error, String direction);

}
