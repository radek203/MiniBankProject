package me.radek203.branchservice.controller;

import lombok.AllArgsConstructor;
import me.radek203.branchservice.entity.BalanceChange;
import me.radek203.branchservice.entity.Transfer;
import me.radek203.branchservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/transfer")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{from}/{to}/{amount}")
    public ResponseEntity<Transfer> makeTransfer(@PathVariable String from, @PathVariable String to, @PathVariable double amount) {
        return ResponseEntity.ok(paymentService.makeTransfer(from, to, "", amount));
    }

    @GetMapping("/transfer/{id}")
    public ResponseEntity<Transfer> getTransfer(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getTransfer(id));
    }

    @GetMapping("/payment/{from}/{to}/{amount}")
    public ResponseEntity<Transfer> makePaymentTransfer(@PathVariable String from, @PathVariable UUID to, @PathVariable double amount) {
        return ResponseEntity.ok(paymentService.makePaymentTransfer(from, to, "", amount));
    }

    @GetMapping("/balance/{id}")
    public ResponseEntity<BalanceChange> getBalance(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getBalanceChange(id));
    }

    @GetMapping("/deposit/{account}/{amount}")
    public ResponseEntity<BalanceChange> deposit(@PathVariable String account, @PathVariable double amount) {
        return ResponseEntity.ok(paymentService.makeDeposit(account, amount));
    }

    @GetMapping("/withdraw/{account}/{amount}")
    public ResponseEntity<BalanceChange> withdraw(@PathVariable String account, @PathVariable double amount) {
        return ResponseEntity.ok(paymentService.makeWithdraw(account, amount));
    }

}
