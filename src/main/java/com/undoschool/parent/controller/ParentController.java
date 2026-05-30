package com.undoschool.parent.controller;

import com.undoschool.booking.dto.BookOfferingRequest;
import com.undoschool.booking.dto.BookingResponse;
import com.undoschool.booking.service.BookingService;
import com.undoschool.exception.GlobalExceptionHandler.ErrorResponse;
import com.undoschool.offering.dto.OfferingResponse;
import com.undoschool.offering.service.OfferingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
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
@RequestMapping("/api/v1/parents")
@RequiredArgsConstructor
@Tag(name = "Parent — Offerings")
public class ParentController {

    private final OfferingService offeringService;
    private final BookingService bookingService;

    @Operation(
        summary = "Browse available offerings",
        description = """
            Returns all active offerings with sessions converted to the viewer's timezone.

            **Timezone Conversion Example:**

            A session stored as `2025-06-07T12:30:00Z` (UTC):
            | Timezone | Displayed As |
            |----------|-------------|
            | `America/New_York` | `2025-06-07T08:30:00-04:00` |
            | `Asia/Singapore`   | `2025-06-07T20:30:00+08:00` |
            | `Asia/Kolkata`     | `2025-06-07T18:00:00+05:30` |

            Pass a valid IANA timezone string. Defaults to `UTC` if not provided.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Offerings returned with timezone-converted session times",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(schema = @Schema(implementation = OfferingResponse.class))))
    })
    @GetMapping("/offerings")
    public ResponseEntity<List<OfferingResponse>> getAvailableOfferings(
            @Parameter(
                description = "IANA timezone string. Session times will be converted to this timezone.",
                example = "America/New_York"
            )
            @RequestParam(defaultValue = "UTC") String timezone) {
        return ResponseEntity.ok(offeringService.getAvailableOfferings(timezone));
    }

    @Operation(
        summary = "Book an offering",
        description = """
            Books an entire offering for a parent. Booking one offering automatically
            books **all sessions** belonging to that offering.

            **Validation Rules:**
            1. **Duplicate check** — Parent cannot book the same offering twice
            2. **Conflict check** — No session overlap with already-booked sessions
            3. **Session check** — Offering must have at least one session

            **Overlap Detection:**
            ```
            overlap = (start1 < end2) AND (end1 > start2)
            ```

            **Concurrency Safety:**
            Uses pessimistic locking (`SELECT FOR UPDATE`) on the parent row.
            Concurrent booking attempts are serialized — no double-booking possible.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Offering booked successfully. Sessions in parent's timezone.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BookingResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Parent or Offering not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Schedule conflict or offering already booked",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Tag(name = "Parent — Bookings")
    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> bookOffering(
            @Valid @RequestBody BookOfferingRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.bookOffering(req));
    }

    @Operation(
        summary = "Get parent's bookings",
        description = """
            Returns all confirmed bookings for a parent.
            Session times are returned in the **parent's registered timezone**.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bookings returned successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(schema = @Schema(implementation = BookingResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Parent not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Tag(name = "Parent — Bookings")
    @GetMapping("/{parentId}/bookings")
    public ResponseEntity<List<BookingResponse>> getParentBookings(
            @Parameter(description = "ID of the parent", required = true)
            @PathVariable Long parentId) {
        return ResponseEntity.ok(bookingService.getParentBookings(parentId));
    }
}
