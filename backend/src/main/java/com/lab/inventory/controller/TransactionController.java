package com.lab.inventory.controller;

import com.lab.inventory.entity.Transaction;
import com.lab.inventory.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/api/transactions/history")
    public List<Transaction> getHistory() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/api/transactions/recent")
    public List<Map<String, String>> getRecentTransactions() {
        return transactionService.getRecentTransactions().stream().map(t -> {
            String name = t.getStudent().getName();
            String component = t.getComponent().getName();
            String status = t.getStatus();
            String action = "RETURNED".equals(status) || "LATE".equals(status) ? "returned" : "issued";
            return java.util.Map.of(
                "message", name + " " + action + " " + component,
                "time", t.getIssueTime().toString()
            );
        }).collect(java.util.stream.Collectors.toList());
    }

    @PostMapping("/api/issue")
    public ResponseEntity<?> issueComponent(@RequestBody Map<String, String> payload) {
        try {
            String studentUsn = payload.get("student_usn");
            String componentId = payload.get("component_id");
            Transaction t = transactionService.issueComponent(studentUsn, componentId);
            messagingTemplate.convertAndSend("/topic/activity", Map.of("message", t.getStudent().getName() + " issued " + t.getComponent().getName(), "time", java.time.LocalDateTime.now().toString()));
            return ResponseEntity.ok(t);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/return")
    public ResponseEntity<?> returnComponent(@RequestBody Map<String, String> payload) {
        try {
            String studentUsn = payload.get("student_usn");
            String componentId = payload.get("component_id");
            Transaction t = transactionService.returnComponent(studentUsn, componentId);
            messagingTemplate.convertAndSend("/topic/activity", Map.of("message", t.getStudent().getName() + " returned " + t.getComponent().getName(), "time", java.time.LocalDateTime.now().toString()));
            return ResponseEntity.ok(t);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
