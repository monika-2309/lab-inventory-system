package com.lab.inventory.repository;

import com.lab.inventory.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByUsn(String usn);
    Student findByQrCodeText(String qrCodeText);
}
