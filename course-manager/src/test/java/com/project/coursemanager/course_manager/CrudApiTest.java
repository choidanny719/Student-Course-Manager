package com.project.coursemanager.course_manager;

import static com.project.coursemanager.course_manager.api.ApiModels.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class CrudApiTest extends ApiTestSupport {
    @Test
    void courseCanBeCreatedReadUpdatedAndDeleted() {
        var response = client.post().uri("/courses").body(new CourseInput(" cmpt307 ", " Algorithms ", null))
                .retrieve().toEntity(CourseView.class);
        assertEquals(201, response.getStatusCode().value());
        var created = response.getBody();
        assertEquals("CMPT307", created.courseCode());
        assertEquals("Algorithms", created.name());
        assertEquals("", created.description());
        assertEquals(created, client.get().uri("/courses/" + created.id()).retrieve().body(CourseView.class));
        var updated = client.put().uri("/courses/" + created.id())
                .body(new CourseInput("CMPT310", "AI", "Search and planning")).retrieve().body(CourseView.class);
        assertEquals("AI", updated.name());
        assertEquals(1, client.get().uri("/courses").retrieve().body(CourseView[].class).length);
        assertEquals(204, client.delete().uri("/courses/" + created.id()).retrieve().toBodilessEntity().getStatusCode().value());
        assertStatus(404, () -> client.get().uri("/courses/" + created.id()).retrieve().toBodilessEntity());
    }

    @Test
    void studentsCanBeEditedAndEmailsAreUnique() {
        var first = student(" Alex@Example.com ");
        assertEquals("alex@example.com", first.email());
        assertStatus(409, () -> student("ALEX@example.com"));
        var second = student("other@example.com");
        assertStatus(409, () -> client.put().uri("/students/" + second.id())
                .body(new StudentInput("Other", first.email())).retrieve().toBodilessEntity());
        var updated = client.put().uri("/students/" + first.id())
                .body(new StudentInput("Alex Kim", "alex.kim@example.com")).retrieve().body(StudentView.class);
        assertEquals("Alex Kim", updated.name());
        assertEquals(updated, client.get().uri("/students/" + first.id()).retrieve().body(StudentView.class));
        client.delete().uri("/students/" + first.id()).retrieve().toBodilessEntity();
        assertEquals(1, client.get().uri("/students").retrieve().body(StudentView[].class).length);
    }

    @Test
    void assignmentsHaveFlatResponsesAndCompleteCrud() {
        var course = course("CMPT307");
        var created = assignment(course.id(), "Homework", LocalDate.of(2026, 11, 2), false);
        assertEquals(course.id(), created.courseId());
        assertEquals("CMPT307", created.courseCode());
        assertFalse(created.completed());
        assertEquals(created, client.get().uri("/assignments/" + created.id()).retrieve().body(AssignmentView.class));
        var updated = client.put().uri("/assignments/" + created.id())
                .body(new AssignmentInput("Problem set", "Chapter 4", LocalDate.of(2026, 11, 3), true))
                .retrieve().body(AssignmentView.class);
        assertEquals("Problem set", updated.title());
        assertTrue(updated.completed());
        assertEquals(1, client.get().uri("/courses/" + course.id() + "/assignments")
                .retrieve().body(AssignmentView[].class).length);
        client.delete().uri("/assignments/" + created.id()).retrieve().toBodilessEntity();
        assertStatus(404, () -> client.get().uri("/assignments/" + created.id()).retrieve().toBodilessEntity());
    }

    @Test
    void enrollmentsCanBeMovedAndDuplicatesAreRejected() {
        var student = student("alex@example.com");
        var first = course("CMPT307");
        var second = course("CMPT310");
        var input = new EnrollmentInput(student.id(), first.id());
        var created = client.post().uri("/enrollments").body(input).retrieve().body(EnrollmentView.class);
        assertEquals(student.name(), created.studentName());
        assertStatus(409, () -> client.post().uri("/enrollments").body(input).retrieve().toBodilessEntity());
        var updated = client.put().uri("/enrollments/" + created.id())
                .body(new EnrollmentInput(student.id(), second.id())).retrieve().body(EnrollmentView.class);
        assertEquals(second.id(), updated.courseId());
        assertEquals(updated, client.get().uri("/enrollments/" + created.id()).retrieve().body(EnrollmentView.class));
        assertEquals(0, client.get().uri("/enrollments?courseId=" + first.id()).retrieve().body(EnrollmentView[].class).length);
        client.delete().uri("/enrollments/" + created.id()).retrieve().toBodilessEntity();
        assertEquals(0, enrollments.count());
    }

    @Test
    void deletingCourseRemovesOnlyItsAssignmentsAndEnrollments() {
        var student = student("alex@example.com");
        var first = course("CMPT307");
        var second = course("CMPT310");
        assignment(first.id(), "First", LocalDate.now(), false);
        assignment(second.id(), "Second", LocalDate.now(), false);
        client.post().uri("/enrollments").body(new EnrollmentInput(student.id(), first.id())).retrieve().toBodilessEntity();
        client.delete().uri("/courses/" + first.id()).retrieve().toBodilessEntity();
        assertEquals(1, assignments.count());
        assertEquals(0, enrollments.count());
        assertEquals(1, students.count());
        assertEquals(1, courses.count());
    }

    @Test
    void deletingStudentRemovesEnrollmentAndPreservesCoursework() {
        var student = student("alex@example.com");
        var course = course("CMPT307");
        assignment(course.id(), "First", LocalDate.now(), false);
        client.post().uri("/enrollments").body(new EnrollmentInput(student.id(), course.id())).retrieve().toBodilessEntity();
        client.delete().uri("/students/" + student.id()).retrieve().toBodilessEntity();
        assertEquals(0, enrollments.count());
        assertEquals(1, assignments.count());
        assertEquals(1, courses.count());
    }

    @Test
    void invalidInputAndDuplicateCodesHaveUsefulErrors() {
        assertStatus(400, () -> course(" "));
        assertStatus(400, () -> student("bad-email"));
        assertStatus(400, () -> course("A".repeat(25)));
        var course = course("CMPT307");
        assertStatus(409, () -> course("cmpt307"));
        assertStatus(400, () -> assignment(course.id(), "Missing date", null, false));
        assertStatus(400, () -> client.post().uri("/courses").contentType(MediaType.APPLICATION_JSON)
                .body("{").retrieve().toBodilessEntity());
        assertStatus(400, () -> client.post().uri("/enrollments").body(new EnrollmentInput(null, course.id()))
                .retrieve().toBodilessEntity());
    }

    @Test
    void missingParentsAndResourcesReturnNotFound() {
        assertStatus(404, () -> assignment(999L, "Homework", LocalDate.now(), false));
        assertStatus(404, () -> client.post().uri("/enrollments").body(new EnrollmentInput(999L, 999L))
                .retrieve().toBodilessEntity());
        for (String resource : new String[]{"courses", "students", "assignments", "enrollments"}) {
            assertStatus(404, () -> client.delete().uri("/" + resource + "/999").retrieve().toBodilessEntity());
        }
    }
}
