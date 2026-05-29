package com.undoschool.offering.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOfferingRequest {

    @NotNull(message = "courseId is required")
    private Long courseId;

    @NotNull(message = "teacherId is required")
    private Long teacherId;

    @NotBlank(message = "title is required")
    private String title;

    private String description;
}
