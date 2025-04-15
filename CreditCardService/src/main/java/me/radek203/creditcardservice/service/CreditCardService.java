package me.radek203.creditcardservice.service;

import me.radek203.creditcardservice.entity.Bank;
import me.radek203.creditcardservice.entity.CreditCard;
import me.radek203.creditcardservice.entity.Transfer;

import java.util.List;
import java.util.UUID;

public interface CreditCardService {

    /**
     * Retrieves a list of CreditCards associated with a specific user.
     *
     * @param userId the ID of the user
     * @return a list of CreditCards associated with the user
     */
    List<CreditCard> getCreditCards(int userId);

    /**
     * Creates a new Bank entity.
     *
     * @param bank the Bank entity to be created
     * @return the created Bank entity
     */
    Bank createBank(Bank bank);

    /**
     * Creates a new CreditCard for a specific bank and account.
     *
     * @param bank    the ID of the bank
     * @param account the account number associated with the CreditCard
     * @return the created CreditCard
     */
    CreditCard createCreditCard(int bank, String account);

    /**
     * Deletes a CreditCard based on its number.
     *
     * @param cardNumber the number of the CreditCard to be deleted
     */
    void deleteCreditCard(String cardNumber);

    /**
     * Makes a payment using the specified CreditCard details.
     *
     * @param number  the number of the CreditCard
     * @param date    the expiration date of the CreditCard
     * @param cvv     the CVV code of the CreditCard
     * @param service the UUID of the service to which the payment is made
     * @param amount  the amount to be paid
     * @return a Transfer object representing the payment transaction
     */
    Transfer makePayment(String number, String date, String cvv, UUID service, double amount);

}
