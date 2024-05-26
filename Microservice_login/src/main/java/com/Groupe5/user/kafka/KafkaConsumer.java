package com.Groupe5.user.kafka;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "users", groupId = "group_id")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
    }
}