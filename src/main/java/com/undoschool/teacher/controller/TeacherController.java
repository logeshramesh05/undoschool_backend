package com.undoschool.teacher.controller;

import com.undoschool.exception.GlobalExceptionHandler.ErrorResponse;
import com.undoschool.offering.dto.CreateOfferingRequest;
import com.undoschool.offering.dto.OfferingResponse;
import com.undoschool.offering.service.OfferingService;
import com.undoschool.session.dto.AddSessionsRequest;
import com.undoschool.session.dto.SessionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
@Tag(name = "Teacher — Offerings")
public class TeacherController {

    private final OfferingService offeringService;

    @Operation(
        summary = "Create an offering",
        description = """
            Creates a new offering (batch/section) under a course for a teacher.
            An offering starts with `ACTIVE` status and has no sessions initially.
            Sessions must be added separately via `POST /api/v1/teachers/offerings/{offeringId}/sessions`.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Offering created successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = OfferingResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Course or Teacher not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/offerings")
    public ResponseEntity<OfferingResponse> createOffering(
            @Valid @RequestBody CreateOfferingRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(offeringService.createOffering(req));
    }

    @Operation(
        summary = "Add sessions to an offering",
        description = """
            Adds one or more sessions to an existing offering.

            **Timezone Conversion:**
            The teacher submits session times in their local timezone.
            The backend converts all times to UTC before saving.

            Example:
            - Teacher in `Asia/Kolkata` submits `18:00:00`
            - Stored in DB as `12:30:00Z` (UTC)
            - Parents see it in their own local timezone

            **Sequence Numbers:**
            Optional but recommended for ordering (Week 1, Week 2, etc.)
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Sessions added successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(schema = @Schema(implementation = SessionResponse.class)))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Offering not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/offerings/{offeringId}/sessions")
    public ResponseEntity<List<SessionResponse>> addSessions(
            @Parameter(description = "ID of the offering to add sessions to", required = true)
            @PathVariable Long offeringId,
            @Valid @RequestBody AddSessionsRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(offeringService.addSessions(offeringId, req));
    }

    @Operation(
        summary = "Get teacher's offerings",
        description = """
            Returns all offerings created by a teacher with their sessions.
            Session times are returned in the **teacher's own timezone**.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Offerings returned successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(schema = @Schema(implementation = OfferingResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Teacher not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{teacherId}/offerings")
    public ResponseEntity<List<OfferingResponse>> getTeacherOfferings(
            @Parameter(description = "ID of the teacher", required = true)
            @PathVariable Long teacherId) {
        return ResponseEntity.ok(offeringService.getTeacherOfferings(teacherId));
    }
}
