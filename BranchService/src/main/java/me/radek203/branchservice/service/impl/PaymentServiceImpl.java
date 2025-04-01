package me.radek203.branchservice.service.impl;

import lombok.AllArgsConstructor;
import me.radek203.branchservice.entity.Transfer;
import me.radek203.branchservice.repository.TransferRepository;
import me.radek203.branchservice.service.KafkaSenderService;
import me.radek203.branchservice.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final KafkaSenderService kafkaSenderService;
    private final TransferRepository transferRepository;

    @Override
    public Transfer makeTransfer(String fromAccount, String toAccount, double amount) {
        Transfer transfer = new Transfer(null, fromAccount, toAccount, amount, "", "STARTED_BRANCH", System.currentTimeMillis());

        Transfer transferSaved = transferRepository.save(transfer);
        kafkaSenderService.sendMessage("headquarter-transfer-create", String.valueOf(transferSaved.getId()), transferSaved);

        return transferSaved;
    }

}
