package com.cst438.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cst438.domain.CourseDTOG;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;

@RestController
public class CourseController {
	
	@Autowired
	EnrollmentRepository enrollmentRepository;
	
	/*
	 * endpoint used by gradebook service to transfer final course grades
	 */
	@PutMapping("/course/{course_id}")
	@Transactional
	public void updateCourseGrades( @RequestBody CourseDTOG courseDTO, @PathVariable("course_id") int course_id) {
		List<CourseDTOG.GradeDTO> grades = courseDTO.grades;
		// GradeDTO contains email, name, and grade
		// Enrollment table columns 
		/* enrollment_id | student_id | year | semester | course_id | course_grade */
		for(int i = 0; i < grades.size(); ++i) {
			// Get a grade object, find the student id and update the enrollment
			String newEmail = grades.get(i).student_email;
			String newGrade = grades.get(i).grade;
			Enrollment en = enrollmentRepository.findByEmailAndCourseId(newEmail, course_id);
			en.setCourseGrade(newGrade);
			enrollmentRepository.save(en);
		}
	}

}
