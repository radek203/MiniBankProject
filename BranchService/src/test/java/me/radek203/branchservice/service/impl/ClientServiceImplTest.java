package me.radek203.branchservice.service.impl;

import me.radek203.branchservice.client.CreditCardClient;
import me.radek203.branchservice.config.AppProperties;
import me.radek203.branchservice.entity.Client;
import me.radek203.branchservice.entity.ClientStatus;
import me.radek203.branchservice.entity.CreditCard;
import me.radek203.branchservice.exception.ResourceNotFoundException;
import me.radek203.branchservice.repository.ClientRepository;
import me.radek203.branchservice.service.KafkaSenderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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
        when(clientRepository.getAllAccountNumbers()).thenReturn(existingAccountNumbers);
        when(appProperties.getBranchId()).thenReturn(1001);

        when(clientRepository.save(any(Client.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Client savedClient = clientService.saveClient(client);

        assertNotNull(savedClient.getAccountNumber());
        assertEquals(26, savedClient.getAccountNumber().length());
        assertEquals(ClientStatus.CREATING, savedClient.getStatus());
        assertEquals(0, savedClient.getBalance());
        assertEquals(0, savedClient.getBalanceReserved());
        assertEquals(1001, savedClient.getBranch());

        verify(kafkaSenderService).sendMessage(
                eq("headquarter-client-create"),
                anyString(),
                eq(savedClient)
        );
    }

    @Test
    void shouldOrderCreditCardSuccessfully() {
        String accountNumber = "12345678901234567890123456";
        CreditCard expectedCard = new CreditCard("4111111111111111", accountNumber, "12/30", "123");
        when(appProperties.getBranchId()).thenReturn(2002);
        when(creditCardClient.getResponse(
                eq("error/credit-card-creation"),
                any(),
                eq(2002),
                eq(accountNumber)))
                .thenReturn(expectedCard);

        Client client = new Client();
        client.setAccountNumber(accountNumber);
        client.setUserId(1);

        when(clientRepository.findByAccountNumberAndUserId(accountNumber, client.getUserId())).thenReturn(Optional.of(client));

        CreditCard result = clientService.orderCreditCard(accountNumber, client.getUserId());

        assertEquals(expectedCard, result);
    }

    @Test
    void shouldCompleteClientWhenClientExistsAndNotActive() {
        UUID clientId = UUID.randomUUID();
        Client client = new Client();
        client.setId(clientId);
        client.setStatus(ClientStatus.CREATING);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        clientService.completedClient(clientId);

        assertEquals(ClientStatus.ACTIVE, client.getStatus());
        verify(clientRepository).save(client);
    }

    @Test
    void shouldNotCompleteClientWhenClientDoesNotExist() {
        UUID clientId = UUID.randomUUID();
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        clientService.completedClient(clientId);

        verify(clientRepository, never()).save(any());
    }

    @Test
    void shouldNotCompleteClientWhenClientAlreadyActive() {
        UUID clientId = UUID.randomUUID();
        Client client = new Client();
        client.setId(clientId);
        client.setStatus(ClientStatus.ACTIVE);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        clientService.completedClient(clientId);

        verify(clientRepository, never()).save(any());
    }

    @Test
    void shouldDeleteClientWhenFailed() {
        UUID clientId = UUID.randomUUID();
        Client client = new Client();
        client.setId(clientId);
        client.setStatus(ClientStatus.CREATING);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        clientService.failedClient(clientId);

        verify(clientRepository).delete(client);
    }

    @Test
    void shouldUpdateClientSuccessfully() {
        UUID clientId = UUID.randomUUID();

        Client existingClient = new Client();
        existingClient.setId(clientId);
        existingClient.setUserId(1);
        existingClient.setFirstName("Old");
        existingClient.setLastName("Name");
        existingClient.setAddress("Old Address");
        existingClient.setCity("Old City");
        existingClient.setPhone("000000000");

        Client updatedData = new Client();
        updatedData.setUserId(1);
        updatedData.setFirstName("New");
        updatedData.setLastName("Name");
        updatedData.setAddress("New Address");
        updatedData.setCity("New City");
        updatedData.setPhone("123456789");

        when(clientRepository.findByIdAndUserId(clientId, updatedData.getUserId())).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(any(Client.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Client result = clientService.updateClient(clientId, updatedData);

        assertEquals("New", result.getFirstName());
        assertEquals("Name", result.getLastName());
        assertEquals("New Address", result.getAddress());
        assertEquals("New City", result.getCity());
        assertEquals("123456789", result.getPhone());

        verify(clientRepository).save(existingClient);
    }

    @Test
    void shouldThrowExceptionWhenClientNotFound() {
        UUID clientId = UUID.randomUUID();
        Client updatedData = new Client();
        updatedData.setUserId(1);

        when(clientRepository.findByIdAndUserId(clientId, updatedData.getUserId())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> clientService.updateClient(clientId, updatedData));

        assertEquals("error/account-not-found", ex.getMessage());
        assertEquals(clientId.toString(), ex.getData());
    }

    @Test
    void shouldReturnClientWhenExists() {
        UUID clientId = UUID.randomUUID();

        Client client = new Client();
        client.setId(clientId);
        client.setUserId(1);
        client.setFirstName("Anna");
        client.setLastName("Nowak");
        client.setAccountNumber("12345678901234567890123456");

        when(clientRepository.findByIdAndUserId(clientId, client.getUserId())).thenReturn(Optional.of(client));

        Client result = clientService.getClientById(clientId, client.getUserId());

        assertNotNull(result);
        assertEquals(clientId, result.getId());
        assertEquals("Anna", result.getFirstName());
        assertEquals("Nowak", result.getLastName());
    }

    @Test
    void shouldThrowExceptionWhenClientDoesNotExist() {
        UUID clientId = UUID.randomUUID();

        when(clientRepository.findByIdAndUserId(clientId, 0)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> clientService.getClientById(clientId, 0));

        assertEquals("error/account-not-found", ex.getMessage());
        assertEquals(clientId.toString(), ex.getData());
    }
}
