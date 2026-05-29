package com.undoschool.offering.controller;

import com.undoschool.offering.entity.Course;
import com.undoschool.offering.repository.CourseRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Course APIs")
public class CourseController {

    private final CourseRepository courseRepo;

    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CourseRequest req) {
        Course course = Course.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseRepo.save(course));
    }

    @GetMapping
    public ResponseEntity<List<Course>> getCourses() {
        return ResponseEntity.ok(courseRepo.findAll());
    }

    @Data
    static class CourseRequest {
        @NotBlank
        private String title;
        private String description;
    }
}
