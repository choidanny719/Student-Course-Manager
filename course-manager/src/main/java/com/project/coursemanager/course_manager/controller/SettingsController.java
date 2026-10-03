package com.project.coursemanager.course_manager.controller;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
public class SettingsController {
    private final Clock clock;

    public SettingsController(Clock clock) { this.clock = clock; }

    @GetMapping("/settings")
    public Map<String, String> settings() {
        return Map.of("today", LocalDate.now(clock).toString(), "timeZone", clock.getZone().toString());
    }
}
