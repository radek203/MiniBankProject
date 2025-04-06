package me.radek203.branchservice.service;

@FunctionalInterface
public interface KafkaTopicErrorHandler {

    boolean handleError(String key, String value);

}
