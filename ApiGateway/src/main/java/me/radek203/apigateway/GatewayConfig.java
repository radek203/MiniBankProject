package me.radek203.apigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/auth/**")
                        .uri("lb://auth-service"))
                .route(r -> r.path("/client/**")
                        .uri("lb://headquarter-service"))
                .route(r -> r.path("/krakow/transfer/**")
                        .filters(f -> f.rewritePath("/krakow/transfer/(?<segment>.*)", "/transfer/${segment}"))
                        .uri("http://localhost:8082"))
                .route(r -> r.path("/warsaw/transfer/**")
                        .filters(f -> f.rewritePath("/warsaw/transfer/(?<segment>.*)", "/transfer/${segment}"))
                        .uri("http://localhost:8083"))
                .build();
    }

}
