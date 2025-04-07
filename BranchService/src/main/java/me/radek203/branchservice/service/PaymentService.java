package me.radek203.branchservice.service;

import me.radek203.branchservice.entity.BalanceChange;
import me.radek203.branchservice.entity.Transfer;

import java.util.UUID;

public interface PaymentService {

    Transfer getTransfer(UUID id);

    Transfer makeTransfer(String fromAccount, String toAccount, String message, double amount);

    Transfer makePaymentTransfer(String fromAccount, UUID service, String message, double amount);

    void createTransfer(Transfer transfer);

    void failedTransfer(Transfer transfer);

    void completedTransfer(Transfer transfer);

    BalanceChange makeDeposit(String account, double amount);

    BalanceChange makeWithdraw(String account, double amount);

    void completedBalanceChange(BalanceChange balanceChange);

    void failedBalanceChange(BalanceChange balanceChange);

}
