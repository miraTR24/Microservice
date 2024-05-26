package com.Groupe5.user.service;

import com.Groupe5.user.Repository.UserRepository;
import com.Groupe5.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private static final String TOPIC = "users";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public User saveUser(User user) {
        User savedUser = userRepository.save(user);

        // Créer un message JSON
        String message = createJsonMessage(savedUser);

        // Envoyer le message JSON
        kafkaTemplate.send(TOPIC, message);

        return savedUser;
    }

    private String createJsonMessage(User user) {
        try {
            // Créer un objet pour le message
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("type", "INSERT");
            messageMap.put("id", user.getId());
            messageMap.put("username", user.getName());
            messageMap.put("role", user.getRole());

            // Convertir l'objet en JSON
            return objectMapper.writeValueAsString(messageMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erreur lors de la conversion de l'objet en JSON", e);
        }
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}

