package me.radek203.branchservice.config;

import lombok.AllArgsConstructor;
import me.radek203.branchservice.client.HeadquarterClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@AllArgsConstructor
@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced //Jak localhost to tego nie dajemy
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient hqWebClient() {
        return webClientBuilder().baseUrl("http://headquarter-service").build();
        //return webClientBuilder().baseUrl("http://localhost:8081").build();
    }

    @Bean
    public HeadquarterClient hqClient() {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder().exchangeAdapter(WebClientAdapter.create(hqWebClient())).build();
        return factory.createClient(HeadquarterClient.class);
    }

}
