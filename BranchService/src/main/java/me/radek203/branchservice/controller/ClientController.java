package me.radek203.branchservice.controller;

import lombok.AllArgsConstructor;
import me.radek203.branchservice.entity.Client;
import me.radek203.branchservice.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class ClientController {

    private ClientService clientService;

    @PostMapping("/create")
    public ResponseEntity<Client> create(@Validated @RequestBody Client client) {
        return ResponseEntity.ok(clientService.saveClient(client));
    }

}
