package com.cst438;


import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cst438.controller.RegistrationController;
import com.cst438.controller.ScheduleController;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { RegistrationController.class })
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class JunitTestRegistration {

	static final String URL = "http://localhost:8080";
	public static final int TEST_STUDENT_ID = 90001;
	public static final String TEST_STUDENT_EMAIL = "test@csumb.edu";
	public static final String TEST_STUDENT_NAME  = "Test";
	public static final String TEST_STUDENT_STATUS = "Good Standing";
	public static final int TEST_STUDENT_STATUS_CODE = 0;
	
	CourseRepository courseRepository;

	@MockBean
	StudentRepository studentRepository;

	@MockBean
	EnrollmentRepository enrollmentRepository;

	@MockBean
	GradebookService gradebookService;
	
	@Autowired
	private MockMvc mvc;
	
	@Test
	public void addStudent() throws Exception {
		MockHttpServletResponse response;
		
		Student student = new Student();
		student.setName(TEST_STUDENT_NAME);
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setStatus(TEST_STUDENT_STATUS); 
		student.setStatusCode(TEST_STUDENT_STATUS_CODE); 			
		student.setStudent_id(TEST_STUDENT_ID);
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(null);
		response = mvc.perform(
			MockMvcRequestBuilders
				.post("/addStudent")
				.content(asJsonString(student))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();
			
		assertEquals(200, response.getStatus());
			
		verify(studentRepository, times(1)).save(any(Student.class));
	}
	
	@Test 
	public void setHold() throws Exception {
		MockHttpServletResponse response;
		// Create a test student
		Student student = new Student();
		student.setName(TEST_STUDENT_NAME);
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setStatus(TEST_STUDENT_STATUS); 
		student.setStatusCode(TEST_STUDENT_STATUS_CODE); 			
		student.setStudent_id(TEST_STUDENT_ID); 
		
		student.setStatus("HOLD");
		student.setStatusCode(-1);
		
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
		response = mvc.perform(
				MockMvcRequestBuilders
					.post("/addStudent")
					.content(asJsonString(student))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		
		verify(studentRepository, times(1)).save(any(Student.class));
	}
	
	@Test 
	public void releaseHold() throws Exception {
		MockHttpServletResponse response; // Spring mock object
		// Create a test student
		Student student = new Student();
		student.setName(TEST_STUDENT_NAME);
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setStatus("HOLD");
		student.setStatusCode(-1);		
		student.setStudent_id(TEST_STUDENT_ID); 
		
		student.setStatus("Good Standing"); 
		student.setStatusCode(0); 	
		
		
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
		response = mvc.perform(
				MockMvcRequestBuilders
					.post("/addStudent")
					.content(asJsonString(student))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		
		verify(studentRepository, times(1)).save(any(Student.class));
	}
	
	private static String asJsonString(final Object obj) {
		try {

			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
