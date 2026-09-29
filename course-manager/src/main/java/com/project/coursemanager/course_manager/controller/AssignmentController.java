package com.project.coursemanager.course_manager.controller;

import com.project.coursemanager.course_manager.model.Assignment;
import com.project.coursemanager.course_manager.service.CourseService;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {

    private final CourseService courseService;

    public AssignmentController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PutMapping("/{assignmentId}/complete")
    public Assignment completeAssignment(@PathVariable Long assignmentId) {
        return courseService.completeAssignment(assignmentId);
    }

    @GetMapping("/overdue")
    public List<Assignment> getOverdueAssignments() {
        return courseService.getOverdueAssignments();
    }

    @GetMapping("/upcoming")
    public List<Assignment> getUpcomingAssignments(@RequestParam int days) {
        return courseService.getUpcomingAssignments(days);
    }

    @PutMapping("/{assignmentId}")
    public Assignment updateAssignment(
            @PathVariable Long assignmentId,
            @RequestBody Assignment assignment) {

        return courseService.updateAssignment(assignmentId, assignment);
    }

    @DeleteMapping("/{assignmentId}")
    public void deleteAssignment(@PathVariable Long assignmentId) {
        courseService.deleteAssignment(assignmentId);
    }
}