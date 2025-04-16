package me.radek203.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    /**
     * This configuration class sets up the routes for the API Gateway.
     * It defines how incoming requests are routed to different services.
     * It does not work when it is configured in the application.yaml file.
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/auth/**")
                        .uri("lb://auth-service"))
                .route(r -> r.path("/creditcard/**")
                        .uri("lb://credit-card-service"))
                .route(r -> r.path("/client/**")
                        .uri("lb://headquarter-service"))
                .route(r -> r.path("/krakow/transfer/**", "/krakow/account/**")
                        .filters(f -> f
                                .rewritePath("/krakow/transfer/(?<segment>.*)", "/transfer/${segment}")
                                .rewritePath("/krakow/account/(?<segment>.*)", "/account/${segment}")
                        )
                        .uri("lb://branch-service-1"))
                .route(r -> r.path("/warsaw/transfer/**", "/warsaw/account/**")
                        .filters(f -> f
                                .rewritePath("/warsaw/transfer/(?<segment>.*)", "/transfer/${segment}")
                                .rewritePath("/warsaw/account/(?<segment>.*)", "/account/${segment}")
                        )
                        .uri("lb://branch-service-2"))
                .build();
    }

}
