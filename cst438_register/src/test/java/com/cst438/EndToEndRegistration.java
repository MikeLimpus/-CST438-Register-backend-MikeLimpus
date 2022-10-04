package com.cst438;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;

@SpringBootTest
public class EndToEndRegistration {
    public static final String FIREFOX_DRIVER_LOCATION = 
        "/Users/mikelimpus/Code/SchoolCode/CST 438/Module5/HW5/geckodriver";
    public static final String FIREFOX_DRIVER = "webdriver.gecko.driver";
    public static final String URL = "http://localhost:3000";
    public static final String TEST_STUDENT_EMAIL = "test@csumb.edu";
    public static final int TEST_STUDENT_ID = 54321; 
    public static final String TEST_STUDENT_NAME = "Test";
    public static final int SLEEP_DURATION = 1000; // 1 second.

    @Autowired
	EnrollmentRepository enrollmentRepository;
	@Autowired
	CourseRepository courseRepository;
    @Autowired
    GradebookService gradebookService;
    @Autowired
    StudentRepository studentRepository;

    /*
     * Test adding student named "Test" to the database
     */
    @Test 
    public void addStudentTest() throws Exception {
        Student testStudent = null;
        do {
            testStudent = studentRepository.findByEmail(TEST_STUDENT_EMAIL);
            if (testStudent != null)
                studentRepository.delete(testStudent);
        } while (testStudent != null);
        // Set the firefox driver
        System.setProperty(FIREFOX_DRIVER, FIREFOX_DRIVER_LOCATION);
	    WebDriver driver = new FirefoxDriver();
	    // If the system does nothing for 10 seconds, something didn't work
	    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        try {
            driver.get(URL);
            Thread.sleep(SLEEP_DURATION);

            // Click the add student button on home page
            driver.findElement(By.xpath("/html/body/div/div/div/div/a[2]")).click();
            Thread.sleep(SLEEP_DURATION);

            driver.findElement(By.name("student_name")).sendKeys(TEST_STUDENT_NAME);
            Thread.sleep(SLEEP_DURATION);
            driver.findElement(By.name("student_email")).sendKeys(TEST_STUDENT_EMAIL);
            Thread.sleep(SLEEP_DURATION);
            driver.findElement(By.id("Add")).click();
            Thread.sleep(SLEEP_DURATION);

            // Show that student was added to db
            Student s = studentRepository.findByEmail(TEST_STUDENT_EMAIL);
            Boolean found = false;
            if (s != null) {
                found = true;
            }

            assertTrue(found, "Course not added");


        } catch(Exception e) {
            throw e;
        } finally {
            Student s = studentRepository.findByEmail(TEST_STUDENT_EMAIL);
            if (s != null) 
                studentRepository.delete(s);
            driver.quit();
        }
    }
    
}
