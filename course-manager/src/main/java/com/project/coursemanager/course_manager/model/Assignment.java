package com.project.coursemanager.course_manager.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private String title;
    private String description;
    private LocalDate dueDate;
    private boolean completed;

    protected Assignment() {
    }

    public Assignment(Course course, String title, String description, LocalDate dueDate) {
        this.course = course;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = false;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}