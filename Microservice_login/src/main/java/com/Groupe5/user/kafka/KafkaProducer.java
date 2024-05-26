package com.Groupe5.user.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "users";

    public void sendMessage(String message) {
        System.out.println("Producing message: " + message);
        kafkaTemplate.send(TOPIC, message);
    }
}