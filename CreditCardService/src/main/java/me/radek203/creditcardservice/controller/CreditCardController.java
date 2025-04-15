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
    public ResponseEntity<List<CreditCard>> getCreditCards(@PathVariable int userId) {
        return ResponseEntity.ok(creditCardService.getCreditCards(userId));
    }

    @PostMapping("/create")
    public ResponseEntity<Bank> createBank(@RequestBody Bank bank) {
        return ResponseEntity.ok(creditCardService.createBank(bank));
    }

    @PostMapping("/create/{bank}/{account}")
    public ResponseEntity<CreditCard> createCreditCard(@PathVariable int bank, @PathVariable String account) {
        return ResponseEntity.ok(creditCardService.createCreditCard(bank, account));
    }

    @DeleteMapping("/delete/{number}")
    public ResponseEntity<Void> deleteCreditCard(@PathVariable String number) {
        creditCardService.deleteCreditCard(number);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/pay/{number}/{date}/{cvv}/{service}/{amount}")
    public ResponseEntity<Transfer> pay(@PathVariable String number, @PathVariable String date, @PathVariable String cvv, @PathVariable UUID service, @PathVariable double amount) {
        return ResponseEntity.ok(creditCardService.makePayment(number, date, cvv, service, amount));
    }

}
