package me.radek203.branchservice.service.impl;

import me.radek203.branchservice.client.CreditCardClient;
import me.radek203.branchservice.config.AppProperties;
import me.radek203.branchservice.entity.Client;
import me.radek203.branchservice.entity.ClientStatus;
import me.radek203.branchservice.entity.CreditCard;
import me.radek203.branchservice.repository.ClientRepository;
import me.radek203.branchservice.service.KafkaSenderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private KafkaSenderService kafkaSenderService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AppProperties appProperties;

    @Mock
    private CreditCardClient creditCardClient;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    void shouldSaveClientWithRandomAccountNumberAndSendKafkaMessage() {
        Client client = new Client();
        client.setFirstName("Jan");
        client.setLastName("Kowalski");
        client.setUserId(42);
        client.setPhone("123456789");
        client.setAddress("ul. Polska 1");
        client.setCity("Kraków");

        List<String> existingAccountNumbers = List.of("11111111111111111111111111");
        Mockito.when(clientRepository.getAllAccountNumbers()).thenReturn(existingAccountNumbers);
        Mockito.when(appProperties.getBranchId()).thenReturn(1001);

        Mockito.when(clientRepository.save(Mockito.any(Client.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Client savedClient = clientService.saveClient(client);

        assertNotNull(savedClient.getAccountNumber());
        assertEquals(26, savedClient.getAccountNumber().length());
        assertEquals(ClientStatus.CREATING, savedClient.getStatus());
        assertEquals(0, savedClient.getBalance());
        assertEquals(0, savedClient.getBalanceReserved());
        assertEquals(1001, savedClient.getBranch());

        Mockito.verify(kafkaSenderService).sendMessage(
                eq("headquarter-client-create"),
                anyString(),
                eq(savedClient)
        );
    }

    @Test
    void shouldOrderCreditCardSuccessfully() {
        String accountNumber = "12345678901234567890123456";
        CreditCard expectedCard = new CreditCard("4111111111111111", accountNumber, "12/30", "123");
        Mockito.when(appProperties.getBranchId()).thenReturn(2002);
        Mockito.when(creditCardClient.getResponse(
                        eq("error/credit-card-creation"),
                        any(),
                        eq(2002),
                        eq(accountNumber)))
                .thenReturn(expectedCard);

        CreditCard result = clientService.orderCreditCard(accountNumber);

        assertEquals(expectedCard, result);
    }
}
