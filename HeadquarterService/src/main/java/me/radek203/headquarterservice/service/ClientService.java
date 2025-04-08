package me.radek203.headquarterservice.service;

import me.radek203.headquarterservice.entity.Client;

import java.util.List;
import java.util.UUID;

public interface ClientService {

    void createClient(Client client);

    Client getClientByAccountNumber(String accountNumber);

    Client getClientById(UUID id);

    List<Client> getClientsByUserId(int userId);

}
