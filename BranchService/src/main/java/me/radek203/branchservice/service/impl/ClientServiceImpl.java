package me.radek203.branchservice.service.impl;

import lombok.AllArgsConstructor;
import me.radek203.branchservice.entity.Client;
import me.radek203.branchservice.repository.ClientRepository;
import me.radek203.branchservice.service.ClientService;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }
}
