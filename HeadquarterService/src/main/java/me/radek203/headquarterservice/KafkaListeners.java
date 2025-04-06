package me.radek203.headquarterservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.radek203.headquarterservice.entity.Client;
import me.radek203.headquarterservice.entity.Transfer;
import me.radek203.headquarterservice.service.ClientService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class KafkaListeners {

    private ClientService clientService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "headquarter-client-create", groupId = "group_id")
    void listenerClientCreate(String data) throws JsonProcessingException {
        Client client = objectMapper.readValue(data, Client.class);
        clientService.createClient(client);
    }

    @KafkaListener(topics = "headquarter-transfer-create", groupId = "group_id")
    void listenerTransfers(String data) throws JsonProcessingException {
        Transfer transfer = objectMapper.readValue(data, Transfer.class);

        /*
        Optional<Client> fromClient = clientRepository.findByAccountNumber(transfer.getFromAccount());
        Optional<Client> toClient = clientRepository.findByAccountNumber(transfer.getToAccount());
        if (fromClient.isEmpty() || fromClient.get().getBalance() < transfer.getAmount() || toClient.isEmpty()) {
            transfer.setStatus(TransferStatus.FAILED);
            kafkaSenderService.sendMessage("branch-" + transfer.getFromBranchId() + "-transfer-failed", String.valueOf(transfer.getId()), transfer);
            kafkaSenderService.sendMessage("branch-" + transfer.getToBranchId() + "-transfer-failed", String.valueOf(transfer.getId()), transfer);
            return;
        }

        try {
            makeTransaction(fromClient.get(), toClient.get(), transfer);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            transfer.setStatus(TransferStatus.FAILED);
            kafkaSenderService.sendMessage("branch-" + transfer.getFromBranchId() + "-transfer-failed", String.valueOf(transfer.getId()), transfer);
            kafkaSenderService.sendMessage("branch-" + transfer.getToBranchId() + "-transfer-failed", String.valueOf(transfer.getId()), transfer);
            return;
        }

        kafkaSenderService.sendMessage("branch-" + transfer.getFromBranchId() + "-transfer-hq-accepted", String.valueOf(transfer.getId()), transfer);
        kafkaSenderService.sendMessage("branch-" + transfer.getToBranchId() + "-transfer-hq-accepted", String.valueOf(transfer.getId()), transfer);
         */
    }

    /*
    @Transactional
    void makeTransaction(Client fromClientEntity, Client toClientEntity, Transfer transfer) {
        fromClientEntity.setBalance(fromClientEntity.getBalance() - transfer.getAmount());
        toClientEntity.setBalance(toClientEntity.getBalance() + transfer.getAmount());

        clientRepository.save(fromClientEntity);
        clientRepository.save(toClientEntity);
    }
     */

}
