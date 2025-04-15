package me.radek203.apigateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.time.LocalDateTime;

@Component
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    {
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * This method handles exceptions that occur during the processing of requests.
     * It checks if the exception is a ConnectException (indicating a service is unavailable)
     *
     * @param exchange the current server web exchange
     * @param ex       the exception that occurred
     * @return a Mono that completes when the response is written
     */
    @SneakyThrows
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (ex instanceof ConnectException) {
            exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), "error/service-unavailable", "", exchange.getRequest().getPath().toString(), HttpStatus.SERVICE_UNAVAILABLE.toString());
            byte[] bytes = objectMapper.writeValueAsBytes(errorDetails);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            exchange.getResponse().getHeaders().add("Content-Type", "application/json");
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }

        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return exchange.getResponse().setComplete();
    }
}
