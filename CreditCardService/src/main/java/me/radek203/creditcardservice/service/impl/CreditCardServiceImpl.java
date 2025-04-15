package me.radek203.creditcardservice.service.impl;

import lombok.AllArgsConstructor;
import me.radek203.creditcardservice.client.HeadquarterClient;
import me.radek203.creditcardservice.entity.Bank;
import me.radek203.creditcardservice.entity.CreditCard;
import me.radek203.creditcardservice.entity.Transfer;
import me.radek203.creditcardservice.exception.ResourceInvalidException;
import me.radek203.creditcardservice.exception.ResourceNotFoundException;
import me.radek203.creditcardservice.repository.BankRepository;
import me.radek203.creditcardservice.repository.CreditCardRepository;
import me.radek203.creditcardservice.service.CreditCardService;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CreditCardServiceImpl implements CreditCardService {

    private final HeadquarterClient hqClient;
    private final BankRepository bankRepository;
    private final CreditCardRepository creditCardRepository;
    private final RestTemplate restTemplate;

    private String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String getRandomAccountNumber() {
        List<String> numbers = creditCardRepository.getAllNumbers();
        String number;
        do {
            number = generateRandomString(16);
        } while (numbers.contains(number));
        return number;
    }

    private String getRandomCvvNumber() {
        return generateRandomString(3);
    }

    @Override
    public List<CreditCard> getCreditCards(int userId) {
        List<String> accounts = hqClient.getResponse("error/accounts-not-found", hqClient::getAccountsByUserId, userId);
        return creditCardRepository.findAllByAccountNumberIn(accounts);
    }

    @Override
    public Bank createBank(Bank bank) {
        bank.setBankId(null);
        return bankRepository.save(bank);
    }

    @Override
    public CreditCard createCreditCard(int bankId, String account) {
        Bank bank = bankRepository.findById((long) bankId).orElseThrow(() -> new ResourceNotFoundException("error/bank-not-found"));

        LocalDate expirationDate = LocalDate.now().plusYears(2);
        String formattedExpiration = expirationDate.format(DateTimeFormatter.ofPattern("MM/yy"));

        CreditCard creditCard = new CreditCard(getRandomAccountNumber(), account, formattedExpiration, getRandomCvvNumber(), bank);
        creditCard = creditCardRepository.save(creditCard);
        return creditCard;
    }

    @Override
    public void deleteCreditCard(String cardNumber) {
        CreditCard creditCard = creditCardRepository.findByCardNumber(cardNumber).orElseThrow(() -> new ResourceNotFoundException("error/card-not-found"));
        creditCardRepository.delete(creditCard);
    }

    @Override
    public Transfer makePayment(String number, String date, String cvv, UUID service, double amount) {
        CreditCard creditCard = creditCardRepository.findByCardNumber(number).orElseThrow(() -> new ResourceNotFoundException("error/card-not-found"));
        if (!creditCard.getCvv().equals(cvv)) {
            throw new ResourceInvalidException("error/invalid-cvv");
        }
        if (!creditCard.getExpirationDate().replace("/", "").equals(date)) {
            throw new ResourceInvalidException("error/invalid-expiration-date");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth cardExpiration = YearMonth.parse(creditCard.getExpirationDate(), formatter);
        YearMonth current = YearMonth.now();

        if (cardExpiration.isBefore(current)) {
            throw new ResourceInvalidException("error/card-expired");
        }

        ResponseEntity<Transfer> response;
        try {
            response = restTemplate.exchange(creditCard.getBank().getUrl() + "/transfer/payment/" + creditCard.getAccountNumber() + "/" + service + "/" + amount, HttpMethod.GET, null, Transfer.class);
        } catch (Exception e) {
            throw new ResourceInvalidException("error/payment-failed");
        }

        return response.getBody();
    }

}
