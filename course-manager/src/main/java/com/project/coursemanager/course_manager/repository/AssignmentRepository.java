package com.project.coursemanager.course_manager.repository;

import com.project.coursemanager.course_manager.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.time.LocalDate;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCourseIdOrderByDueDateAscIdAsc(Long courseId);
    List<Assignment> findByDueDateBeforeAndCompletedFalseOrderByDueDateAscIdAsc(LocalDate date);
    List<Assignment> findByDueDateBetweenAndCompletedFalseOrderByDueDateAscIdAsc(LocalDate start, LocalDate end);
    void deleteByCourseId(Long courseId);
}
