package com.project.coursemanager.course_manager;

import static com.project.coursemanager.course_manager.api.ApiModels.*;
import static org.junit.jupiter.api.Assertions.*;

import com.project.coursemanager.course_manager.repository.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class ApiTestSupport {
    @LocalServerPort int port;
    @Autowired AssignmentRepository assignments;
    @Autowired EnrollmentRepository enrollments;
    @Autowired StudentRepository students;
    @Autowired CourseRepository courses;
    RestClient client;

    @BeforeEach
    void reset() {
        assignments.deleteAllInBatch();
        enrollments.deleteAllInBatch();
        students.deleteAllInBatch();
        courses.deleteAllInBatch();
        client = RestClient.create("http://127.0.0.1:" + port);
    }

    CourseView course(String code) {
        return client.post().uri("/courses").body(new CourseInput(code, "Algorithms", ""))
                .retrieve().body(CourseView.class);
    }

    StudentView student(String email) {
        return client.post().uri("/students").body(new StudentInput("Alex", email))
                .retrieve().body(StudentView.class);
    }

    AssignmentView assignment(Long courseId, String title, LocalDate due, boolean completed) {
        return client.post().uri("/courses/" + courseId + "/assignments")
                .body(new AssignmentInput(title, "", due, completed)).retrieve().body(AssignmentView.class);
    }

    HttpClientErrorException assertStatus(int status, Executable action) {
        var error = assertThrows(HttpClientErrorException.class, action);
        assertEquals(status, error.getStatusCode().value());
        assertTrue(error.getResponseBodyAsString().contains("message"));
        return error;
    }
}
