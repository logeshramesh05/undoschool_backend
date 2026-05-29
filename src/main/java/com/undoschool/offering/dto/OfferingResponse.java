package com.undoschool.offering.dto;

import com.undoschool.session.dto.SessionResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data @Builder
public class OfferingResponse {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private Long teacherId;
    private String teacherName;
    private String title;
    private String description;
    private String status;
    private List<SessionResponse> sessions;
}
