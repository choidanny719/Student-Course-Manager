package com.project.coursemanager.course_manager.controller;

import static com.project.coursemanager.course_manager.api.ApiModels.*;
import com.project.coursemanager.course_manager.service.CourseService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseService service;
    public CourseController(CourseService service) { this.service = service; }

    @GetMapping
    public List<CourseView> list() { return service.list(); }

    @GetMapping("/{id}")
    public CourseView get(@PathVariable Long id) { return service.get(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseView create(@Valid @RequestBody CourseInput input) { return service.create(input); }

    @PutMapping("/{id}")
    public CourseView update(@PathVariable Long id, @Valid @RequestBody CourseInput input) {
        return service.update(id, input);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }

    @GetMapping("/{id}/assignments")
    public List<AssignmentView> assignments(@PathVariable Long id) { return service.listAssignments(id); }

    @PostMapping("/{id}/assignments")
    @ResponseStatus(HttpStatus.CREATED)
    public AssignmentView addAssignment(@PathVariable Long id, @Valid @RequestBody AssignmentInput input) {
        return service.createAssignment(id, input);
    }

}
