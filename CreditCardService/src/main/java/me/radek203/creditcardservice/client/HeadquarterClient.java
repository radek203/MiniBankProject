package me.radek203.creditcardservice.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;
import java.util.UUID;

@HttpExchange
public interface HeadquarterClient {

    @GetExchange("client/{id}/accounts")
    ResponseEntity<List<String>> getAccountsByUserId(@PathVariable int id);

}
