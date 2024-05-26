package com.Groupe5.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;

import java.util.Map;
@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/perform_login")
    public String performLogin(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        String tokenUrl = "http://srv-dpi-archidist-keycloak.univ-rouen.fr:8080/realms/GROUPE05/protocol/openid-connect/token";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", "spring-boot-client");
        params.add("username", username);
        params.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            logger.info("Attempting to retrieve token for user: {}", username);
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().containsKey("access_token")) {
                String accessToken = (String) response.getBody().get("access_token");
                logger.info("Token retrieved successfully: {}", accessToken);
                session.setAttribute("token", accessToken);
                return "redirect:/api/v1/demo";
            } else {
                logger.error("Failed to retrieve token, response: {}", response);
                model.addAttribute("error", "Invalid credentials");
                return "login";
            }
        } catch (Exception e) {
            logger.error("An error occurred while trying to authenticate", e);
            model.addAttribute("error", "An error occurred while trying to authenticate. Please try again.");
            return "login";
        }
    }
}