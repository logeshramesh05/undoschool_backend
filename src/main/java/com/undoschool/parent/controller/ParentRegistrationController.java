package com.undoschool.parent.controller;

import com.undoschool.parent.dto.CreateParentRequest;
import com.undoschool.parent.entity.Parent;
import com.undoschool.parent.service.ParentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/parents")
@RequiredArgsConstructor
@Tag(name = "Parent APIs")
public class ParentRegistrationController {

    private final ParentService parentService;

    @PostMapping
    public ResponseEntity<Parent> createParent(@Valid @RequestBody CreateParentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(parentService.createParent(req));
    }
}
