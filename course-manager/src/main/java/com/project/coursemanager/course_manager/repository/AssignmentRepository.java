package com.project.coursemanager.course_manager.repository;

import com.project.coursemanager.course_manager.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.*;
import java.time.LocalDate;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCourseIdOrderByDueDateAscIdAsc(Long courseId);
    List<Assignment> findByDueDateBeforeAndCompletedFalseOrderByDueDateAscIdAsc(LocalDate date);
    List<Assignment> findByDueDateBetweenAndCompletedFalseOrderByDueDateAscIdAsc(LocalDate start, LocalDate end);
    void deleteByCourseId(Long courseId);

    @Query("""
            select a from Assignment a
            where (:courseId is null or a.course.id = :courseId)
              and (:fromDate is null or a.dueDate >= :fromDate)
              and (:toDate is null or a.dueDate <= :toDate)
              and (:completed is null or a.completed = :completed)
            order by a.dueDate, a.id
            """)
    List<Assignment> filter(Long courseId, LocalDate fromDate, LocalDate toDate, Boolean completed);
}
