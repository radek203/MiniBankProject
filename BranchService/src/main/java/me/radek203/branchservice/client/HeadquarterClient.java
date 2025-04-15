package me.radek203.branchservice.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.UUID;

/**
 * HeadquarterClient is an interface that defines methods for interacting with the headquarter service.
 * It uses Spring's WebClient to make HTTP requests to the headquarter service endpoints.
 */
@HttpExchange
public interface HeadquarterClient extends Client {

    @GetExchange("/client/{account}/branch")
    ResponseEntity<Integer> getBranchId(@PathVariable String account);

    @GetExchange("/client/{id}/account")
    ResponseEntity<String> getAccountNumber(@PathVariable UUID id);

}
