package com.cst438.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://registerf-cst438.herokuapp.com/"})
public class RegistrationController {
	
	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	EnrollmentRepository enrollmentRepository;
	
	@Autowired
	GradebookService gradebookService;
	
	/* 
	 * As an administrator, I can add a student to the system.  I input the student email and name.  
	 * The student email must not already exists in the system.
	 */
	@PostMapping("/addStudent")
	@Transactional
	private void addStudent(String email, String name) {
		String student_email = email;   		// student's email 
		Student check_student = studentRepository.findByEmail(student_email);
		if (check_student == null) { 			// Student should not exist in db
			Student student = new Student();
			student.setName(name);
			student.setEmail(student_email);
			student.setStatus("Good Standing"); // For now, we'll say a new student has "good standing" 
			student.setStatusCode(0); 			// again, 0 == good standing for now
			student.setStudent_id(90001); 		// Placeholder id for now 
			Student savedStudent = studentRepository.save(student);
		} 
		else throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student with email: " + student_email + " Already Enrolled");
	}
	
	/* 
	 * As an administrator, I can put student registration on HOLD.
	 */
	// TODO: setHold and releaseHold could probably be condensed into one function, assuming the only relevant info in 
	//		 the status field is about student holds
	@PostMapping("/setHold")
	@Transactional
	private int setHold(String email) {
		Student student = studentRepository.findByEmail(email);
		if (student != null)  {						// Student should exist in db
			if (student.getStatusCode() == -1) {	// Student should not already have a hold 
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student with email: " + email + " Already has active hold");
			}
			student.setStatus("HOLD");
			student.setStatusCode(-1);
			Student savedStudent = studentRepository.save(student);
			return -1;
		}
		else throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student with email: " + email + " Doesn't exist");
	}
	
	/*
	 * As an administrator, I can release the HOLD on student registration.
	 */
	@PostMapping("/releaseHold")
	@Transactional
	private int releaseHold(String email) {
		Student student = studentRepository.findByEmail(email);
		if (student != null)  {						// Student should exist in db
			if (student.getStatusCode() != -1) {	// Student should have a hold to release 
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student with email: " + email + " has no active hold");
			}
			student.setStatus("Good Standing");
			student.setStatusCode(0);
			Student savedStudent = studentRepository.save(student);
			return 0;
		}
		else throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student with email: " + email + " Doesn't exist");
	}
}
