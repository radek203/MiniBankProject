package me.radek203.branchservice.service;

public interface KafkaSenderService {

    /**
     * Sends a message to a Kafka topic.
     *
     * @param topic   the Kafka topic to send the message to
     * @param key     the key for the message
     * @param payload the payload of the message
     */
    void sendMessage(String topic, String key, Object payload);

    /**
     * Handles errors that occur while sending messages to Kafka.
     *
     * @param topic     the Kafka topic where the error occurred
     * @param key       the key for the message that caused the error
     * @param value     the value of the message that caused the error
     * @param error     the error message
     * @param direction the direction of the message (IN or OUT)
     */
    void handleKafkaError(String topic, String key, String value, String error, Direction direction);

    /**
     * Enum representing the direction of the message (IN or OUT).
     */
    enum Direction {
        IN, OUT
    }

}
