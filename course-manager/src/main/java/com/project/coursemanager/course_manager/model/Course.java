package com.project.coursemanager.course_manager.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String courseCode;
    private String name;
    private String description;

    // 'course' in Assignment class is what connects these
    @OneToMany(mappedBy = "course") // One course can have many assignments
    private List<Assignment> assignments = new ArrayList<>();

    protected Course() {}

    public Course(String courseCode, String name, String description) {
        this.courseCode = courseCode;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }
}