package com.project.coursemanager.course_manager;

import static com.project.coursemanager.course_manager.api.ApiModels.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DeadlineApiTest extends ApiTestSupport {
    private final LocalDate today = LocalDate.of(2026, 10, 2);

    private String[] titles(String query) {
        return Arrays.stream(client.get().uri("/assignments" + query).retrieve().body(AssignmentView[].class))
                .map(AssignmentView::title).toArray(String[]::new);
    }

    @Test
    void upcomingIncludesTodayAndTheLastDayButExcludesCompletedWork() {
        var course = course("CMPT307");
        assignment(course.id(), "Yesterday", today.minusDays(1), false);
        assignment(course.id(), "Today", today, false);
        assignment(course.id(), "Last day", today.plusDays(7), false);
        assignment(course.id(), "Later", today.plusDays(8), false);
        assignment(course.id(), "Finished", today.plusDays(2), true);
        assertArrayEquals(new String[]{"Today", "Last day"}, titles("?status=upcoming"));
        assertArrayEquals(new String[]{"Today"}, titles("?status=upcoming&days=3"));
        assertArrayEquals(new String[]{"Today", "Last day"}, titles("/upcoming?days=7"));
        assertArrayEquals(new String[]{"Yesterday"}, titles("?status=overdue"));
    }

    @Test
    void eachPresetUsesAnInclusiveUpperBoundary() {
        var course = course("CMPT307");
        for (int days : new int[]{3, 7, 14, 30}) {
            assignments.deleteAllInBatch();
            assignment(course.id(), "Boundary", today.plusDays(days), false);
            assignment(course.id(), "Outside", today.plusDays(days + 1), false);
            assertArrayEquals(new String[]{"Boundary"}, titles("?status=upcoming&days=" + days));
        }
    }

    @Test
    void customRangesAndCourseFiltersAreAppliedTogether() {
        var first = course("CMPT307");
        var second = course("CMPT310");
        assignment(first.id(), "Before", today.minusDays(1), false);
        assignment(first.id(), "Start", today, false);
        assignment(first.id(), "End", today.plusDays(4), true);
        assignment(first.id(), "After", today.plusDays(5), false);
        assignment(second.id(), "Other course", today, false);
        String range = "?courseId=" + first.id() + "&from=2026-10-02&to=2026-10-06";
        assertArrayEquals(new String[]{"Start", "End"}, titles(range));
        assertArrayEquals(new String[]{"Start"}, titles(range + "&status=pending"));
        assertArrayEquals(new String[]{"End"}, titles(range + "&status=completed"));
        assertEquals(0, titles(range + "&status=overdue").length);
    }

    @Test
    void completionCanBeCheckedAndUncheckedWithoutChangingAssignmentDetails() {
        var course = course("CMPT307");
        var assignment = assignment(course.id(), "Homework", today.minusDays(1), false);
        String path = "/assignments/" + assignment.id() + "/completion";
        var completed = client.patch().uri(path).body(new CompletionInput(true)).retrieve().body(AssignmentView.class);
        assertTrue(completed.completed());
        assertEquals("Homework", completed.title());
        assertEquals(0, titles("/overdue").length);
        client.patch().uri(path).body(new CompletionInput(false)).retrieve().toBodilessEntity();
        assertArrayEquals(new String[]{"Homework"}, titles("/overdue"));
        assertFalse(client.get().uri("/assignments/" + assignment.id()).retrieve().body(AssignmentView.class).completed());
        assertStatus(400, () -> client.patch().uri(path).body(Map.of()).retrieve().toBodilessEntity());
    }

    @Test
    void invalidFiltersReturnBadRequest() {
        for (String query : new String[]{"?from=2026-10-10&to=2026-10-01", "?from=not-a-date",
                "?status=unknown", "?status=upcoming&days=0", "?status=upcoming&days=3651",
                "?status=upcoming&days=7&from=2026-10-02", "?status=all&days=7"}) {
            assertStatus(400, () -> titles(query));
        }
    }

    @Test
    void dateBoundaryUsesTheConfiguredTimezone() {
        var settings = client.get().uri("/settings").retrieve().body(Map.class);
        assertEquals("2026-10-02", settings.get("today"));
        assertEquals("America/Vancouver", settings.get("timeZone"));
    }
}
