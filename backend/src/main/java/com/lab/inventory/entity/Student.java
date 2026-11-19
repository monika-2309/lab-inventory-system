package com.lab.inventory.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String usn;

    @Column(nullable = false)
    private String branch;

    @Column(name = "qr_code_text", unique = true)
    private String qrCodeText;
}
