package me.radek203.apigateway.client;

import me.radek203.apigateway.entity.JWTAuthentication;
import me.radek203.apigateway.entity.UserDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

/**
 * Client for authentication service.
 */
@HttpExchange
public interface AuthClient {

    @PostExchange("/auth/validate")
    Mono<UserDTO> validateToken(@RequestBody final JWTAuthentication authentication);

}
