package com.project.coursemanager.course_manager.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "assignments")
public class Assignment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    @Column(nullable = false, length = 200)
    private String title;
    @Column(nullable = false, length = 5000)
    private String description;
    @Column(nullable = false)
    private LocalDate dueDate;
    @Column(nullable = false)
    private boolean completed;

    protected Assignment() {}
    public Assignment(Course course, String title, String description, LocalDate dueDate, boolean completed) {
        this.course = course;
        update(title, description, dueDate, completed);
    }
    public void update(String title, String description, LocalDate dueDate, boolean completed) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
    }
    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
