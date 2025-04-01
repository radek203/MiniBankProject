package me.radek203.branchservice;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class KafkaListeners {

    @KafkaListener(topics = "branch-${branch.id}", groupId = "group_id")
    void listener(String data) {
        System.out.println("Listener received: " + data);
    }

}
