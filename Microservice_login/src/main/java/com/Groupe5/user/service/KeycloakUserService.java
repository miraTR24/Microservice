package com.Groupe5.user.service;

import com.Groupe5.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakUserService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserService.class);

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;
    @Autowired
    private UserService userService;
    private final RestTemplate restTemplate = new RestTemplate();

    private String getAdminAccessToken() {
        String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", clientId);
        params.add("username", adminUsername);
        params.add("password", adminPassword);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().containsKey("access_token")) {
            return (String) response.getBody().get("access_token");
        } else {
            logger.error("Failed to retrieve admin access token, response: {}", response);
            throw new RuntimeException("Failed to retrieve admin access token");
        }
    }

    public void createUser(String username, String email, String firstName, String lastName, String password) {
        String token = getAdminAccessToken();

        String createUserUrl = authServerUrl + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("enabled", true);
        // Utiliser "clientRoles" au lieu de "realmRoles"
        Map<String, List<String>> clientRoles = new HashMap<>();
        clientRoles.put("spring-boot-client", Collections.singletonList("client_user"));
        user.put("clientRoles", clientRoles);
        String date = LocalDateTime.now().toString();

        User user2 = new User(username,email,password,date,"client_user");

        userService.saveUser(user2);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(user, headers);
        ResponseEntity<Void> response = restTemplate.postForEntity(createUserUrl, request, Void.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            logger.info("User created successfully: {}", username);
        } else {
            logger.error("Failed to create user, response: {}", response);
            throw new RuntimeException("Failed to create user");
        }

        // Set user password
        String getUserIdUrl = createUserUrl + "?username=" + username;
        ResponseEntity<List> getUserResponse = restTemplate.exchange(getUserIdUrl, HttpMethod.GET, new HttpEntity<>(headers), List.class);

        if (getUserResponse.getStatusCode() == HttpStatus.OK && !getUserResponse.getBody().isEmpty()) {
            Map<String, Object> userResponse = (Map<String, Object>) getUserResponse.getBody().get(0);
            String userId = (String) userResponse.get("id");

            String resetPasswordUrl = createUserUrl + "/" + userId + "/reset-password";

            Map<String, Object> credential = new HashMap<>();
            credential.put("type", "password");
            credential.put("value", password);
            credential.put("temporary", false);


            HttpEntity<Map<String, Object>> passwordRequest = new HttpEntity<>(credential, headers);
            ResponseEntity<Void> passwordResponse = restTemplate.exchange(resetPasswordUrl, HttpMethod.PUT, passwordRequest, Void.class);

            if (passwordResponse.getStatusCode() == HttpStatus.NO_CONTENT) {
                logger.info("Password set successfully for user: {}", username);
            } else {
                logger.error("Failed to set password for user, response: {}", passwordResponse);
                throw new RuntimeException("Failed to set password for user");
            }
        } else {
            logger.error("Failed to retrieve user ID for username: {}", username);
            throw new RuntimeException("Failed to retrieve user ID");
        }
    }
}