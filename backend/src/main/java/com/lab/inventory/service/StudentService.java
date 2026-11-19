package com.lab.inventory.service;

import com.lab.inventory.entity.Student;
import com.lab.inventory.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    public Student addStudent(Student student) {
        student.setQrCodeText("STD_" + student.getUsn());
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    public Student getStudentByQr(String qrCodeText) {
        return studentRepository.findByQrCodeText(qrCodeText);
    }
    
    public Student getStudentByUsn(String usn) {
        return studentRepository.findByUsn(usn);
    }
}
