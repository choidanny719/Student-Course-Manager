package com.project.coursemanager.course_manager.controller;

import static com.project.coursemanager.course_manager.api.ApiModels.*;
import com.project.coursemanager.course_manager.service.CourseService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {
    private final CourseService service;
    public AssignmentController(CourseService service) { this.service = service; }

    @GetMapping("/{id}")
    public AssignmentView get(@PathVariable Long id) { return service.getAssignment(id); }

    @PutMapping("/{id}")
    public AssignmentView update(@PathVariable Long id, @Valid @RequestBody AssignmentInput input) {
        return service.updateAssignment(id, input);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.deleteAssignment(id); }

    @PutMapping("/{id}/complete")
    public AssignmentView complete(@PathVariable Long id) { return service.completeAssignment(id); }

    @GetMapping("/overdue")
    public List<AssignmentView> overdue() { return service.overdue(); }

    @GetMapping("/upcoming")
    public List<AssignmentView> upcoming(@RequestParam(defaultValue = "7") int days) { return service.upcoming(days); }
}
