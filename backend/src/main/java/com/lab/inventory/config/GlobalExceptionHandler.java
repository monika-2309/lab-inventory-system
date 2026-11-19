package com.lab.inventory.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> response = new HashMap<>();
        if (ex.getMessage() != null && (ex.getMessage().contains("usn") || ex.getMessage().contains("students"))) {
            response.put("error", "A student with this USN is already registered!");
        } else if (ex.getMessage() != null && (ex.getMessage().contains("components") || ex.getMessage().contains("component_id"))) {
            response.put("error", "Component constraint violation. Are you sending duplicate data?");
        } else {
            response.put("error", "Duplicate entry or data integrity violation.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Internal Server Error: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
