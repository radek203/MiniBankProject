package me.radek203.branchservice.controller;

import lombok.AllArgsConstructor;
import me.radek203.branchservice.entity.Transfer;
import me.radek203.branchservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@AllArgsConstructor
@RestController
public class PaymentController {

    private PaymentService paymentService;

    @GetMapping("/transfer")
    public ResponseEntity<Transfer> makeTransfer() {
        Random random = new Random();
        return ResponseEntity.ok(paymentService.makeTransfer("account" + random.nextInt(1000), "account" + random.nextInt(1000), random.nextDouble() * 1000));
    }

}
