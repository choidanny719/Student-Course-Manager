package com.project.coursemanager.course_manager.controller;

import static com.project.coursemanager.course_manager.api.ApiModels.*;
import com.project.coursemanager.course_manager.service.CourseService;
import jakarta.validation.Valid;
import java.util.List;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {
    private final CourseService service;
    public AssignmentController(CourseService service) { this.service = service; }

    @GetMapping
    public List<AssignmentView> list(@RequestParam(required = false) Long courseId,
                                    @RequestParam(defaultValue = "all") String status,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                    @RequestParam(required = false) Integer days) {
        return service.filterAssignments(courseId, status, from, to, days);
    }

    @PatchMapping("/{id}/completion")
    public AssignmentView completion(@PathVariable Long id, @Valid @RequestBody CompletionInput input) {
        return service.setCompleted(id, input.completed());
    }

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
