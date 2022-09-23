package com.cst438.domain;

public class StudentDTO {
	public String email;
	public String name;
	public String status;
	public int status_code;
	public int student_id;
	
	@Override
	public String toString() {
		return "[email=" + email + ", name=" + name;
	}
	
	public String getEmail() { return email; }
	public String getName() { return name; }
}

