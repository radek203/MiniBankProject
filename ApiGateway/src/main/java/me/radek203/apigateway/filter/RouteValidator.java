package me.radek203.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    private final List<String> allowedRoutes = List.of(
            "/auth/register",
            "/auth/login",
            "/auth/validate"
    );

    public Predicate<ServerHttpRequest> isSecured() {
        return request -> allowedRoutes
                .stream()
                .noneMatch(uri -> request.getURI().getPath().contains(uri));
    }

}
