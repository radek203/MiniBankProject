package me.radek203.headquarterservice.service.impl;

import me.radek203.headquarterservice.entity.Client;
import me.radek203.headquarterservice.entity.ClientStatus;
import me.radek203.headquarterservice.exception.ResourceNotFoundException;
import me.radek203.headquarterservice.repository.ClientRepository;
import me.radek203.headquarterservice.service.KafkaSenderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    private final UUID clientId = UUID.randomUUID();
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private KafkaSenderService kafkaSenderService;
    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    void createClient_shouldSaveAndSendKafkaMessage_whenClientIsNew() {
        Client client = new Client();
        client.setId(clientId);
        client.setBranch(1);

        Mockito.when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        clientService.createClient(client);

        assertEquals(ClientStatus.ACTIVE, client.getStatus());
        verify(clientRepository).save(client);
        verify(kafkaSenderService).sendMessage("branch-1-client-create-active", clientId.toString(), client);
    }

    @Test
    void createClient_shouldDoNothing_whenClientIsAlreadyActive() {
        Client existing = new Client();
        existing.setId(clientId);
        existing.setStatus(ClientStatus.ACTIVE);

        Mockito.when(clientRepository.findById(clientId)).thenReturn(Optional.of(existing));

        clientService.createClient(existing);

        verify(clientRepository, never()).save(any());
        verify(kafkaSenderService, never()).sendMessage(any(), any(), any());
    }

    @Test
    void getClientByAccountNumber_shouldReturnClient_whenFound() {
        Client client = new Client();
        client.setAccountNumber("ACC123");

        when(clientRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(client));

        Client result = clientService.getClientByAccountNumber("ACC123");

        assertEquals("ACC123", result.getAccountNumber());
    }

    @Test
    void getClientByAccountNumber_shouldThrow_whenNotFound() {
        when(clientRepository.findByAccountNumber("ACC123")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> clientService.getClientByAccountNumber("ACC123"));
    }

    @Test
    void getClientsByUserId_shouldReturnList() {
        List<Client> clients = List.of(new Client(), new Client());

        when(clientRepository.findByUserId(1)).thenReturn(clients);

        List<Client> result = clientService.getClientsByUserId(1);

        assertEquals(2, result.size());
    }
}
