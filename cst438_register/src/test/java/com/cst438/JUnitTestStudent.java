package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cst438.controller.StudentController;
import com.cst438.domain.Enrollment;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ContextConfiguration(classes = { StudentController.class })
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class JUnitTestStudent {
	static final String URL = "http://localhost:8080";
	public static final String TEST_STUDENT_EMAIL = "test@csumb.edu";
	public static final String TEST_STUDENT_NAME  = "test";
	public static final String TEST_STUDENT_STATUS_HOLD  = "HOLD";
	public static final String TEST_STUDENT_STATUS_ENROLLED  = "ENROLLED";
	
	@MockBean
	StudentRepository studentRepository;

	@Autowired
	private MockMvc mvc;
	
	@Test
	public void addStudent() throws Exception {
		
		MockHttpServletResponse response;
		
		Student student = new Student();
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setName(TEST_STUDENT_NAME);
		student.setStatusCode(0);
		student.setStudent_id(1);
		
		// given  -- stubs for database repositories that return test data
	    given(studentRepository.save(any(Student.class))).willReturn(student);
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/students?email=" + TEST_STUDENT_EMAIL + "&name=" + TEST_STUDENT_NAME)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
		
		Student returnedStudent = fromJsonString(response.getContentAsString(), Student.class);
		assertEquals(student.getEmail(), returnedStudent.getEmail());
		assertEquals(student.getName(), returnedStudent.getName());
		assertEquals(student.getStatusCode(), returnedStudent.getStatusCode());
		assertEquals(student.getStudent_id(), returnedStudent.getStudent_id());
		
		// verify that repository save method was called.
		verify(studentRepository).save(any(Student.class));
	}
	
	@Test
	public void addHold() throws Exception {
		
		MockHttpServletResponse response;
		MockHttpServletResponse statusResponse;
		
		Student student = new Student();
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setName(TEST_STUDENT_NAME);
		student.setStatusCode(0);
		student.setStudent_id(1);
		
	    given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
	    given(studentRepository.save(any(Student.class))).willReturn(student);
		
	    // add student
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/students?email=" + TEST_STUDENT_EMAIL + "&name=" + TEST_STUDENT_NAME)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());

		student.setStatus(TEST_STUDENT_STATUS_HOLD);
		
		// place HOLD status on student
		statusResponse = mvc.perform(
				MockMvcRequestBuilders
			      .put("/students/status?email=" + TEST_STUDENT_EMAIL + "&status=" + TEST_STUDENT_STATUS_HOLD)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		assertEquals(200, statusResponse.getStatus());
		
		Student returnedStudent = fromJsonString(statusResponse.getContentAsString(), Student.class);
		assertEquals(student.getEmail(), returnedStudent.getEmail());
		assertEquals(student.getName(), returnedStudent.getName());
		assertEquals(student.getStatus(), returnedStudent.getStatus());
		assertEquals(student.getStatusCode(), returnedStudent.getStatusCode());
		assertEquals(student.getStudent_id(), returnedStudent.getStudent_id());
		
		// verify that repository save method was called.
		verify(studentRepository).save(any(Student.class));
	}

	@Test
	public void removeHold() throws Exception {
		
		MockHttpServletResponse response;
		MockHttpServletResponse holdResponse;
		MockHttpServletResponse enrolledResponse;
		
		Student student = new Student();
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setName(TEST_STUDENT_NAME);
		student.setStatusCode(0);
		student.setStudent_id(1);
		
	    given(studentRepository.save(any(Student.class))).willReturn(student);
	    given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
		
	    // add student
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/students?email=" + TEST_STUDENT_EMAIL + "&name=" + TEST_STUDENT_NAME)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());

		// place HOLD status on student
		holdResponse = mvc.perform(
				MockMvcRequestBuilders
			      .put("/students/status?email=" + TEST_STUDENT_EMAIL + "&status=" + TEST_STUDENT_STATUS_HOLD)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		assertEquals(200, holdResponse.getStatus());
		
		student.setStatus(TEST_STUDENT_STATUS_ENROLLED);
		
		// change status on student
		enrolledResponse = mvc.perform(
				MockMvcRequestBuilders
			      .put("/students/status?email=" + TEST_STUDENT_EMAIL + "&status=" + TEST_STUDENT_STATUS_ENROLLED)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		assertEquals(200, enrolledResponse.getStatus());
		
		Student returnedStudent = fromJsonString(enrolledResponse.getContentAsString(), Student.class);
		assertEquals(student.getEmail(), returnedStudent.getEmail());
		assertEquals(student.getName(), returnedStudent.getName());
		assertEquals(student.getStatus(), returnedStudent.getStatus());
		assertEquals(student.getStatusCode(), returnedStudent.getStatusCode());
		assertEquals(student.getStudent_id(), returnedStudent.getStudent_id());
	}

	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}













