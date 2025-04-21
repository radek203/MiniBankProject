package me.radek203.creditcardservice.controller;

import lombok.AllArgsConstructor;
import me.radek203.creditcardservice.entity.Bank;
import me.radek203.creditcardservice.entity.CreditCard;
import me.radek203.creditcardservice.entity.Transfer;
import me.radek203.creditcardservice.service.CreditCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * CreditCardController is a REST controller that handles HTTP requests related to credit card operations.
 * It provides endpoints for creating, retrieving, deleting, and making payments with credit cards.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/creditcard")
public class CreditCardController {

    private final CreditCardService creditCardService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<CreditCard>> getCreditCards(@PathVariable int userId, @RequestHeader("X-UserId") int userIdHeader) {
        return ResponseEntity.ok(creditCardService.getCreditCards(userId, userIdHeader));
    }

    @PostMapping("/create")
    public ResponseEntity<Bank> createBank(@RequestBody Bank bank, @RequestHeader("X-Role") String userRole) {
        return ResponseEntity.ok(creditCardService.createBank(bank, userRole));
    }

    @PostMapping("/create/{bank}/{account}")
    public ResponseEntity<CreditCard> createCreditCard(@PathVariable int bank, @PathVariable String account, @RequestHeader("X-Internal-Auth") String internalAuth) {
        return ResponseEntity.ok(creditCardService.createCreditCard(bank, account));
    }

    @DeleteMapping("/delete/{number}/{userId}")
    public ResponseEntity<Void> deleteCreditCard(@PathVariable String number, @PathVariable int userId, @RequestHeader("X-Internal-Auth") String internalAuth) {
        creditCardService.deleteCreditCard(number, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/pay/{number}/{date}/{cvv}/{service}/{amount}")
    public ResponseEntity<Transfer> pay(@PathVariable String number, @PathVariable String date, @PathVariable String cvv, @PathVariable UUID service, @PathVariable double amount) {
        return ResponseEntity.ok(creditCardService.makePayment(number, date, cvv, service, amount));
    }

}
