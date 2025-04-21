package me.radek203.branchservice.controller;

import lombok.AllArgsConstructor;
import me.radek203.branchservice.entity.BalanceChange;
import me.radek203.branchservice.entity.Transfer;
import me.radek203.branchservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * PaymentController is a REST controller that handles payment-related requests.
 * It provides endpoints for making transfers, deposits, withdrawals, and retrieving transfer and balance information.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/transfer")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{from}/{to}/{amount}")
    public ResponseEntity<Transfer> makeTransfer(@PathVariable String from, @PathVariable String to, @PathVariable double amount, @RequestHeader("X-UserId") int userId) {
        return ResponseEntity.ok(paymentService.makeTransfer(from, to, amount, userId));
    }

    @GetMapping("/transfer/{account}/{id}")
    public ResponseEntity<Transfer> getTransfer(@PathVariable String account, @PathVariable UUID id, @RequestHeader("X-UserId") int userId) {
        return ResponseEntity.ok(paymentService.getTransfer(account, id, userId));
    }

    @GetMapping("/transfer/all/{account}")
    public ResponseEntity<List<Transfer>> getTransfersByAccount(@PathVariable String account, @RequestHeader("X-UserId") int userId) {
        return ResponseEntity.ok(paymentService.getTransfersByAccount(account, userId));
    }

    @GetMapping("/payment/{from}/{to}/{amount}")
    public ResponseEntity<Transfer> makePaymentTransfer(@PathVariable String from, @PathVariable UUID to, @PathVariable double amount, @RequestHeader("X-Internal-Auth") String internalAuth) {
        return ResponseEntity.ok(paymentService.makePaymentTransfer(from, to, amount));
    }

    @GetMapping("/balance/{account}/{id}")
    public ResponseEntity<BalanceChange> getBalance(@PathVariable String account, @PathVariable UUID id, @RequestHeader("X-UserId") int userId) {
        return ResponseEntity.ok(paymentService.getBalanceChange(account, id, userId));
    }

    @GetMapping("/balance/all/{account}")
    public ResponseEntity<List<BalanceChange>> getBalanceChangesByAccount(@PathVariable String account, @RequestHeader("X-UserId") int userId) {
        return ResponseEntity.ok(paymentService.getBalanceChanges(account, userId));
    }

    @GetMapping("/deposit/{account}/{amount}")
    public ResponseEntity<BalanceChange> deposit(@PathVariable String account, @PathVariable double amount, @RequestHeader("X-UserId") int userId) {
        return ResponseEntity.ok(paymentService.makeDeposit(account, amount, userId));
    }

    @GetMapping("/withdraw/{account}/{amount}")
    public ResponseEntity<BalanceChange> withdraw(@PathVariable String account, @PathVariable double amount, @RequestHeader("X-UserId") int userId) {
        return ResponseEntity.ok(paymentService.makeWithdraw(account, amount, userId));
    }

}
