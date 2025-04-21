package me.radek203.headquarterservice.service;

import me.radek203.headquarterservice.entity.Client;

import java.util.List;
import java.util.UUID;

public interface ClientService {

    /**
     * Saves the given client to the database.
     *
     * @param client the client to save
     */
    void createClient(Client client);

    /**
     * Get a client by account number.
     *
     * @param accountNumber the account number of the client
     * @return the client with the specified account number
     */
    Client getClientByAccountNumber(String accountNumber);

    /**
     * Get a client by ID.
     *
     * @param id the ID of the client
     * @return the client with the specified ID
     */
    Client getClientById(UUID id);

    /**
     * Get all clients by user ID.
     *
     * @param userId       the ID of the user
     * @param userIdHeader the ID of the user from the header
     * @return a list of clients associated with the specified user ID
     */
    List<Client> getClientsByUserId(int userId, int userIdHeader);

    /**
     * Get all account numbers by user ID.
     *
     * @param userId the ID of the user
     * @return a list of account numbers associated with the specified user ID
     */
    List<String> getAccountNumbersByUserId(int userId);

}
