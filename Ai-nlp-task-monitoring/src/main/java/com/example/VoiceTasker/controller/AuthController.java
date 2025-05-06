package com.example.VoiceTasker.controller;

import com.example.VoiceTasker.model.User;
import com.example.VoiceTasker.repository.UserRepository;
import com.example.VoiceTasker.config.JwtConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5502")
public class AuthController {
    @Autowired
    private JwtConfig jwtConfig;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (!jwtConfig.isTokenExpired(token)) {
                String email = jwtConfig.getUserEmail(token);
                String password = jwtConfig.getUserPassword(token);
                
                return userRepository.findByEmail(email)
                    .filter(user -> user.getPassword().equals(password))
                    .map(user -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("id", user.getId());
                        response.put("email", user.getEmail());
                        return ResponseEntity.ok(response);
                    })
                    .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (!jwtConfig.isTokenExpired(token)) {
                String email = jwtConfig.getUserEmail(token);
                String password = jwtConfig.getUserPassword(token);
                
                if (userRepository.existsByEmail(email)) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Email already exists"));
                }
                
                User user = new User();
                user.setEmail(email);
                user.setPassword(password);
                userRepository.save(user);
                
                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("email", user.getEmail());
                response.put("password", user.getPassword());
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}