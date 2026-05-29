package com.undoschool.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookOfferingRequest {

    @NotNull(message = "parentId is required")
    private Long parentId;

    @NotNull(message = "offeringId is required")
    private Long offeringId;
}
