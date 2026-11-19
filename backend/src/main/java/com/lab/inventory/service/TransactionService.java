package com.lab.inventory.service;

import com.lab.inventory.entity.Component;
import com.lab.inventory.entity.Student;
import com.lab.inventory.entity.Transaction;
import com.lab.inventory.repository.ComponentRepository;
import com.lab.inventory.repository.StudentRepository;
import com.lab.inventory.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ComponentRepository componentRepository;

    public Transaction issueComponent(String studentUsn, String componentId) {
        Student student = studentRepository.findByUsn(studentUsn);
        Component component = componentRepository.findByComponentId(componentId);

        if (student == null || component == null) {
            throw new RuntimeException("Invalid QR");
        }

        // Check if already issued by this student
        if (!transactionRepository.findByStudentUsnAndComponentIdAndStatus(studentUsn, componentId, "ISSUED").isEmpty()) {
            throw new RuntimeException("Already issued");
        }

        if (component.getQuantity() <= 0) {
            throw new RuntimeException("Component not available");
        }

        // Update component quantity accordingly
        component.setQuantity(component.getQuantity() - 1);
        componentRepository.save(component);

        Transaction transaction = new Transaction();
        transaction.setStudent(student);
        transaction.setComponent(component);
        transaction.setIssueTime(LocalDateTime.now());
        transaction.setStatus("ISSUED");

        return transactionRepository.save(transaction);
    }

    public Transaction returnComponent(String studentUsn, String componentId) {
        Student student = studentRepository.findByUsn(studentUsn);
        Component component = componentRepository.findByComponentId(componentId);

        if (student == null || component == null) {
            throw new RuntimeException("Invalid QR");
        }

        List<Transaction> transactions = transactionRepository.findByStudentUsnAndComponentIdAndStatus(studentUsn, componentId, "ISSUED");

        if (transactions.isEmpty()) {
            throw new RuntimeException("Not issued");
        }

        Transaction transaction = transactions.get(0);
        transaction.setReturnTime(LocalDateTime.now());
        
        // Calculate delay, say more than 7 days is late
        LocalDateTime dueDate = transaction.getIssueTime().plusDays(7);
        if (LocalDateTime.now().isAfter(dueDate)) {
            transaction.setStatus("LATE");
        } else {
            transaction.setStatus("RETURNED");
        }

        // Update component quantity accordingly
        component.setQuantity(component.getQuantity() + 1);
        componentRepository.save(component);

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getRecentTransactions() {
        return transactionRepository.findTop5ByOrderByIssueTimeDesc();
    }
}
