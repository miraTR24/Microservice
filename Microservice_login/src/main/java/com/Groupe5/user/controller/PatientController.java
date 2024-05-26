package com.Groupe5.user.controller;


import com.Groupe5.user.service.KeycloakUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PatientController {

    @Autowired
    private KeycloakUserService keycloakUserService;

    @PostMapping("/create")
    public String createUser(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam String password) {
        keycloakUserService.createUser(username, email, firstName, lastName, password);
        return "User created successfully";
    }
}