package me.radek203.branchservice.client;

import me.radek203.branchservice.exception.ResourceInvalidException;
import org.springframework.http.ResponseEntity;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Client interface for handling responses from external services.
 * It provides methods to get responses and handle errors.
 */
public interface Client {

    default <K, V> V getResponse(String error, Function<K, ResponseEntity<V>> consumer, K key) {
        ResponseEntity<V> responseEntity = consumer.apply(key);
        if (responseEntity.getBody() == null) {
            throw new ResourceInvalidException(error);
        }
        return responseEntity.getBody();
    }

    default <K1, K2, V> V getResponse(String error, BiFunction<K1, K2, ResponseEntity<V>> consumer, K1 key1, K2 key2) {
        ResponseEntity<V> responseEntity = consumer.apply(key1, key2);
        if (responseEntity.getBody() == null) {
            throw new ResourceInvalidException(error);
        }
        return responseEntity.getBody();
    }

}
