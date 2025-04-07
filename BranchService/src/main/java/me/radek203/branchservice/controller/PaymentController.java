package me.radek203.branchservice.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.radek203.branchservice.entity.BalanceChange;
import me.radek203.branchservice.entity.Transfer;
import me.radek203.branchservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@AllArgsConstructor
@RestController
public class PaymentController {

    private PaymentService paymentService;

    @GetMapping("/transfer/{from}/{to}/{amount}")
    public ResponseEntity<Transfer> makeTransfer(@PathVariable String from, @PathVariable String to, @PathVariable double amount) {
        return ResponseEntity.ok(paymentService.makeTransfer(from, to, "", amount));
    }

    @GetMapping("/paymenttransfer/{from}/{to}/{amount}")
    public ResponseEntity<Transfer> makeFastTransfer(@PathVariable String from, @PathVariable UUID to, @PathVariable double amount) {
        return ResponseEntity.ok(paymentService.makePaymentTransfer(from, to, "", amount));
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
