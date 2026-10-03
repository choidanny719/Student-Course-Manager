package com.project.coursemanager.course_manager.controller;

import static com.project.coursemanager.course_manager.api.ApiModels.*;
import com.project.coursemanager.course_manager.service.EnrollmentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {
    private final EnrollmentService service;
    public EnrollmentController(EnrollmentService service) { this.service = service; }

    @GetMapping
    public List<EnrollmentView> list(@RequestParam(required = false) Long studentId,
                                     @RequestParam(required = false) Long courseId) {
        return service.list(studentId, courseId);
    }

    @GetMapping("/{id}")
    public EnrollmentView get(@PathVariable Long id) { return service.get(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentView create(@Valid @RequestBody EnrollmentInput input) { return service.create(input); }

    @PutMapping("/{id}")
    public EnrollmentView update(@PathVariable Long id, @Valid @RequestBody EnrollmentInput input) {
        return service.update(id, input);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
