package me.radek203.branchservice.service.impl.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.radek203.branchservice.entity.Transfer;
import me.radek203.branchservice.entity.TransferStatus;
import me.radek203.branchservice.service.KafkaSenderService;
import me.radek203.branchservice.service.KafkaTopicErrorHandler;

public class KafkaTransferErrorHandlers {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static KafkaTopicErrorHandler getKafkaTransferErrorHandler(KafkaSenderService sender) {
        return (key, value) -> {
            try {
                Transfer transfer = MAPPER.readValue(value, Transfer.class);
                transfer.setStatus(TransferStatus.FAILED);
                sender.sendMessage("branch-" + transfer.getFromBranchId() + "-transfer-failed", String.valueOf(transfer.getId()), transfer);
                return true;
            } catch (JsonProcessingException ignored) {
            }
            return false;
        };
    }

}
