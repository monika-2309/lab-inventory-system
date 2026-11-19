package com.lab.inventory.repository;

import com.lab.inventory.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByUsername(String username);
    Admin findByUsernameIgnoreCase(String username);
}
