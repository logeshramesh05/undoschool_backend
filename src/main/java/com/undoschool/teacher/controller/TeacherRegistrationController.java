package com.undoschool.teacher.controller;

import com.undoschool.teacher.dto.CreateTeacherRequest;
import com.undoschool.teacher.entity.Teacher;
import com.undoschool.teacher.service.TeacherService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
@Tag(name = "Teacher APIs")
public class TeacherRegistrationController {

    private final TeacherService teacherService;

    @PostMapping
    public ResponseEntity<Teacher> createTeacher(@Valid @RequestBody CreateTeacherRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teacherService.createTeacher(req));
    }
}
