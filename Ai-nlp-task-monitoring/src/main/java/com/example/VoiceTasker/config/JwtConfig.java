package com.example.VoiceTasker.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Configuration
public class JwtConfig {
    private static final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvcljcDWwTyHhwjRzVRlo\n" +
            "Se4tL+DcN3Tjz/cwjRS9Y3353PBBqQtwWfQeDd0niuXqYVoQ4ZiSMD/wo5yDVwRB\n" +
            "lfW+XNI5ePXeYlHSH8r7uELaZoGMJNxXiwVvyY6lvYxkPo9go79AoXXQXebCzPV/\n" +
            "S6CbeF2DE590h4+XSQqHleHAP25UhpwuJdwaDFcWs5o+BQTT6sDClJVWx/EbgoR+\n" +
            "nYVXVBShR1xZzZA9mDWztl9iRa0VENKSP5l0tx6oe98MRp8bmH+SVQPHBhX4kjcn\n" +
            "qn1DjBpVnNLaHo6lgDAsHrm0+ThfDFYCQ8UIvGKFoRPo8+fHz96XEzbq5wAZ+vVc\n" +
            "MQIDAQAB\n" +
            "-----END PUBLIC KEY-----";

    @Bean
    public JwtParser jwtParser() {
        return Jwts.parserBuilder()
                .setSigningKey(getRSAPublicKey())
                .build();
    }

    private RSAPublicKey getRSAPublicKey() {
        try {
            String publicKeyPEM = PUBLIC_KEY
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyPEM);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load RSA public key", e);
        }
    }

    // Method to validate and parse token
    public Claims parseToken(String token) {
        try {
            return jwtParser().parseClaimsJws(token).getBody();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    // Helper method to get user role from token
    public String getUserRole(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }

    // Helper method to get user email from token
    public String getUserEmail(String token) {
        Claims claims = parseToken(token);
        return claims.get("email", String.class);
    }

    // Helper method to get user password from token
    public String getUserPassword(String token) {
        Claims claims = parseToken(token);
        return claims.get("password", String.class);
    }

    // Helper method to check if token is expired
    public boolean isTokenExpired(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().before(new Date());
    }
}