package com.project.coursemanager.course_manager.controller;

import com.project.coursemanager.course_manager.model.Enrollment;
import com.project.coursemanager.course_manager.repository.CourseRepository;
import com.project.coursemanager.course_manager.repository.EnrollmentRepository;
import com.project.coursemanager.course_manager.repository.StudentRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentController(
            EnrollmentRepository enrollmentRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository) {

        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @PostMapping
    public Enrollment createEnrollment(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {

        var student = studentRepository.findById(studentId).orElseThrow();
        var course = courseRepository.findById(courseId).orElseThrow();

        Enrollment enrollment = new Enrollment(student, course);

        return enrollmentRepository.save(enrollment);
    }
}