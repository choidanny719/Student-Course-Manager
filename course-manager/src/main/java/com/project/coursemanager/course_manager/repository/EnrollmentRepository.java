package com.project.coursemanager.course_manager.repository;

import com.project.coursemanager.course_manager.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
}