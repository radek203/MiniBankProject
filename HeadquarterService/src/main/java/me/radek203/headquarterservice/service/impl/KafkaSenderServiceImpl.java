package me.radek203.headquarterservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.radek203.headquarterservice.entity.BalanceChange;
import me.radek203.headquarterservice.entity.Client;
import me.radek203.headquarterservice.entity.Transfer;
import me.radek203.headquarterservice.entity.TransferStatus;
import me.radek203.headquarterservice.service.KafkaSenderService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaSenderServiceImpl implements KafkaSenderService {

    private ObjectMapper mapper = new ObjectMapper();
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendMessage(String topic, String key, Object payload) {
        String message;
        try {
            message = mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            saveDeadLetter(topic, key, payload.toString(), e.getMessage(), Direction.OUT);
            return;
        }
        kafkaTemplate.send(topic, message).whenComplete((result, exception) -> {
            if (exception != null) {
                saveDeadLetter(topic, key, message, exception.getMessage(), Direction.OUT);
            }
        });
    }

    @Override
    public void saveDeadLetter(String topic, String key, String value, String error, Direction direction) {
        // Implement the logic to save the dead letter message
        // For example, you can log it or store it in a database
        if (direction == Direction.IN) {
            if (topic.equals("headquarter-client-create")) {
                try {
                    Client client = mapper.readValue(value, Client.class);
                    sendMessage("branch-" + client.getBranch() + "-client-create-error", String.valueOf(client.getId()), client);
                    return;
                } catch (JsonProcessingException ignored) {
                }
            }
            if (topic.equals("headquarter-transfer-create")) {
                try {
                    Transfer transfer = mapper.readValue(value, Transfer.class);
                    transfer.setStatus(TransferStatus.FAILED);
                    sendMessage("branch-" + transfer.getFromBranchId() + "-transfer-failed", String.valueOf(transfer.getId()), transfer);
                    sendMessage("branch-" + transfer.getToBranchId() + "-transfer-failed", String.valueOf(transfer.getId()), transfer);
                } catch (JsonProcessingException ignored) {
                }
            }
            if (topic.equals("headquarter-balance-deposit") || topic.equals("headquarter-balance-withdraw")) {
                try {
                    BalanceChange balanceChange = mapper.readValue(value, BalanceChange.class);
                    sendMessage("branch-" + balanceChange.getBranchId() + "-balance-change-failed", String.valueOf(balanceChange.getId()), balanceChange);
                } catch (JsonProcessingException ignored) {
                }
            }
        }
        System.out.println("Dead letter saved: Topic: " + topic + ", Key: " + key + ", Value: " + value + " , Error: " + error + ", Direction: " + direction);
    }

}
