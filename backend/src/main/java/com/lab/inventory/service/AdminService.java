package com.lab.inventory.service;

import com.lab.inventory.entity.Admin;
import com.lab.inventory.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    public boolean authenticate(String username, String password) {
        if (username == null || password == null) return false;
        String u = username.trim();
        String p = password.trim();

        Admin admin = adminRepository.findByUsernameIgnoreCase(u);
        if (admin != null) {
            return admin.getPassword().trim().equals(p);
        }
        // Failsafe fallback for default admin
        if ("admin".equalsIgnoreCase(u) && "admin".equals(p)) {
            return true;
        }
        return false;
    }
}
