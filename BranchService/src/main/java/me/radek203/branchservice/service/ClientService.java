package me.radek203.branchservice.service;

import me.radek203.branchservice.entity.Client;
import me.radek203.branchservice.entity.CreditCard;

import java.util.UUID;

public interface ClientService {

    /**
     * Saves the given client to the database.
     *
     * @param client the client to save
     * @return the saved client with a generated account number
     */
    Client saveClient(Client client);

    /**
     * Updates the given client in the database.
     *
     * @param id     the unique identifier of the client to update
     * @param client the client to update
     * @return the updated client
     */
    Client updateClient(UUID id, Client client);

    /**
     * Retrieves a client by their unique identifier.
     *
     * @param id     the unique identifier of the client
     * @param userId the user identifier of the client
     * @return the client with the specified identifier
     */
    Client getClientById(UUID id, int userId);

    /**
     * Orders a credit card for the specified account number.
     *
     * @param accountNumber the account number for which to order the credit card
     * @param userId        the user identifier of the client
     * @return the ordered credit card
     */
    CreditCard orderCreditCard(String accountNumber, int userId);

    /**
     * Deletes a credit card by their number
     *
     * @param number the credit card number
     * @param userId the user identifier of the client
     */
    void deleteCreditCard(String number, int userId);

    /**
     * Handles the completion of a client creation process.
     *
     * @param id the unique identifier of the client
     */
    void completedClient(UUID id);

    /**
     * Handles the failure of a client creation process.
     *
     * @param id the unique identifier of the client
     */
    void failedClient(UUID id);

}
