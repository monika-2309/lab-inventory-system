package com.lab.inventory.controller;

import com.lab.inventory.entity.Component;
import com.lab.inventory.entity.Transaction;
import com.lab.inventory.service.ComponentService;
import com.lab.inventory.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private ComponentService componentService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public Map<String, Object> getDashboardStats() {
        List<Component> components = componentService.getAllComponents();
        List<Transaction> transactions = transactionService.getAllTransactions();

        long issuedComponents = transactions.stream().filter(t -> "ISSUED".equals(t.getStatus())).count();
        long availableStock = components.stream().mapToInt(Component::getQuantity).sum();
        long totalComponents = availableStock + issuedComponents;
        
        long lateReturns = transactions.stream().filter(t -> "LATE".equals(t.getStatus())).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalComponents", totalComponents);
        stats.put("availableStock", availableStock);
        stats.put("issuedComponents", issuedComponents);
        stats.put("lateReturns", lateReturns);
        
        stats.put("totalComponentTypes", components.size());

        return stats;
    }

    @GetMapping("/stats")
    public Map<String, Object> getChartStats() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        long totalIssued = transactions.stream().filter(t -> "ISSUED".equals(t.getStatus())).count();
        long totalReturned = transactions.stream().filter(t -> "RETURNED".equals(t.getStatus()) || "LATE".equals(t.getStatus())).count();

        Map<String, Long> dailyUsageMap = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getIssueTime().format(DateTimeFormatter.ISO_LOCAL_DATE),
                        Collectors.counting()
                ));

        List<Map<String, Object>> dailyUsage = dailyUsageMap.entrySet().stream()
                .map(e -> Map.of("date", (Object)e.getKey(), "count", (Object)e.getValue()))
                .sorted((m1, m2) -> ((String)m1.get("date")).compareTo((String)m2.get("date")))
                .collect(Collectors.toList());

        return Map.of(
                "totalIssued", totalIssued,
                "totalReturned", totalReturned,
                "dailyUsage", dailyUsage
        );
    }

    @GetMapping("/alerts")
    public Map<String, Object> getAlerts() {
        List<Component> lowStock = componentService.getAllComponents().stream()
                .filter(c -> c.getQuantity() < 5)
                .collect(Collectors.toList());
        
        List<Transaction> lateReturns = transactionService.getAllTransactions().stream()
                .filter(t -> "ISSUED".equals(t.getStatus()) && t.getIssueTime().isBefore(LocalDateTime.now().minusDays(2)))
                .collect(Collectors.toList());

        return Map.of(
                "lowStock", lowStock,
                "lateReturns", lateReturns
        );
    }
}
