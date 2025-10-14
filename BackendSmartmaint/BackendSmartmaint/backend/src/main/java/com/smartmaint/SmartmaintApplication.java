package com.smartmaint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication(scanBasePackages = "com.smartmaint")
public class SmartmaintApplication {

    public static void main(String[] args) {
        System.out.println("🚀 Iniciando Smartmaint backend...");
        SpringApplication.run(SmartmaintApplication.class, args);
        System.out.println("✅ Backend Smartmaint activo y escuchando");
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
