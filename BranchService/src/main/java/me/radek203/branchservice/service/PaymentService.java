package me.radek203.branchservice.service;

import me.radek203.branchservice.entity.BalanceChange;
import me.radek203.branchservice.entity.Transfer;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    Transfer getTransfer(UUID id);

    Transfer makeTransfer(String fromAccount, String toAccount, double amount);

    Transfer makePaymentTransfer(String fromAccount, UUID service, double amount);

    List<Transfer> getTransfersByAccount(String account);

    void createTransfer(Transfer transfer);

    void failedTransfer(Transfer transfer);

    void completedTransfer(Transfer transfer);

    BalanceChange getBalanceChange(UUID id);

    BalanceChange makeDeposit(String account, double amount);

    BalanceChange makeWithdraw(String account, double amount);

    List<BalanceChange> getBalanceChanges(String account);

    void completedBalanceChange(BalanceChange balanceChange);

    void failedBalanceChange(BalanceChange balanceChange);

}
