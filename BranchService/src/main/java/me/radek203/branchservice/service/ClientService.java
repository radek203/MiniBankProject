package me.radek203.branchservice.service;

import me.radek203.branchservice.entity.Client;
import me.radek203.branchservice.entity.CreditCard;

public interface ClientService {

    Client saveClient(Client client);

    CreditCard orderCreditCard(String accountNumber);

}
