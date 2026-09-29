package com.project.coursemanager.course_manager.repository;

import com.project.coursemanager.course_manager.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}