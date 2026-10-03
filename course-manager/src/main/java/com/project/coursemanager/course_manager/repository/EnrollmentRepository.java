package com.project.coursemanager.course_manager.repository;

import com.project.coursemanager.course_manager.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.time.LocalDate;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    void deleteByStudentId(Long studentId);
    void deleteByCourseId(Long courseId);
}
