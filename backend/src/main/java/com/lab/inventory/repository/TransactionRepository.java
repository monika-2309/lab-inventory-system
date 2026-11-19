package com.lab.inventory.repository;

import com.lab.inventory.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByStatus(String status);
    
    @Query("SELECT t FROM Transaction t WHERE t.student.usn = :usn AND t.component.componentId = :componentId AND t.status = :status")
    List<Transaction> findByStudentUsnAndComponentIdAndStatus(@Param("usn") String usn, @Param("componentId") String componentId, @Param("status") String status);

    List<Transaction> findTop5ByOrderByIssueTimeDesc();
}
