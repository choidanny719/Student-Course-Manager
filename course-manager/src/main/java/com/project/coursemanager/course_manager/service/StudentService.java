package com.project.coursemanager.course_manager.service;

import static com.project.coursemanager.course_manager.api.ApiModels.*;
import com.project.coursemanager.course_manager.api.ApiException;
import com.project.coursemanager.course_manager.model.Student;
import com.project.coursemanager.course_manager.repository.*;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudentService {
    private final StudentRepository students;
    private final EnrollmentRepository enrollments;

    public StudentService(StudentRepository students, EnrollmentRepository enrollments) {
        this.students = students;
        this.enrollments = enrollments;
    }

    @Transactional(readOnly = true)
    public List<StudentView> list() {
        return students.findAll(Sort.by("name", "id")).stream().map(StudentView::from).toList();
    }

    @Transactional(readOnly = true)
    public StudentView get(Long id) { return StudentView.from(find(id)); }

    public StudentView create(StudentInput input) {
        checkEmail(input.email(), null);
        return StudentView.from(students.save(new Student(input.name(), input.email())));
    }

    public StudentView update(Long id, StudentInput input) {
        Student student = find(id);
        checkEmail(input.email(), id);
        student.update(input.name(), input.email());
        return StudentView.from(student);
    }

    public void delete(Long id) {
        Student student = find(id);
        enrollments.deleteByStudentId(id);
        students.delete(student);
    }

    private Student find(Long id) {
        return students.findById(id).orElseThrow(() -> ApiException.notFound("Student"));
    }

    private void checkEmail(String email, Long id) {
        students.findByEmailIgnoreCase(email).filter(student -> !student.getId().equals(id)).ifPresent(student -> {
            throw ApiException.conflict("A student with that email already exists");
        });
    }
}
