package com.project.coursemanager.course_manager.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Student number

    private String name; // Name
    private String email; // school email

    // 'student' in Enrollment class is what connects these
    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrollments = new ArrayList<>();

    protected Student() {}

    public Student(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }
}
