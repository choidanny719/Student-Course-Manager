package com.project.coursemanager.course_manager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 24)
    private String courseCode;
    @Column(nullable = false, length = 120)
    private String name;
    @Column(nullable = false, length = 2000)
    private String description;

    protected Course() {}
    public Course(String courseCode, String name, String description) { update(courseCode, name, description); }
    public void update(String courseCode, String name, String description) {
        this.courseCode = courseCode;
        this.name = name;
        this.description = description;
    }
    public Long getId() { return id; }
    public String getCourseCode() { return courseCode; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}
