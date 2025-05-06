package com.example.VoiceTasker.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
public class JwtService {
    private final JwtParser jwtParser;
    private static final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAquEdNC/yDYqLTRskQSID\n" +
            "L8koeCDO3DVm44GBarbfgODViorwu1ov0mzBwGJNWcXHvVQHxsEx/QOs99zriz+X\n" +
            "Gzaztgeu5fkCIdsAH3EL02RMbn1MdnH14AFuJFoO/lldqUVnfW4sqPgUhOQ9TqTE\n" +
            "io2L4wxZNiRbVO0mDVA8wBVzBpMRzRZXvemIzoTER4CHCQMQAKiFrdmNeQgcSyXO\n" +
            "hGTlsuhPEJ1y5MXC3CcuTUPj4biKtNwROHZuaX4CfSemEueTpa1WjDXn60iMUBzf\n" +
            "/Tx/daS0WY6EwIepsBmHoxNTr8b2P/Xlvg7IvI1hp39MgSNGAYxTDSdH7cB5bto6\n" +
            "sQIDAQAB\n" +
            "-----END PUBLIC KEY-----";

    public JwtService(JwtParser jwtParser) {
        this.jwtParser = jwtParser;
    }
    public boolean validateToken(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    public Claims parseToken(String token) {
        try {
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("email", String.class);
    }

    public String getPasswordFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("password", String.class);
    }

    public String getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }

    public boolean hasRole(String token, String role) {
        String tokenRole = getRoleFromToken(token);
        return role.equalsIgnoreCase(tokenRole);
    }

    public boolean isTokenExpired(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().getTime() < System.currentTimeMillis();
    }
}