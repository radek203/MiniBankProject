package me.radek203.headquarterservice.service.impl;

import me.radek203.headquarterservice.entity.BalanceChange;
import me.radek203.headquarterservice.entity.Client;
import me.radek203.headquarterservice.entity.Transfer;
import me.radek203.headquarterservice.entity.TransferStatus;
import me.radek203.headquarterservice.exception.ResourceInvalidException;
import me.radek203.headquarterservice.exception.ResourceNotFoundException;
import me.radek203.headquarterservice.repository.ClientRepository;
import me.radek203.headquarterservice.service.KafkaSenderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    private final String fromAccount = "FROM123";
    private final String toAccount = "TO456";
    private final UUID transferId = UUID.randomUUID();
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private KafkaSenderService kafkaSenderService;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Client createClient(String account, double balance) {
        Client client = new Client();
        client.setAccountNumber(account);
        client.setBalance(balance);
        return client;
    }

    private Transfer createTransfer(double amount, int fromBranchId, int toBranchId) {
        return new Transfer(transferId, fromAccount, toAccount, amount, null, fromBranchId, toBranchId, System.currentTimeMillis());
    }

    @Test
    void makeTransfer_shouldTransferAndSendKafkaMessages_whenValid() {
        Transfer transfer = createTransfer(100.0, 1, 2);
        Client fromClient = createClient(fromAccount, 200.0);
        Client toClient = createClient(toAccount, 50.0);

        when(clientRepository.findByAccountNumber(fromAccount)).thenReturn(Optional.of(fromClient));
        when(clientRepository.findByAccountNumber(toAccount)).thenReturn(Optional.of(toClient));

        paymentService.makeTransfer(transfer);

        assertEquals(100.0, fromClient.getBalance());
        assertEquals(150.0, toClient.getBalance());
        assertEquals(TransferStatus.COMPLETED, transfer.getStatus());

        verify(clientRepository).save(fromClient);
        verify(clientRepository).save(toClient);
        verify(kafkaSenderService).sendMessage("branch-1-transfer-completed", transfer.getId().toString(), transfer);
        verify(kafkaSenderService).sendMessage("branch-2-transfer-completed", transfer.getId().toString(), transfer);
    }

    @Test
    void makeTransfer_shouldThrow_whenFromClientNotFound() {
        Transfer transfer = createTransfer(100.0, 1, 1);
        when(clientRepository.findByAccountNumber(fromAccount)).thenReturn(Optional.empty());

        assertThrows(ResourceInvalidException.class, () -> paymentService.makeTransfer(transfer));
    }

    @Test
    void makeTransfer_shouldThrow_whenToClientNotFound() {
        Transfer transfer = createTransfer(100.0, 1, 1);
        Client fromClient = createClient(fromAccount, 500.0);
        when(clientRepository.findByAccountNumber(fromAccount)).thenReturn(Optional.of(fromClient));
        when(clientRepository.findByAccountNumber(toAccount)).thenReturn(Optional.empty());

        assertThrows(ResourceInvalidException.class, () -> paymentService.makeTransfer(transfer));
    }

    @Test
    void makeTransfer_shouldThrow_whenInsufficientBalance() {
        Transfer transfer = createTransfer(100.0, 1, 1);
        Client fromClient = createClient(fromAccount, 50.0);
        Client toClient = createClient(toAccount, 200.0);

        when(clientRepository.findByAccountNumber(fromAccount)).thenReturn(Optional.of(fromClient));
        when(clientRepository.findByAccountNumber(toAccount)).thenReturn(Optional.of(toClient));

        assertThrows(ResourceInvalidException.class, () -> paymentService.makeTransfer(transfer));
    }

    @Test
    void makeTransfer_shouldSendOneKafkaMessage_whenSameBranch() {
        Transfer transfer = createTransfer(50.0, 1, 1);
        Client fromClient = createClient(fromAccount, 100.0);
        Client toClient = createClient(toAccount, 100.0);

        when(clientRepository.findByAccountNumber(fromAccount)).thenReturn(Optional.of(fromClient));
        when(clientRepository.findByAccountNumber(toAccount)).thenReturn(Optional.of(toClient));

        paymentService.makeTransfer(transfer);

        verify(kafkaSenderService).sendMessage("branch-1-transfer-completed", transfer.getId().toString(), transfer);
        verify(kafkaSenderService, times(1)).sendMessage(any(), any(), any());
    }

    @Test
    void makeBalanceChange_shouldIncreaseBalance_whenDeposit() {
        BalanceChange change = new BalanceChange(UUID.randomUUID(), "ACC1", 200.0, null, 1, System.currentTimeMillis());
        Client client = createClient("ACC1", 300.0);

        when(clientRepository.findByAccountNumber("ACC1")).thenReturn(Optional.of(client));

        paymentService.makeBalanceChange(change);

        assertEquals(500.0, client.getBalance());
        verify(clientRepository).save(client);
        verify(kafkaSenderService).sendMessage("branch-1-balance-change-completed", change.getId().toString(), change);
    }

    @Test
    void makeBalanceChange_shouldDecreaseBalance_whenValidWithdraw() {
        BalanceChange change = new BalanceChange(UUID.randomUUID(), "ACC1", -100.0, null, 1, System.currentTimeMillis());
        Client client = createClient("ACC1", 200.0);

        when(clientRepository.findByAccountNumber("ACC1")).thenReturn(Optional.of(client));

        paymentService.makeBalanceChange(change);

        assertEquals(100.0, client.getBalance());
    }

    @Test
    void makeBalanceChange_shouldThrow_whenInsufficientBalanceForWithdraw() {
        BalanceChange change = new BalanceChange(UUID.randomUUID(), "ACC1", -500.0, null, 1, System.currentTimeMillis());
        Client client = createClient("ACC1", 200.0);

        when(clientRepository.findByAccountNumber("ACC1")).thenReturn(Optional.of(client));

        assertThrows(ResourceInvalidException.class, () -> paymentService.makeBalanceChange(change));
    }

    @Test
    void makeBalanceChange_shouldThrow_whenClientNotFound() {
        BalanceChange change = new BalanceChange(UUID.randomUUID(), "ACC404", 100.0, null, 1, System.currentTimeMillis());
        when(clientRepository.findByAccountNumber("ACC404")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.makeBalanceChange(change));
    }
}
