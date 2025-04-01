package me.radek203.headquarterservice;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class KafkaListeners {

    @KafkaListener(topics = "headquarter", groupId = "group_id")
    void listener(String data) {
        System.out.println("Listener received: " + data);
    }

    @KafkaListener(topics = "headquarter-client-create", groupId = "group_id")
    void listenerClients(String data) {
        System.out.println("Listener clients received: " + data);
    }

}
