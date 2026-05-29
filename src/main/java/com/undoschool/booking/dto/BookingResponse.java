package com.undoschool.booking.dto;

import com.undoschool.session.dto.SessionResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data @Builder
public class BookingResponse {
    private Long id;
    private Long parentId;
    private Long offeringId;
    private String offeringTitle;
    private String status;
    private String bookedAt;
    private List<SessionResponse> sessions;
}
