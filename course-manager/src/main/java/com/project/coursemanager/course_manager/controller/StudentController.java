package com.project.coursemanager.course_manager.controller;

import static com.project.coursemanager.course_manager.api.ApiModels.*;
import com.project.coursemanager.course_manager.service.StudentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentService service;
    public StudentController(StudentService service) { this.service = service; }

    @GetMapping
    public List<StudentView> list() { return service.list(); }

    @GetMapping("/{id}")
    public StudentView get(@PathVariable Long id) { return service.get(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentView create(@Valid @RequestBody StudentInput input) { return service.create(input); }

    @PutMapping("/{id}")
    public StudentView update(@PathVariable Long id, @Valid @RequestBody StudentInput input) {
        return service.update(id, input);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }

}
