package me.radek203.headquarterservice.config;

import me.radek203.headquarterservice.service.KafkaSenderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaSenderService sender) {
        return new DefaultErrorHandler((record, exception) -> sender.handleKafkaError(record.topic(), (String) record.key(), (String) record.value(), exception.getMessage(), KafkaSenderService.Direction.IN), new FixedBackOff(1000L, 3));
    }

}
