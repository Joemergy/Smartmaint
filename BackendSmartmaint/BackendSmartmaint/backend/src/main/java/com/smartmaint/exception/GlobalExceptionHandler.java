package com.smartmaint.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        System.out.println("💥 Excepción global: " + e.getClass().getSimpleName());
        e.printStackTrace();

        if (TransactionAspectSupport.currentTransactionStatus().isRollbackOnly()) {
            System.out.println("⚠️ Transacción marcada como rollback-only. Se ha registrado rollback.");
        }

        return ResponseEntity.status(500).body("Error interno en el servidor. Consulta los logs para más detalles.");
    }
}
