package me.radek203.branchservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import me.radek203.branchservice.entity.Client;
import me.radek203.branchservice.entity.CreditCard;
import me.radek203.branchservice.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * ClientController is a REST controller that handles client-related requests.
 * It provides endpoints for creating clients and credit cards.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/account")
public class ClientController {

    private final ClientService clientService;

    @PostMapping("/create")
    public ResponseEntity<Client> create(@Valid @RequestBody Client client, @RequestHeader("X-UserId") int userId) {
        client.setUserId(userId);
        return ResponseEntity.ok(clientService.saveClient(client));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClient(@PathVariable UUID id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Client> update(@Valid @RequestBody Client client, @PathVariable UUID id, @RequestHeader("X-UserId") int userId) {
        client.setUserId(userId);
        return ResponseEntity.ok(clientService.updateClient(id, client));
    }

    @PostMapping("/create/card/{account}")
    public ResponseEntity<CreditCard> createCreditCard(@PathVariable String account) {
        return ResponseEntity.ok(clientService.orderCreditCard(account));
    }

}
