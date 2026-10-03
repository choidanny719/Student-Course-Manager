package com.project.coursemanager.course_manager.service;

import static com.project.coursemanager.course_manager.api.ApiModels.*;
import com.project.coursemanager.course_manager.api.ApiException;
import com.project.coursemanager.course_manager.model.*;
import com.project.coursemanager.course_manager.repository.*;
import java.time.LocalDate;
import java.time.Clock;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CourseService {
    private final CourseRepository courses;
    private final AssignmentRepository assignments;
    private final EnrollmentRepository enrollments;
    private final Clock clock;

    public CourseService(CourseRepository courses, AssignmentRepository assignments, EnrollmentRepository enrollments, Clock clock) {
        this.courses = courses;
        this.assignments = assignments;
        this.enrollments = enrollments;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<CourseView> list() {
        return courses.findAll(Sort.by("courseCode", "id")).stream().map(CourseView::from).toList();
    }

    @Transactional(readOnly = true)
    public CourseView get(Long id) { return CourseView.from(findCourse(id)); }

    public CourseView create(CourseInput input) {
        checkCode(input.courseCode(), null);
        return CourseView.from(courses.save(new Course(input.courseCode(), input.name(), input.description())));
    }

    public CourseView update(Long id, CourseInput input) {
        Course course = findCourse(id);
        checkCode(input.courseCode(), id);
        course.update(input.courseCode(), input.name(), input.description());
        return CourseView.from(course);
    }

    public void delete(Long id) {
        Course course = findCourse(id);
        assignments.deleteByCourseId(id);
        enrollments.deleteByCourseId(id);
        courses.delete(course);
    }

    @Transactional(readOnly = true)
    public List<AssignmentView> listAssignments(Long courseId) {
        findCourse(courseId);
        return assignments.findByCourseIdOrderByDueDateAscIdAsc(courseId).stream().map(AssignmentView::from).toList();
    }

    @Transactional(readOnly = true)
    public AssignmentView getAssignment(Long id) { return AssignmentView.from(findAssignment(id)); }

    public AssignmentView createAssignment(Long courseId, AssignmentInput input) {
        var assignment = new Assignment(findCourse(courseId), input.title(), input.description(), input.dueDate(), input.completed());
        return AssignmentView.from(assignments.save(assignment));
    }

    public AssignmentView updateAssignment(Long id, AssignmentInput input) {
        Assignment assignment = findAssignment(id);
        assignment.update(input.title(), input.description(), input.dueDate(), input.completed());
        return AssignmentView.from(assignment);
    }

    public void deleteAssignment(Long id) { assignments.delete(findAssignment(id)); }

    public AssignmentView completeAssignment(Long id) {
        return setCompleted(id, true);
    }

    public AssignmentView setCompleted(Long id, boolean completed) {
        Assignment assignment = findAssignment(id);
        assignment.setCompleted(completed);
        return AssignmentView.from(assignment);
    }

    @Transactional(readOnly = true)
    public List<AssignmentView> overdue() {
        return filterAssignments(null, "overdue", null, null, null);
    }

    @Transactional(readOnly = true)
    public List<AssignmentView> upcoming(int days) {
        return filterAssignments(null, "upcoming", null, null, days);
    }

    @Transactional(readOnly = true)
    public List<AssignmentView> filterAssignments(Long courseId, String status, LocalDate from, LocalDate to, Integer days) {
        if (courseId != null) findCourse(courseId);
        if (from != null && to != null && from.isAfter(to)) {
            throw ApiException.invalid("Start date must be on or before end date");
        }
        if (days != null && (days < 1 || days > 3650)) {
            throw ApiException.invalid("Days must be between 1 and 3650");
        }
        if (days != null && (!status.equals("upcoming") || from != null || to != null)) {
            throw ApiException.invalid("Use days with upcoming, or use a custom date range");
        }
        LocalDate today = LocalDate.now(clock);
        Boolean completed = null;
        switch (status) {
            case "all" -> {}
            case "pending" -> completed = false;
            case "completed" -> completed = true;
            case "overdue" -> {
                completed = false;
                LocalDate yesterday = today.minusDays(1);
                to = to == null || to.isAfter(yesterday) ? yesterday : to;
            }
            case "upcoming" -> {
                completed = false;
                if (days != null || (from == null && to == null)) to = today.plusDays(days == null ? 7 : days);
                from = from == null || from.isBefore(today) ? today : from;
            }
            default -> throw ApiException.invalid("Choose all, pending, upcoming, overdue or completed");
        }
        return assignments.filter(courseId, from, to, completed).stream().map(AssignmentView::from).toList();
    }

    private Course findCourse(Long id) {
        return courses.findById(id).orElseThrow(() -> ApiException.notFound("Course"));
    }

    private Assignment findAssignment(Long id) {
        return assignments.findById(id).orElseThrow(() -> ApiException.notFound("Assignment"));
    }

    private void checkCode(String code, Long id) {
        courses.findByCourseCodeIgnoreCase(code).filter(course -> !course.getId().equals(id)).ifPresent(course -> {
            throw ApiException.conflict("A course with that code already exists");
        });
    }
}
