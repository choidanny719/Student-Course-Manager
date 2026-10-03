package com.project.coursemanager.course_manager.api;

import com.project.coursemanager.course_manager.model.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Locale;

public final class ApiModels {
    private ApiModels() {}

    public record StudentInput(@NotBlank @Size(max = 100) String name,
                               @NotBlank @Email @Size(max = 160) String email) {
        public StudentInput {
            name = clean(name);
            email = email == null ? null : email.strip().toLowerCase(Locale.ROOT);
        }
    }

    public record CourseInput(@NotBlank @Size(max = 24) String courseCode,
                              @NotBlank @Size(max = 120) String name,
                              @Size(max = 2000) String description) {
        public CourseInput {
            courseCode = courseCode == null ? null : courseCode.strip().toUpperCase(Locale.ROOT);
            name = clean(name);
            description = description == null ? "" : description.strip();
        }
    }

    public record AssignmentInput(@NotBlank @Size(max = 200) String title,
                                  @Size(max = 5000) String description,
                                  @NotNull LocalDate dueDate, boolean completed) {
        public AssignmentInput {
            title = clean(title);
            description = description == null ? "" : description.strip();
        }
    }

    public record EnrollmentInput(@NotNull @Positive Long studentId,
                                  @NotNull @Positive Long courseId) {}

    public record CompletionInput(@NotNull Boolean completed) {}

    public record StudentView(Long id, String name, String email) {
        public static StudentView from(Student student) {
            return new StudentView(student.getId(), student.getName(), student.getEmail());
        }
    }

    public record CourseView(Long id, String courseCode, String name, String description) {
        public static CourseView from(Course course) {
            return new CourseView(course.getId(), course.getCourseCode(), course.getName(), course.getDescription());
        }
    }

    public record AssignmentView(Long id, Long courseId, String courseCode, String courseName,
                                 String title, String description, LocalDate dueDate, boolean completed) {
        public static AssignmentView from(Assignment assignment) {
            Course course = assignment.getCourse();
            return new AssignmentView(assignment.getId(), course.getId(), course.getCourseCode(), course.getName(),
                    assignment.getTitle(), assignment.getDescription(), assignment.getDueDate(), assignment.isCompleted());
        }
    }

    public record EnrollmentView(Long id, Long studentId, String studentName, String studentEmail,
                                 Long courseId, String courseCode, String courseName) {
        public static EnrollmentView from(Enrollment enrollment) {
            Student student = enrollment.getStudent();
            Course course = enrollment.getCourse();
            return new EnrollmentView(enrollment.getId(), student.getId(), student.getName(), student.getEmail(),
                    course.getId(), course.getCourseCode(), course.getName());
        }
    }

    private static String clean(String value) {
        return value == null ? null : value.strip();
    }
}
