package com.project.coursemanager.course_manager.repository;

import com.project.coursemanager.course_manager.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByDueDateBeforeAndCompletedFalse(LocalDate date);

    List<Assignment> findByDueDateBetweenAndCompletedFalse(
        LocalDate startDate,
        LocalDate endDate);
}