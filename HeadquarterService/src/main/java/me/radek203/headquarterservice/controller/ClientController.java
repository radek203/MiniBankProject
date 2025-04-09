package me.radek203.headquarterservice.controller;

import lombok.AllArgsConstructor;
import me.radek203.headquarterservice.entity.Client;
import me.radek203.headquarterservice.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/{account}/branch")
    ResponseEntity<Integer> getBranchId(@PathVariable String account) {
        return ResponseEntity.ok(clientService.getClientByAccountNumber(account).getBranch());
    }

    @GetMapping("/{id}/account")
    ResponseEntity<String> getAccountNumber(@PathVariable UUID id) {
        return ResponseEntity.ok(clientService.getClientById(id).getAccountNumber());
    }

    @GetMapping("/{id}")
    ResponseEntity<List<Client>> getAccounts(@PathVariable int id) {
        return ResponseEntity.ok(clientService.getClientsByUserId(id));
    }

    @GetMapping("/{id}/accounts")
    ResponseEntity<List<String>> getAccountsByUserId(@PathVariable int id) {
        return ResponseEntity.ok(clientService.getClientsByUserId(id).stream().map(Client::getAccountNumber).toList());
    }

}
