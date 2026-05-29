package com.undoschool.session.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class SessionResponse {
    private Long id;
    private Integer sequenceNo;
    /** ISO offset string in the viewer's local timezone */
    private String startTime;
    private String endTime;
}
