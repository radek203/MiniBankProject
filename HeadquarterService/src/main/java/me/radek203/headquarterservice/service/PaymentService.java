package me.radek203.headquarterservice.service;

import me.radek203.headquarterservice.entity.BalanceChange;
import me.radek203.headquarterservice.entity.Transfer;

public interface PaymentService {

    /**
     * Handles the payment process for a transfer.
     *
     * @param transfer the transfer object containing payment details
     */
    void makeTransfer(Transfer transfer);

    /**
     * Handles the balance change process.
     *
     * @param balanceChange the balance change object containing details
     */
    void makeBalanceChange(BalanceChange balanceChange);

}
