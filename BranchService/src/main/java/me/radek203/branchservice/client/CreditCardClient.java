package me.radek203.branchservice.client;

import me.radek203.branchservice.entity.CreditCard;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * CreditCardClient is an interface that defines a client for interacting with the CreditCardService.
 * It provides methods to create a credit card for a specific bank and account.
 */
@HttpExchange
public interface CreditCardClient extends Client {

    @PostExchange("/creditcard/create/{bank}/{account}")
    ResponseEntity<CreditCard> createCreditCard(@PathVariable int bank, @PathVariable String account);

    @DeleteExchange("/creditcard/delete/{number}/{userId}")
    ResponseEntity<Void> deleteCreditCard(@PathVariable String number, @PathVariable int userId);

}
