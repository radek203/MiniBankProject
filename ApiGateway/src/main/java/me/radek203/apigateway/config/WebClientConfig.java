package me.radek203.apigateway.config;

import lombok.AllArgsConstructor;
import me.radek203.apigateway.client.AuthClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Configuration class for WebClient.
 * This class is responsible for creating and configuring the WebClient bean.
 * It also creates an AuthClient bean that can be used to make requests to the auth service.
 */
@AllArgsConstructor
@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient authWebClient() {
        return webClientBuilder().baseUrl("http://auth-service").build();
    }

    @Bean
    public AuthClient authClient() {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder().exchangeAdapter(WebClientAdapter.create(authWebClient())).build();
        return factory.createClient(AuthClient.class);
    }

}
