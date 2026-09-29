package com.project.coursemanager.course_manager.controller;

import com.project.coursemanager.course_manager.model.Assignment;
import com.project.coursemanager.course_manager.model.Course;
import com.project.coursemanager.course_manager.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return courseService.saveCourse(course);
    }

    @PostMapping("/{courseId}/assignments")
    public Assignment createAssignment(
            @PathVariable Long courseId,
            @RequestBody Assignment assignment) {

        return courseService.saveAssignment(courseId, assignment);
    }

    @GetMapping("/{courseId}/assignments")
    public List<Assignment> getAssignments(@PathVariable Long courseId) {
        return courseService.getAssignments(courseId);
    }
}