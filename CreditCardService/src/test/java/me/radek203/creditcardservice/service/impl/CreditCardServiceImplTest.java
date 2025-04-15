package me.radek203.creditcardservice.service.impl;

import me.radek203.creditcardservice.client.HeadquarterClient;
import me.radek203.creditcardservice.entity.Bank;
import me.radek203.creditcardservice.entity.CreditCard;
import me.radek203.creditcardservice.entity.Transfer;
import me.radek203.creditcardservice.exception.ClientException;
import me.radek203.creditcardservice.exception.ErrorDetails;
import me.radek203.creditcardservice.exception.ResourceInvalidException;
import me.radek203.creditcardservice.exception.ResourceNotFoundException;
import me.radek203.creditcardservice.repository.BankRepository;
import me.radek203.creditcardservice.repository.CreditCardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditCardServiceImplTest {

    @Mock
    private HeadquarterClient headquarterClient;
    @Mock
    private BankRepository bankRepository;
    @Mock
    private CreditCardRepository creditCardRepository;
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CreditCardServiceImpl creditCardService;

    @Test
    void shouldReturnCreditCards_whenAccountsFound() {
        int userId = 1;
        List<String> accountNumbers = List.of("123", "456");
        List<CreditCard> cards = List.of(
                new CreditCard("1111222233334444", "123", "12/25", "123", new Bank())
        );

        Mockito.when(headquarterClient.getResponse(
                        Mockito.eq("error/accounts-not-found"),
                        Mockito.any(),
                        Mockito.eq(userId)))
                .thenReturn(accountNumbers);
        Mockito.when(creditCardRepository.findAllByAccountNumberIn(accountNumbers))
                .thenReturn(cards);

        List<CreditCard> result = creditCardService.getCreditCards(userId);

        assertEquals(1, result.size());
        assertEquals("123", result.get(0).getAccountNumber());
    }

    @Test
    void shouldThrow_whenAccountsNotFound() {
        int userId = 1;

        Mockito.when(headquarterClient.getResponse(
                        Mockito.eq("error/accounts-not-found"),
                        Mockito.any(),
                        Mockito.eq(userId)))
                .thenThrow(new ClientException(new ErrorDetails(), HttpStatus.NOT_FOUND));

        assertThrows(ClientException.class, () -> creditCardService.getCreditCards(userId));
    }

    @Test
    void shouldCreateNewBank() {
        Bank bank = new Bank();
        bank.setName("Test Bank");
        bank.setUrl("http://test-bank.com");

        Bank saved = new Bank();
        saved.setBankId(1L);
        saved.setName("Test Bank");

        Mockito.when(bankRepository.save(any(Bank.class))).thenReturn(saved);

        Bank result = creditCardService.createBank(bank);

        assertNotNull(result.getBankId());
        assertEquals("Test Bank", result.getName());
    }

    @Test
    void shouldCreateCreditCardWithGeneratedData() {
        Bank bank = new Bank(1L, "Test", "http://test-bank");
        Mockito.when(bankRepository.findById(1L)).thenReturn(Optional.of(bank));
        Mockito.when(creditCardRepository.getAllNumbers()).thenReturn(List.of());
        Mockito.when(creditCardRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreditCard result = creditCardService.createCreditCard(1, "123456");

        assertEquals("123456", result.getAccountNumber());
        assertEquals(16, result.getCardNumber().length());
        assertEquals(3, result.getCvv().length());
        assertNotNull(result.getExpirationDate());
    }

    @Test
    void shouldThrowWhenBankNotFound() {
        Mockito.when(bankRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> creditCardService.createCreditCard(1, "acc"));
    }

    @Test
    void shouldDeleteExistingCard() {
        CreditCard card = new CreditCard("1111222233334444", "acc", "12/25", "123", new Bank());

        Mockito.when(creditCardRepository.findByCardNumber("1111222233334444"))
                .thenReturn(Optional.of(card));

        creditCardService.deleteCreditCard("1111222233334444");

        Mockito.verify(creditCardRepository).delete(card);
    }

    @Test
    void shouldThrowWhenCardToDeleteNotFound() {
        Mockito.when(creditCardRepository.findByCardNumber("notfound")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> creditCardService.deleteCreditCard("notfound"));
    }

    @Test
    void shouldMakePaymentWhenCardValid() {
        String cardNumber = "1234567890123456";
        String cvv = "123";
        String expirationDate = LocalDate.now().plusYears(1).format(DateTimeFormatter.ofPattern("MM/yy"));
        String formattedInputDate = expirationDate.replace("/", "");
        String accountNumber = "9876543210";
        UUID serviceId = UUID.randomUUID();
        double amount = 100.0;

        Bank bank = new Bank();
        bank.setBankId(1L);
        bank.setName("Test Bank");
        bank.setUrl("http://testbank.com");

        CreditCard creditCard = new CreditCard(cardNumber, accountNumber, expirationDate, cvv, bank);

        when(creditCardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(creditCard));

        Transfer expectedTransfer = new Transfer(
                UUID.randomUUID(), accountNumber, "TO_ACCOUNT", amount, "SUCCESS", 1, 2, System.currentTimeMillis()
        );

        ResponseEntity<Transfer> mockResponse = new ResponseEntity<>(expectedTransfer, HttpStatus.OK);

        String expectedUrl = bank.getUrl() + "/transfer/payment/" + accountNumber + "/" + serviceId + "/" + amount;

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(Transfer.class)
        )).thenReturn(mockResponse);

        Transfer result = creditCardService.makePayment(cardNumber, formattedInputDate, cvv, serviceId, amount);

        assertNotNull(result);
        assertEquals(expectedTransfer.getStatus(), result.getStatus());
        assertEquals(expectedTransfer.getAmount(), result.getAmount());
        assertEquals(expectedTransfer.getFromAccount(), result.getFromAccount());
    }

    @Test
    void shouldThrowInvalidCvv() {
        CreditCard card = new CreditCard("1234", "acc", "12/25", "000", new Bank());

        Mockito.when(creditCardRepository.findByCardNumber("1234")).thenReturn(Optional.of(card));

        assertThrows(ResourceInvalidException.class, () ->
                creditCardService.makePayment("1234", "1225", "123", UUID.randomUUID(), 10.0));
    }

    @Test
    void shouldThrowInvalidDate() {
        CreditCard card = new CreditCard("1234", "acc", "12/25", "123", new Bank());

        Mockito.when(creditCardRepository.findByCardNumber("1234")).thenReturn(Optional.of(card));

        assertThrows(ResourceInvalidException.class, () ->
                creditCardService.makePayment("1234", "1224", "123", UUID.randomUUID(), 10.0));
    }

    @Test
    void shouldThrowWhenCardExpired() {
        CreditCard card = new CreditCard("1234", "acc", "01/22", "123", new Bank());

        Mockito.when(creditCardRepository.findByCardNumber("1234")).thenReturn(Optional.of(card));

        assertThrows(ResourceInvalidException.class, () ->
                creditCardService.makePayment("1234", "0122", "123", UUID.randomUUID(), 10.0));
    }

    @Test
    void shouldThrowWhenRestTemplateFails() {
        CreditCard card = new CreditCard("1234", "acc", "12/99", "123", new Bank(1L, "bank", "http://invalid-url"));

        Mockito.when(creditCardRepository.findByCardNumber("1234")).thenReturn(Optional.of(card));

        Mockito.when(restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.eq(Transfer.class)
        )).thenThrow(new RuntimeException("Connection error"));

        assertThrows(ResourceInvalidException.class, () ->
                creditCardService.makePayment("1234", "1299", "123", UUID.randomUUID(), 10.0));
    }
}
