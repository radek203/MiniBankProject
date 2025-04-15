package me.radek203.branchservice.controller;

import lombok.AllArgsConstructor;
import me.radek203.branchservice.entity.Client;
import me.radek203.branchservice.entity.CreditCard;
import me.radek203.branchservice.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/account")
public class ClientController {

    private final ClientService clientService;

    @PostMapping("/create")
    public ResponseEntity<Client> create(@Validated @RequestBody Client client, @RequestHeader("X-UserId") int userId) {
        client.setUserId(userId);
        return ResponseEntity.ok(clientService.saveClient(client));
    }

    @PostMapping("/create/card/{account}")
    public ResponseEntity<CreditCard> createCreditCard(@PathVariable String account) {
        return ResponseEntity.ok(clientService.orderCreditCard(account));
    }

}
