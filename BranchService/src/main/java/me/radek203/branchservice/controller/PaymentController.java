package me.radek203.branchservice.controller;

import lombok.AllArgsConstructor;
import me.radek203.branchservice.entity.Transfer;
import me.radek203.branchservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class PaymentController {

    private PaymentService paymentService;

    @GetMapping("/transfer/{from}/{to}/{amount}")
    public ResponseEntity<Transfer> makeTransfer(@PathVariable String from, @PathVariable String to, @PathVariable double amount) {
        return ResponseEntity.ok(paymentService.makeTransfer(from, to, "", amount));
    }

}
