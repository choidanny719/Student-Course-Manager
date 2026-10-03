package com.project.coursemanager.course_manager.service;

import static com.project.coursemanager.course_manager.api.ApiModels.*;
import com.project.coursemanager.course_manager.api.ApiException;
import com.project.coursemanager.course_manager.model.Enrollment;
import com.project.coursemanager.course_manager.repository.*;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EnrollmentService {
    private final EnrollmentRepository enrollments;
    private final StudentRepository students;
    private final CourseRepository courses;

    public EnrollmentService(EnrollmentRepository enrollments, StudentRepository students, CourseRepository courses) {
        this.enrollments = enrollments;
        this.students = students;
        this.courses = courses;
    }

    @Transactional(readOnly = true)
    public List<EnrollmentView> list(Long studentId, Long courseId) {
        return enrollments.findAll(Sort.by("id")).stream()
                .filter(item -> studentId == null || item.getStudent().getId().equals(studentId))
                .filter(item -> courseId == null || item.getCourse().getId().equals(courseId))
                .map(EnrollmentView::from).toList();
    }

    @Transactional(readOnly = true)
    public EnrollmentView get(Long id) { return EnrollmentView.from(find(id)); }

    public EnrollmentView create(EnrollmentInput input) {
        checkDuplicate(input, null);
        var student = students.findById(input.studentId()).orElseThrow(() -> ApiException.notFound("Student"));
        var course = courses.findById(input.courseId()).orElseThrow(() -> ApiException.notFound("Course"));
        return EnrollmentView.from(enrollments.save(new Enrollment(student, course)));
    }

    public EnrollmentView update(Long id, EnrollmentInput input) {
        Enrollment enrollment = find(id);
        checkDuplicate(input, id);
        var student = students.findById(input.studentId()).orElseThrow(() -> ApiException.notFound("Student"));
        var course = courses.findById(input.courseId()).orElseThrow(() -> ApiException.notFound("Course"));
        enrollment.update(student, course);
        return EnrollmentView.from(enrollment);
    }

    public void delete(Long id) { enrollments.delete(find(id)); }

    private Enrollment find(Long id) {
        return enrollments.findById(id).orElseThrow(() -> ApiException.notFound("Enrollment"));
    }

    private void checkDuplicate(EnrollmentInput input, Long id) {
        enrollments.findByStudentIdAndCourseId(input.studentId(), input.courseId())
                .filter(item -> !item.getId().equals(id)).ifPresent(item -> {
                    throw ApiException.conflict("This student is already enrolled in that course");
                });
    }
}
