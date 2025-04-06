package me.radek203.headquarterservice.service.impl;

import lombok.AllArgsConstructor;
import me.radek203.headquarterservice.entity.Client;
import me.radek203.headquarterservice.entity.ClientStatus;
import me.radek203.headquarterservice.repository.ClientRepository;
import me.radek203.headquarterservice.service.ClientService;
import me.radek203.headquarterservice.service.KafkaSenderService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private KafkaSenderService kafkaSenderService;

    @Override
    public void createClient(Client client) {
        Optional<Client> clientFound = clientRepository.findById(client.getId());
        if (clientFound.isPresent() && clientFound.get().getStatus() == ClientStatus.ACTIVE) {
            return;
        }

        client.setStatus(ClientStatus.ACTIVE);
        clientRepository.save(client);
        kafkaSenderService.sendMessage("branch-" + client.getBranch() + "-client-create-active", String.valueOf(client.getId()), client);
    }
}
