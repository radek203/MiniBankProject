package me.radek203.headquarterservice.service.impl.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.radek203.headquarterservice.entity.BalanceChange;
import me.radek203.headquarterservice.entity.Transfer;
import me.radek203.headquarterservice.entity.TransferStatus;
import me.radek203.headquarterservice.service.KafkaSenderService;
import me.radek203.headquarterservice.service.KafkaTopicErrorHandler;

public class KafkaTransferErrorHandlers {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static KafkaTopicErrorHandler getKafkaTransferCreateHandler(KafkaSenderService sender) {
        return (key, value) -> {
            try {
                Transfer transfer = OBJECT_MAPPER.readValue(value, Transfer.class);
                transfer.setStatus(TransferStatus.FAILED);
                sender.sendMessage("branch-" + transfer.getFromBranchId() + "-transfer-failed", String.valueOf(transfer.getId()), transfer);
                if (transfer.getFromBranchId() != transfer.getToBranchId()) {
                    sender.sendMessage("branch-" + transfer.getToBranchId() + "-transfer-failed", String.valueOf(transfer.getId()), transfer);
                }
                return true;
            } catch (JsonProcessingException ignored) {
            }
            return false;
        };
    }

    public static KafkaTopicErrorHandler getKafkaTransferErrorHandler(KafkaSenderService sender) {
        return (key, value) -> {
            try {
                BalanceChange balanceChange = OBJECT_MAPPER.readValue(value, BalanceChange.class);
                sender.sendMessage("branch-" + balanceChange.getBranchId() + "-balance-change-failed", String.valueOf(balanceChange.getId()), balanceChange);
                return true;
            } catch (JsonProcessingException ignored) {
            }
            return false;
        };
    }

}
