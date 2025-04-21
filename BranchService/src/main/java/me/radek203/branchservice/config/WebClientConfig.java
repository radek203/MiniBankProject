package me.radek203.branchservice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import me.radek203.branchservice.client.CreditCardClient;
import me.radek203.branchservice.client.HeadquarterClient;
import me.radek203.branchservice.exception.ClientException;
import me.radek203.branchservice.exception.ErrorDetails;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * WebClientConfig is a configuration class that sets up WebClient instances for making HTTP requests
 * to external services. It also provides error handling for the responses.
 */
@AllArgsConstructor
@Configuration
public class WebClientConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient hqWebClient() {
        return webClientBuilder().baseUrl("http://headquarter-service").filter(internalAuthFilter()).filter(errorHandlingFilter()).build();
    }

    @Bean
    public HeadquarterClient hqClient() {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder().exchangeAdapter(WebClientAdapter.create(hqWebClient())).build();
        return factory.createClient(HeadquarterClient.class);
    }

    @Bean
    public WebClient creditCardWebClient() {
        return webClientBuilder().baseUrl("http://credit-card-service").filter(internalAuthFilter()).filter(errorHandlingFilter()).build();
    }

    @Bean
    public CreditCardClient creditCardClient() {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder().exchangeAdapter(WebClientAdapter.create(creditCardWebClient())).build();
        return factory.createClient(CreditCardClient.class);
    }

    private ExchangeFilterFunction internalAuthFilter() {
        return (request, next) -> {
            ClientRequest authenticatedRequest = ClientRequest.from(request)
                    .header("X-Internal-Auth", "branch-service")
                    .build();
            return next.exchange(authenticatedRequest);
        };
    }

    private ExchangeFilterFunction errorHandlingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                return clientResponse
                        .bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            ErrorDetails errorDetails;
                            try {
                                errorDetails = objectMapper.readValue(errorBody, ErrorDetails.class);
                            } catch (JsonProcessingException e) {
                                errorDetails = new ErrorDetails(LocalDateTime.now(), "error/server-error", "", "", "");
                            }
                            return Mono.error(new ClientException(errorDetails, clientResponse.statusCode()));
                        });
            }
            return Mono.just(clientResponse);
        });
    }

}
