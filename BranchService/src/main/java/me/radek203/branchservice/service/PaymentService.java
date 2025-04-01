package me.radek203.branchservice.service;

import me.radek203.branchservice.entity.Transfer;

public interface PaymentService {

    Transfer makeTransfer(String fromAccount, String toAccount, double amount);

}
