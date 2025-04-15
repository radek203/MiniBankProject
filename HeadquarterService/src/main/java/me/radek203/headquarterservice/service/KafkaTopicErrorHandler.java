package me.radek203.headquarterservice.service;

@FunctionalInterface
public interface KafkaTopicErrorHandler {

    /**
     * Handles errors that occur during the processing of Kafka topics.
     *
     * @param key   The key associated with the Kafka message.
     * @param value The value of the Kafka message.
     * @return true if the error was handled successfully, false otherwise.
     */
    boolean handleError(String key, String value);

}
