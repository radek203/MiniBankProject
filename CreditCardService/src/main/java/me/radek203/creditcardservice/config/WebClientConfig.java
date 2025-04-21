package me.radek203.creditcardservice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import me.radek203.creditcardservice.client.HeadquarterClient;
import me.radek203.creditcardservice.exception.ClientException;
import me.radek203.creditcardservice.exception.ErrorDetails;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
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
    @LoadBalanced
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(internalAuthInterceptor());
        return restTemplate;
    }

    private ClientHttpRequestInterceptor internalAuthInterceptor() {
        return (request, body, execution) -> {
            request.getHeaders().add("X-Internal-Auth", "credit-card-service");
            return execution.execute(request, body);
        };
    }

    private ExchangeFilterFunction internalAuthFilter() {
        return (request, next) -> {
            ClientRequest authenticatedRequest = ClientRequest.from(request)
                    .header("X-Internal-Auth", "credit-card-service")
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
                            if (clientResponse.statusCode().isError()) {
                                ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), "error/server-error", "", "", "");
                                return Mono.error(new ClientException(errorDetails, clientResponse.statusCode()));
                            }
                            ErrorDetails errorDetails;
                            try {
                                errorDetails = objectMapper.readValue(errorBody, ErrorDetails.class);
                            } catch (JsonProcessingException e) {
                                return Mono.error(new RuntimeException(e));
                            }
                            return Mono.error(new ClientException(errorDetails, clientResponse.statusCode()));
                        });
            }
            return Mono.just(clientResponse);
        });
    }

}
