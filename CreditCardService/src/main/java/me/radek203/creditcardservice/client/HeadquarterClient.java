package me.radek203.creditcardservice.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

/**
 * HeadquarterClient is an interface that defines a client for interacting with the headquarter service.
 * It extends the Client interface and provides methods to retrieve account information by user ID.
 */
@HttpExchange
public interface HeadquarterClient extends Client {

    @GetExchange("client/{id}/accounts")
    ResponseEntity<List<String>> getAccountsByUserId(@PathVariable int id);

}
