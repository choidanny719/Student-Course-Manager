package com.project.coursemanager.course_manager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "enrollments", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))
public class Enrollment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    protected Enrollment() {}
    public Enrollment(Student student, Course course) { update(student, course); }
    public void update(Student student, Course course) { this.student = student; this.course = course; }
    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
}
