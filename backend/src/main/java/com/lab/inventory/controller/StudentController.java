package com.lab.inventory.controller;

import com.lab.inventory.entity.Student;
import com.lab.inventory.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @PostMapping
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.addStudent(student));
    }
    
    @GetMapping("/scan")
    public ResponseEntity<Student> getStudentByQr(@RequestParam String qr) {
        Student s = studentService.getStudentByQr(qr);
        if(s != null) return ResponseEntity.ok(s);
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{usn}")
    public ResponseEntity<Student> getStudentByUsn(@PathVariable String usn) {
        Student s = studentService.getStudentByUsn(usn);
        if (s == null) s = studentService.getStudentByQr(usn);
        if (s == null && usn.startsWith("STD_")) s = studentService.getStudentByUsn(usn.substring(4));
        if (s != null) return ResponseEntity.ok(s);
        return ResponseEntity.notFound().build();
    }
}
