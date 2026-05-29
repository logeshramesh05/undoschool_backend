package com.undoschool.session.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AddSessionsRequest {

    /** IANA timezone of the teacher when submitting these times */
    @NotBlank(message = "timezone is required")
    private String timezone;

    @NotEmpty(message = "at least one session is required")
    @Valid
    private List<SessionInput> sessions;

    @Data
    public static class SessionInput {
        /** Local datetime string: "2025-06-07T18:00:00" */
        @NotBlank(message = "startTime is required")
        private String startTime;

        @NotBlank(message = "endTime is required")
        private String endTime;

        private Integer sequenceNo;
    }
}
