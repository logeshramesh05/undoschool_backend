package com.undoschool.teacher.controller;

import com.undoschool.offering.dto.CreateOfferingRequest;
import com.undoschool.offering.dto.OfferingResponse;
import com.undoschool.offering.service.OfferingService;
import com.undoschool.session.dto.AddSessionsRequest;
import com.undoschool.session.dto.SessionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
@Tag(name = "Teacher APIs")
public class TeacherController {

    private final OfferingService offeringService;

    @Operation(summary = "Create an offering")
    @PostMapping("/offerings")
    public ResponseEntity<OfferingResponse> createOffering(
            @Valid @RequestBody CreateOfferingRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(offeringService.createOffering(req));
    }

    @Operation(summary = "Add sessions to an offering")
    @PostMapping("/offerings/{offeringId}/sessions")
    public ResponseEntity<List<SessionResponse>> addSessions(
            @PathVariable Long offeringId,
            @Valid @RequestBody AddSessionsRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(offeringService.addSessions(offeringId, req));
    }

    @Operation(summary = "Get all offerings for a teacher")
    @GetMapping("/{teacherId}/offerings")
    public ResponseEntity<List<OfferingResponse>> getTeacherOfferings(
            @PathVariable Long teacherId) {
        return ResponseEntity.ok(offeringService.getTeacherOfferings(teacherId));
    }
}
