package me.radek203.branchservice.service.impl;

import lombok.AllArgsConstructor;
import me.radek203.branchservice.entity.Client;
import me.radek203.branchservice.repository.ClientRepository;
import me.radek203.branchservice.service.ClientService;
import me.radek203.branchservice.service.KafkaSenderService;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    private final KafkaSenderService kafkaSenderService;
    private final ClientRepository clientRepository;

    @Override
    public Client saveClient(Client client) {
        Client clientSaved = clientRepository.save(client);
        kafkaSenderService.sendMessage("headquarter-client-create", String.valueOf(clientSaved.getId()), clientSaved);
        return clientSaved;
    }
}
