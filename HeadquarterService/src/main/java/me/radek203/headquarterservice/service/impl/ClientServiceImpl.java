package me.radek203.headquarterservice.service.impl;

import lombok.AllArgsConstructor;
import me.radek203.headquarterservice.entity.Client;
import me.radek203.headquarterservice.entity.ClientStatus;
import me.radek203.headquarterservice.exception.ResourceNotFoundException;
import me.radek203.headquarterservice.repository.ClientRepository;
import me.radek203.headquarterservice.service.ClientService;
import me.radek203.headquarterservice.service.KafkaSenderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final KafkaSenderService kafkaSenderService;

    @Override
    public void createClient(Client client) {
        Optional<Client> clientFound = clientRepository.findById(client.getId());
        if (clientFound.isPresent() && clientFound.get().getStatus() == ClientStatus.ACTIVE) {
            return;
        }

        client.setStatus(ClientStatus.ACTIVE);
        clientRepository.save(client);
        kafkaSenderService.sendMessage("branch-" + client.getBranch() + "-client-create-active", String.valueOf(client.getId()), client.getId());
    }

    @Override
    public Client getClientByAccountNumber(String accountNumber) {
        return clientRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new ResourceNotFoundException("error/account-not-found", accountNumber));
    }

    @Override
    public Client getClientById(UUID id) {
        return clientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("error/account-not-found", String.valueOf(id)));
    }

    @Override
    public List<Client> getClientsByUserId(int userId) {
        return clientRepository.findByUserId(userId);
    }

    @Override
    public List<String> getAccountNumbersByUserId(int userId) {
        return clientRepository.findByUserId(userId).stream().map(Client::getAccountNumber).toList();
    }
}
