package me.radek203.branchservice.config;

import me.radek203.branchservice.service.KafkaSenderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlerConfig {

    /**
     * Configures a DefaultErrorHandler for Kafka message processing.
     * This handler will retry processing the message 3 times with a 1-second delay
     * before sending the error to the KafkaSenderService for further handling.
     *
     * @param kafkaSenderService the KafkaSenderService to handle errors
     * @return a DefaultErrorHandler instance
     */
    @Bean
    public DefaultErrorHandler errorHandler(KafkaSenderService kafkaSenderService) {
        return new DefaultErrorHandler((record, exception) -> kafkaSenderService.handleKafkaError(record.topic(), (String) record.key(), (String) record.value(), exception.getMessage(), KafkaSenderService.Direction.IN), new FixedBackOff(1000L, 3));
    }

}
