package me.radek203.branchservice.service.impl;

import me.radek203.branchservice.client.HeadquarterClient;
import me.radek203.branchservice.config.AppProperties;
import me.radek203.branchservice.entity.*;
import me.radek203.branchservice.exception.ResourceInvalidException;
import me.radek203.branchservice.repository.BalanceChangeRepository;
import me.radek203.branchservice.repository.ClientRepository;
import me.radek203.branchservice.repository.TransferRepository;
import me.radek203.branchservice.service.KafkaSenderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private KafkaSenderService kafkaSenderService;
    @Mock
    private TransferRepository transferRepository;
    @Mock
    private BalanceChangeRepository balanceChangeRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private AppProperties appProperties;
    @Mock
    private HeadquarterClient hqClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void shouldMakeTransferInSameBranch() {
        String fromAccount = "111";
        String toAccount = "222";
        double amount = 100;

        Client fromClient = new Client();
        fromClient.setUserId(1);
        fromClient.setAccountNumber(fromAccount);
        fromClient.setBalance(500);
        fromClient.setBalanceReserved(0);

        Client toClient = new Client();
        toClient.setAccountNumber(toAccount);
        toClient.setBalance(200);
        toClient.setBalanceReserved(50);

        when(clientRepository.findByAccountNumberAndUserId(fromAccount, fromClient.getUserId())).thenReturn(Optional.of(fromClient));
        when(clientRepository.findByAccountNumber(toAccount)).thenReturn(Optional.of(toClient));
        when(appProperties.getBranchId()).thenReturn(1);
        when(hqClient.getResponse(anyString(), any(), eq(toAccount))).thenReturn(1);
        when(transferRepository.save(any(Transfer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transfer result = paymentService.makeTransfer(fromAccount, toAccount, amount, fromClient.getUserId());

        assertNotNull(result);
        assertEquals(TransferStatus.STARTED, result.getStatus());
        assertEquals(1, result.getFromBranchId());
        assertEquals(1, result.getToBranchId());

        assertEquals(400, fromClient.getBalance());
        assertEquals(100, fromClient.getBalanceReserved());
        assertEquals(-50, toClient.getBalanceReserved());

        verify(clientRepository, times(2)).save(any(Client.class));
        verify(kafkaSenderService).sendMessage(
                eq("headquarter-transfer-create"),
                eq(result.getId().toString()),
                eq(result)
        );
    }

    @Test
    void shouldThrowExceptionWhenBalanceIsTooLow() {
        String fromAccount = "111";
        String toAccount = "222";

        Client fromClient = new Client();
        fromClient.setUserId(1);
        fromClient.setAccountNumber(fromAccount);
        fromClient.setBalance(50);
        when(clientRepository.findByAccountNumberAndUserId(fromAccount, fromClient.getUserId())).thenReturn(Optional.of(fromClient));

        assertThrows(ResourceInvalidException.class, () ->
                paymentService.makeTransfer(fromAccount, toAccount, 100, fromClient.getUserId())
        );
    }

    @Test
    void shouldMakeDeposit() {
        String account = "123";
        double amount = 200;

        Client client = new Client();
        client.setUserId(1);
        client.setAccountNumber(account);
        client.setBalance(300);
        client.setBalanceReserved(50);

        when(clientRepository.findByAccountNumberAndUserId(account, client.getUserId())).thenReturn(Optional.of(client));
        when(appProperties.getBranchId()).thenReturn(1);
        when(balanceChangeRepository.save(any(BalanceChange.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BalanceChange result = paymentService.makeDeposit(account, amount, client.getUserId());

        assertNotNull(result);
        assertEquals(BalanceChangeStatus.STARTED, result.getStatus());
        assertEquals(account, result.getAccount());

        assertEquals(-150, client.getBalanceReserved());

        verify(clientRepository).save(client);
        verify(kafkaSenderService).sendMessage(
                eq("headquarter-balance-deposit"),
                eq(result.getId().toString()),
                eq(result)
        );
    }

    @Test
    void shouldMakeWithdraw() {
        String account = "123";
        double amount = 150;

        Client client = new Client();
        client.setUserId(1);
        client.setAccountNumber(account);
        client.setBalance(200);
        client.setBalanceReserved(0);

        when(clientRepository.findByAccountNumberAndUserId(account, client.getUserId())).thenReturn(Optional.of(client));
        when(appProperties.getBranchId()).thenReturn(1);
        when(balanceChangeRepository.save(any(BalanceChange.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BalanceChange result = paymentService.makeWithdraw(account, amount, client.getUserId());

        assertNotNull(result);
        assertEquals(-amount, result.getAmount());
        assertEquals(account, result.getAccount());
        assertEquals(50, client.getBalance());
        assertEquals(150, client.getBalanceReserved());

        verify(kafkaSenderService).sendMessage(
                eq("headquarter-balance-withdraw"),
                eq(result.getId().toString()),
                eq(result)
        );
    }

    @Test
    void shouldCompleteTransferAndUpdateBalances() {
        UUID transferId = UUID.randomUUID();
        Transfer transfer = new Transfer();
        transfer.setId(transferId);
        transfer.setAmount(100);
        transfer.setStatus(TransferStatus.STARTED);
        transfer.setFromAccount("111");
        transfer.setToAccount("222");

        Client fromClient = new Client();
        fromClient.setAccountNumber("111");
        fromClient.setBalance(500);
        fromClient.setBalanceReserved(100);

        Client toClient = new Client();
        toClient.setAccountNumber("222");
        toClient.setBalance(200);

        when(transferRepository.findById(transferId)).thenReturn(Optional.of(transfer));
        when(clientRepository.findByAccountNumber("111")).thenReturn(Optional.of(fromClient));
        when(clientRepository.findByAccountNumber("222")).thenReturn(Optional.of(toClient));

        paymentService.completedTransfer(transfer);

        assertEquals(TransferStatus.STARTED, transfer.getStatus());
        assertEquals(0, fromClient.getBalanceReserved());
        assertEquals(500, fromClient.getBalance());
        assertEquals(300, toClient.getBalance());

        verify(clientRepository, times(2)).save(any(Client.class));
    }

    @Test
    void shouldFailTransferAndRestoreFromBalance() {
        UUID transferId = UUID.randomUUID();
        Transfer transfer = new Transfer();
        transfer.setId(transferId);
        transfer.setAmount(150);
        transfer.setStatus(TransferStatus.STARTED);
        transfer.setFromAccount("111");

        Client fromClient = new Client();
        fromClient.setAccountNumber("111");
        fromClient.setBalance(350);
        fromClient.setBalanceReserved(150);

        when(transferRepository.findById(transferId)).thenReturn(Optional.of(transfer));
        when(clientRepository.findByAccountNumber("111")).thenReturn(Optional.of(fromClient));

        paymentService.failedTransfer(transfer);

        assertEquals(0, fromClient.getBalanceReserved());
        assertEquals(500, fromClient.getBalance());

        verify(clientRepository).save(fromClient);
    }

    @Test
    void shouldCompleteBalanceChangeAndUpdateBalance() {
        UUID changeId = UUID.randomUUID();
        BalanceChange change = new BalanceChange();
        change.setId(changeId);
        change.setAccount("abc123");
        change.setAmount(250);
        change.setStatus(BalanceChangeStatus.STARTED);

        Client client = new Client();
        client.setAccountNumber("abc123");
        client.setBalance(300);
        client.setBalanceReserved(-250);

        when(clientRepository.findByAccountNumber("abc123")).thenReturn(Optional.of(client));

        paymentService.completedBalanceChange(change);

        assertEquals(BalanceChangeStatus.COMPLETED, change.getStatus());
        assertEquals(550, client.getBalance());
        assertEquals(0, client.getBalanceReserved());

        verify(clientRepository).save(client);
    }

    @Test
    void shouldFailBalanceChangeAndRollbackReservation() {
        UUID changeId = UUID.randomUUID();
        BalanceChange change = new BalanceChange();
        change.setId(changeId);
        change.setAccount("abc123");
        change.setAmount(-300);
        change.setStatus(BalanceChangeStatus.STARTED);

        Client client = new Client();
        client.setAccountNumber("abc123");
        client.setBalance(700);
        client.setBalanceReserved(300);

        when(clientRepository.findByAccountNumber("abc123")).thenReturn(Optional.of(client));
        when(balanceChangeRepository.findById(changeId)).thenReturn(Optional.of(change));

        paymentService.failedBalanceChange(change);

        assertEquals(1000, client.getBalance());
        assertEquals(0, client.getBalanceReserved());

        verify(clientRepository).save(client);
    }

    @Test
    void completedTransfer_shouldNotProceedWhenClientNotFound() {
        UUID transferId = UUID.randomUUID();
        Transfer transfer = new Transfer();
        transfer.setId(transferId);
        transfer.setAmount(100);
        transfer.setFromAccount("111");
        transfer.setToAccount("222");
        transfer.setStatus(TransferStatus.STARTED);

        when(transferRepository.findById(transferId)).thenReturn(Optional.of(transfer));
        when(clientRepository.findByAccountNumber("111")).thenReturn(Optional.empty());
        when(clientRepository.findByAccountNumber("222")).thenReturn(Optional.empty());

        paymentService.completedTransfer(transfer);

        verify(clientRepository, never()).save(any());
    }

    @Test
    void failedTransfer_shouldNotProceedWhenFromClientNotFound() {
        UUID id = UUID.randomUUID();
        Transfer transfer = new Transfer();
        transfer.setId(id);
        transfer.setFromAccount("111");

        when(transferRepository.findById(id)).thenReturn(Optional.empty());

        paymentService.failedTransfer(transfer);

        verify(clientRepository, never()).save(any());
        verify(transferRepository, never()).deleteById(any());
    }

    @Test
    void completedTransfer_shouldNotProceedIfAlreadyCompleted() {
        UUID id = UUID.randomUUID();
        Transfer transfer = new Transfer();
        transfer.setId(id);
        transfer.setStatus(TransferStatus.COMPLETED);

        when(transferRepository.findById(id)).thenReturn(Optional.of(transfer));

        paymentService.completedTransfer(transfer);

        verify(transferRepository, never()).save(any());
    }

    @Test
    void completedTransfer_shouldSkipIfAlreadyCompleted() {
        UUID id = UUID.randomUUID();
        Transfer transfer = new Transfer();
        transfer.setId(id);
        transfer.setStatus(TransferStatus.COMPLETED);

        when(transferRepository.findById(id)).thenReturn(Optional.of(transfer));

        paymentService.completedTransfer(transfer);

        verify(clientRepository, never()).save(any());
    }

    @Test
    void completedBalanceChange_shouldNotProceedWhenClientNotFound() {
        UUID balanceChangeId = UUID.randomUUID();
        BalanceChange balanceChange = new BalanceChange();
        balanceChange.setId(balanceChangeId);
        balanceChange.setAccount("abc123");

        when(clientRepository.findByAccountNumber("abc123")).thenReturn(Optional.empty());

        paymentService.completedBalanceChange(balanceChange);

        verify(clientRepository, never()).save(any());
        verify(balanceChangeRepository, never()).save(any());
    }

    @Test
    void completedBalanceChange_shouldNotProceedIfAlreadyCompleted() {
        Client client = new Client();
        client.setAccountNumber("abc123");

        UUID balanceChangeId = UUID.randomUUID();
        BalanceChange balanceChange = new BalanceChange();
        balanceChange.setId(balanceChangeId);
        balanceChange.setStatus(BalanceChangeStatus.COMPLETED);
        balanceChange.setAccount("abc123");

        when(clientRepository.findByAccountNumber("abc123")).thenReturn(Optional.of(client));
        when(balanceChangeRepository.findById(balanceChangeId)).thenReturn(Optional.of(balanceChange));

        paymentService.completedBalanceChange(balanceChange);

        verify(clientRepository, never()).save(any());
        verify(balanceChangeRepository, never()).save(any());
    }

    @Test
    void failedBalanceChange_shouldNotProceedWhenClientNotFound() {
        UUID balanceChangeId = UUID.randomUUID();
        BalanceChange balanceChange = new BalanceChange();
        balanceChange.setId(balanceChangeId);
        balanceChange.setAccount("abc123");

        when(clientRepository.findByAccountNumber("abc123")).thenReturn(Optional.empty());

        paymentService.failedBalanceChange(balanceChange);

        verify(clientRepository, never()).save(any());
        verify(balanceChangeRepository, never()).deleteById(any());
    }

    @Test
    void failedBalanceChange_shouldNotProceedWhenBalanceChangeNotFound() {
        Client client = new Client();
        client.setAccountNumber("abc123");

        UUID balanceChangeId = UUID.randomUUID();
        BalanceChange balanceChange = new BalanceChange();
        balanceChange.setId(balanceChangeId);
        balanceChange.setAccount("abc123");

        when(clientRepository.findByAccountNumber("abc123")).thenReturn(Optional.of(client));
        when(balanceChangeRepository.findById(balanceChangeId)).thenReturn(Optional.empty());

        paymentService.failedBalanceChange(balanceChange);

        verify(clientRepository, never()).save(any());
        verify(balanceChangeRepository, never()).deleteById(any());
    }

    @Test
    void failedBalanceChange_shouldSkipIfAlreadyCompleted() {
        Client client = new Client();
        client.setAccountNumber("abc123");

        UUID balanceChangeId = UUID.randomUUID();
        BalanceChange balanceChange = new BalanceChange();
        balanceChange.setId(balanceChangeId);
        balanceChange.setStatus(BalanceChangeStatus.COMPLETED);
        balanceChange.setAccount("abc123");

        when(clientRepository.findByAccountNumber("abc123")).thenReturn(Optional.of(client));
        when(balanceChangeRepository.findById(balanceChangeId)).thenReturn(Optional.of(balanceChange));

        paymentService.failedBalanceChange(balanceChange);

        verify(clientRepository, never()).save(any());
        verify(balanceChangeRepository, never()).deleteById(any());
    }
}