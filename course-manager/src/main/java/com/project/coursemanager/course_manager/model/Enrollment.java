package com.project.coursemanager.course_manager.model;

import jakarta.persistence.*;

@Entity
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // Many enrollment can be done by 1 student
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne // Many enrollment can exist for 1 course
    @JoinColumn(name = "course_id")
    private Course course;

protected Enrollment() {}

public Enrollment(Student student, Course course) {
    this.student = student;
    this.course = course;
}

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }
}