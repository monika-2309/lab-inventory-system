package com.lab.inventory.controller;

import com.lab.inventory.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        if (adminService.authenticate(username, password)) {
            return ResponseEntity.ok(Map.of("message", "Login successful", "success", true));
        }
        return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials", "success", false));
    }
}
