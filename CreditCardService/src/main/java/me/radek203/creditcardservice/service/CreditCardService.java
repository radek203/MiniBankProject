package me.radek203.creditcardservice.service;

import me.radek203.creditcardservice.entity.Bank;
import me.radek203.creditcardservice.entity.CreditCard;
import me.radek203.creditcardservice.entity.Transfer;

import java.util.UUID;

public interface CreditCardService {

    Bank createBank(Bank bank);

    CreditCard createCreditCard(int bank, String account);

    void deleteCreditCard(String cardNumber);

    Transfer makePayment(String number, String date, String cvv, UUID service, double amount);

}
