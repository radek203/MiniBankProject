package me.radek203.branchservice.service;

import me.radek203.branchservice.entity.BalanceChange;
import me.radek203.branchservice.entity.Transfer;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    /**
     * Retrieves a transfer operation by its unique identifier.
     *
     * @param id The unique identifier of the transfer operation.
     * @return A Transfer object representing the transfer operation.
     */
    Transfer getTransfer(UUID id);

    /**
     * Creates a transfer operation between two accounts.
     *
     * @param fromAccount The account number from which to transfer.
     * @param toAccount   The account number to which to transfer.
     * @param amount      The amount to be transferred.
     * @return A Transfer object representing the transfer operation.
     */
    Transfer makeTransfer(String fromAccount, String toAccount, double amount);

    /**
     * Creates a payment operation for a given credit card and service.
     *
     * @param fromAccount The credit card number to be charged.
     * @param service     The unique identifier of the service to be paid.
     * @param amount      The amount to be charged.
     * @return A Transfer object representing the payment operation.
     */
    Transfer makePaymentTransfer(String fromAccount, UUID service, double amount);

    /**
     * Retrieves a transfers associated with a given account.
     *
     * @param account The account number associated with the transfer.
     * @return A Transfer object representing the transfer operation.
     */
    List<Transfer> getTransfersByAccount(String account);

    /**
     * Creates a transfer operation for a given transfer object.
     *
     * @param transfer The transfer object containing details of the transfer.
     */
    void createTransfer(Transfer transfer);

    /**
     * Handles the failure of a transfer operation.
     *
     * @param transfer The transfer operation that failed.
     */
    void failedTransfer(Transfer transfer);

    /**
     * Handles the completion of a transfer operation.
     *
     * @param transfer The transfer operation that was completed.
     */
    void completedTransfer(Transfer transfer);

    /**
     * Retrieves a balance change operation by its unique identifier.
     *
     * @param id The unique identifier of the balance change operation.
     * @return A BalanceChange object representing the balance change operation.
     */
    BalanceChange getBalanceChange(UUID id);

    /**
     * Creates a deposit operation for a given account and amount.
     *
     * @param account The account number to which to deposit.
     * @param amount  The amount to deposit.
     * @return A BalanceChange object representing the deposit operation.
     */
    BalanceChange makeDeposit(String account, double amount);

    /**
     * Creates a withdrawal operation for a given account and amount.
     *
     * @param account The account number from which to withdraw.
     * @param amount  The amount to withdraw.
     * @return A BalanceChange object representing the withdrawal operation.
     */
    BalanceChange makeWithdraw(String account, double amount);

    /**
     * Retrieves a list of balance changes for a given account.
     *
     * @param account The account number for which to retrieve balance changes.
     * @return A list of BalanceChange objects associated with the specified account.
     */
    List<BalanceChange> getBalanceChanges(String account);

    /**
     * Handles the completion of a balance change operation.
     *
     * @param balanceChange The balance change operation that was completed.
     */
    void completedBalanceChange(BalanceChange balanceChange);

    /**
     * Handles the failure of a balance change operation.
     *
     * @param balanceChange The balance change operation that failed.
     */
    void failedBalanceChange(BalanceChange balanceChange);

}
