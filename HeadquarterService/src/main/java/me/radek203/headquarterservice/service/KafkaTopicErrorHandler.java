package me.radek203.headquarterservice.service;

@FunctionalInterface
public interface KafkaTopicErrorHandler {

    boolean handleError(String key, String value);

}
