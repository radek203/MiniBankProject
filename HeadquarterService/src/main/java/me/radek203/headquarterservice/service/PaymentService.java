package me.radek203.headquarterservice.service;

import me.radek203.headquarterservice.entity.BalanceChange;
import me.radek203.headquarterservice.entity.Transfer;

public interface PaymentService {

    void makeTransfer(Transfer transfer);

    void makeBalanceChange(BalanceChange balanceChange);

}
