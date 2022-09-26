package com.cst438.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
public class StudentController {

	@Autowired
	StudentRepository studentRepository;

	@PostMapping("/students")
	public Student addStudent(@RequestParam("email") String email, @RequestParam("name") String name) {
		Student student = studentRepository.findByEmail(email);

		if (student == null) {
			Student newStudent = new Student();
			newStudent.setEmail(email);
			newStudent.setName(name);
			
			Student savedStudent = studentRepository.save(newStudent);

			return savedStudent;
		}
		return student;
	}
	
	@PutMapping("/students/status")
	public Student changeStudentStatus(@RequestParam("email") String email, @RequestParam("status") String status) {
		Student student = studentRepository.findByEmail(email);
		
		if (student == null) {
			System.out.println("/schedule student not found. "+email);
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student not found. " );
		}
		student.setStatus(status);
		Student savedStudent = studentRepository.save(student);
		return savedStudent;
	}

}