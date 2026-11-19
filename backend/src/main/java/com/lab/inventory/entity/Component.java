package com.lab.inventory.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "components")
public class Component {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "component_id", unique = true, nullable = false)
    private String componentId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(name = "qr_code_text", unique = true)
    private String qrCodeText;
}
