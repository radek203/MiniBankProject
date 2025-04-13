package me.radek203.headquarterservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.radek203.headquarterservice.entity.BalanceChange;
import me.radek203.headquarterservice.entity.Client;
import me.radek203.headquarterservice.entity.Transfer;
import me.radek203.headquarterservice.service.ClientService;
import me.radek203.headquarterservice.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class KafkaListeners {

    private final ClientService clientService;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "headquarter-client-create", groupId = "hq_group")
    void listenerClientCreate(String data) throws JsonProcessingException {
        Client client = objectMapper.readValue(data, Client.class);
        clientService.createClient(client);
    }

    @KafkaListener(topics = "headquarter-transfer-create", groupId = "hq_group")
    void listenerTransfers(String data) throws JsonProcessingException {
        Transfer transfer = objectMapper.readValue(data, Transfer.class);
        paymentService.makeTransfer(transfer);
    }

    @KafkaListener(topics = "headquarter-balance-deposit", groupId = "hq_group")
    void listenerBalanceDeposit(String data) throws JsonProcessingException {
        BalanceChange balanceChange = objectMapper.readValue(data, BalanceChange.class);
        paymentService.makeBalanceChange(balanceChange);
    }

    @KafkaListener(topics = "headquarter-balance-withdraw", groupId = "hq_group")
    void listenerBalanceWithdraw(String data) throws JsonProcessingException {
        BalanceChange balanceChange = objectMapper.readValue(data, BalanceChange.class);
        paymentService.makeBalanceChange(balanceChange);
    }

}
