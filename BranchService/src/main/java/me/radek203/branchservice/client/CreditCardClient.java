package me.radek203.branchservice.client;

import me.radek203.branchservice.entity.CreditCard;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface CreditCardClient extends Client {

    @PostExchange("/creditcard/create/{bank}/{account}")
    ResponseEntity<CreditCard> createCreditCard(@PathVariable int bank, @PathVariable String account);

}
