package com.undoschool.parent.controller;

import com.undoschool.booking.dto.BookOfferingRequest;
import com.undoschool.booking.dto.BookingResponse;
import com.undoschool.booking.service.BookingService;
import com.undoschool.offering.dto.OfferingResponse;
import com.undoschool.offering.service.OfferingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parents")
@RequiredArgsConstructor
@Tag(name = "Parent APIs")
public class ParentController {

    private final OfferingService offeringService;
    private final BookingService bookingService;

    @Operation(summary = "View all available offerings")
    @GetMapping("/offerings")
    public ResponseEntity<List<OfferingResponse>> getAvailableOfferings(
            @RequestParam(defaultValue = "UTC") String timezone) {
        return ResponseEntity.ok(offeringService.getAvailableOfferings(timezone));
    }

    @Operation(summary = "Book an offering")
    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> bookOffering(
            @Valid @RequestBody BookOfferingRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.bookOffering(req));
    }

    @Operation(summary = "Get all bookings for a parent")
    @GetMapping("/{parentId}/bookings")
    public ResponseEntity<List<BookingResponse>> getParentBookings(
            @PathVariable Long parentId) {
        return ResponseEntity.ok(bookingService.getParentBookings(parentId));
    }
}
