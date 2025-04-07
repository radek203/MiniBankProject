package me.radek203.creditcardservice.service.impl;

import lombok.AllArgsConstructor;
import me.radek203.creditcardservice.entity.Bank;
import me.radek203.creditcardservice.entity.CreditCard;
import me.radek203.creditcardservice.entity.Transfer;
import me.radek203.creditcardservice.repository.BankRepository;
import me.radek203.creditcardservice.repository.CreditCardRepository;
import me.radek203.creditcardservice.service.CreditCardService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CreditCardServiceImpl implements CreditCardService {

    private BankRepository bankRepository;
    private CreditCardRepository creditCardRepository;

    private String getRandomAccountNumber() {
        SecureRandom random = new SecureRandom();
        List<String> numbers = creditCardRepository.getAllNumbers();
        String number;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                sb.append(random.nextInt(10));
            }
            number = sb.toString();
        } while (numbers.contains(number));
        return number;
    }

    private String getRandomCvvNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    public Bank createBank(Bank bank) {
        bank.setBankId(null);
        return bankRepository.save(bank);
    }

    @Override
    public CreditCard createCreditCard(int bankId, String account) {
        Bank bank = bankRepository.findById((long) bankId).orElseThrow(() -> new RuntimeException("error/bank-not-found"));

        //TODO: Add real expiration date
        CreditCard creditCard = new CreditCard(getRandomAccountNumber(), account, "26/12", getRandomCvvNumber(), bank);
        creditCard = creditCardRepository.save(creditCard);
        return creditCard;
    }

    @Override
    public void deleteCreditCard(String cardNumber) {
        CreditCard creditCard = creditCardRepository.findByCardNumber(cardNumber).orElseThrow(() -> new RuntimeException("error/card-not-found"));
        creditCardRepository.delete(creditCard);
    }

    @Override
    public Transfer makePayment(String number, String date, String cvv, UUID service, double amount) {
        CreditCard creditCard = creditCardRepository.findByCardNumber(number).orElseThrow(() -> new RuntimeException("error/card-not-found"));
        if (!creditCard.getCvv().equals(cvv)) {
            throw new RuntimeException("error/invalid-cvv");
        }
        if (!creditCard.getExpirationDate().replace("/", "").equals(date)) {
            throw new RuntimeException("error/invalid-expiration-date");
        }
        //TODO: check if card is not expired

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Transfer> response = restTemplate.exchange(creditCard.getBank().getUrl() + "/paymenttransfer/" + creditCard.getAccountNumber() + "/" + service + "/" + amount, HttpMethod.GET, null, Transfer.class);

        return response.getBody();
    }

}
