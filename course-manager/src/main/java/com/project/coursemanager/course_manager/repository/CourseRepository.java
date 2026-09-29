package com.project.coursemanager.course_manager.repository;

import com.project.coursemanager.course_manager.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}