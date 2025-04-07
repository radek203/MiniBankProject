package me.radek203.headquarterservice.controller;

import lombok.AllArgsConstructor;
import me.radek203.headquarterservice.entity.Client;
import me.radek203.headquarterservice.exception.ResourceNotFoundException;
import me.radek203.headquarterservice.repository.ClientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/client")
public class ClientController {

    private final ClientRepository clientRepository;

    @GetMapping("/{account}/branch")
    ResponseEntity<Integer> getBranchId(@PathVariable String account) {
        Client client = clientRepository.findByAccountNumber(account).orElseThrow(() -> new ResourceNotFoundException("error/account-not-found", account));
        return ResponseEntity.ok(client.getBranch());
    }

    @GetMapping("/{id}/account")
    ResponseEntity<String> getAccountNumber(@PathVariable UUID id) {
        Client client = clientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("error/account-not-found", String.valueOf(id)));
        return ResponseEntity.ok(client.getAccountNumber());
    }

}
