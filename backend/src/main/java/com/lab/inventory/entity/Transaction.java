package com.lab.inventory.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_usn", referencedColumnName = "usn", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "component_id", referencedColumnName = "component_id", nullable = false)
    private Component component;

    @Column(name = "issue_time", nullable = false)
    private LocalDateTime issueTime;

    @Column(name = "return_time")
    private LocalDateTime returnTime;

    @Column(nullable = false)
    private String status; // 'ISSUED', 'RETURNED'
}
