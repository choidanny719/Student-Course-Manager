package com.project.coursemanager.course_manager.service;

import com.project.coursemanager.course_manager.model.Assignment;
import com.project.coursemanager.course_manager.model.Course;
import com.project.coursemanager.course_manager.repository.AssignmentRepository;
import com.project.coursemanager.course_manager.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;

    public CourseService(CourseRepository courseRepository,
            AssignmentRepository assignmentRepository) {
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course getCourse(Long courseId) {
        return courseRepository.findById(courseId).orElseThrow();
    }

    public Assignment saveAssignment(Long courseId, Assignment assignment) {
        Course course = courseRepository.findById(courseId).orElseThrow();

        assignment.setCourse(course);

        return assignmentRepository.save(assignment);
    }

    public List<Assignment> getAssignments(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        return course.getAssignments();
    }

    public Assignment completeAssignment(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow();

        assignment.setCompleted(true);

        return assignmentRepository.save(assignment);
    }

    public List<Assignment> getOverdueAssignments() {
        return assignmentRepository.findByDueDateBeforeAndCompletedFalse(LocalDate.now());
    }

    public List<Assignment> getUpcomingAssignments(int days) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        return assignmentRepository.findByDueDateBetweenAndCompletedFalse(
                today,
                endDate);
    }

    public Assignment updateAssignment(Long assignmentId, Assignment updatedAssignment) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow();

        assignment.setTitle(updatedAssignment.getTitle());
        assignment.setDescription(updatedAssignment.getDescription());
        assignment.setDueDate(updatedAssignment.getDueDate());

        return assignmentRepository.save(assignment);
    }

    public void deleteAssignment(Long assignmentId) {
        assignmentRepository.deleteById(assignmentId);
    }
}