package me.radek203.apigateway.filter;

import lombok.AllArgsConstructor;
import me.radek203.apigateway.client.AuthClient;
import me.radek203.apigateway.entity.JWTAuthentication;
import me.radek203.apigateway.exception.ResourceInvalidException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;

@Component
@AllArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private RouteValidator routeValidator;
    private AuthClient authClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }
        if (routeValidator.isSecured().test(exchange.getRequest())) {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new ResourceInvalidException("Missing Authorization Header");
            }
            String authHeader = exchange.getRequest().getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).getFirst();
            if (!authHeader.startsWith("Bearer ") || authHeader.length() < 8) {
                throw new ResourceInvalidException("Invalid Authorization Header");
            }
            String jwt = authHeader.substring(7);
            return authClient.validateToken(new JWTAuthentication(jwt))
                    .flatMap(response -> {
                        ServerHttpRequest mutatedRequest = exchange.getRequest()
                                .mutate()
                                .headers(httpHeaders -> {
                                    httpHeaders.remove("X-UserId");
                                    httpHeaders.remove("X-Username");
                                    httpHeaders.remove("X-Role");
                                    httpHeaders.remove("X-Email");
                                })
                                .header("X-UserId", String.valueOf(response.getId()))
                                .header("X-Username", response.getUsername())
                                .header("X-Role", response.getRole().name())
                                .header("X-Email", response.getEmail())
                                .build();

                        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
                        return chain.filter(mutatedExchange);
                    })
                    .onErrorResume(e -> {
                        if (e instanceof ConnectException) {
                            return Mono.error(e);
                        }
                        return Mono.error(new ResourceInvalidException("Invalid JWT token"));
                    });
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

}
