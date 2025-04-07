package me.radek203.branchservice.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.UUID;

@HttpExchange
public interface HeadquarterClient {

    @GetExchange("/client/{account}/branch")
    ResponseEntity<Integer> getBranchId(@PathVariable String account);

    @GetExchange("/client/{id}/account")
    ResponseEntity<String> getAccountNumber(@PathVariable UUID id);

}
