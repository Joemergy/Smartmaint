package com.smartmaint.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.security.Key;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "SmartmaintClaveUltraSeguraJWT2025++"; // ✅ 36 caracteres
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 horas

    private Key key;

    @PostConstruct
    public void initKey() {
        try {
            byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
            System.out.println("🔑 Longitud de clave en bytes: " + keyBytes.length);

            if (keyBytes.length < 32) {
                throw new IllegalArgumentException("❌ Clave JWT demasiado corta: mínimo 32 bytes requeridos para HS256");
            }

            key = Keys.hmacShaKeyFor(keyBytes);
            System.out.println("✅ Clave JWT inicializada correctamente");
        } catch (Exception e) {
            System.out.println("💥 Error al inicializar clave JWT: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public String generarToken(String correo) {
        System.out.println("🧠 Generando token para: " + correo);

        if (correo == null || correo.isBlank()) {
            throw new IllegalArgumentException("❌ El correo no puede ser nulo o vacío");
        }

        try {
            System.out.println("🔐 Clave usada para firmar: " + key);
            return Jwts.builder()
                    .setSubject(correo)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            System.out.println("💥 Error al generar token: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public String generarTokenConClaims(String correo, Map<String, Object> claims) {
        System.out.println("🧠 Generando token con claims para: " + correo);

        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(correo)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            System.out.println("💥 Error al generar token con claims: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public String extraerCorreo(String token) {
        System.out.println("🔍 Extrayendo correo del token...");
        try {
            return getClaims(token).getSubject();
        } catch (Exception e) {
            System.out.println("💥 Error al extraer correo: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean validarToken(String token) {
        try {
            Claims claims = getClaims(token);
            boolean valido = claims.getExpiration().after(new Date());
            System.out.println("🔐 Token válido: " + valido);
            return valido;
        } catch (Exception e) {
            System.out.println("💥 Token inválido: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.out.println("💥 Error al parsear token: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
