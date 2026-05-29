package com.undoschool.offering.service;

import com.undoschool.exception.ResourceNotFoundException;
import com.undoschool.util.TimezoneUtil;
import com.undoschool.offering.dto.CreateOfferingRequest;
import com.undoschool.offering.dto.OfferingResponse;
import com.undoschool.offering.entity.Course;
import com.undoschool.offering.entity.Offering;
import com.undoschool.offering.repository.CourseRepository;
import com.undoschool.offering.repository.OfferingRepository;
import com.undoschool.session.dto.AddSessionsRequest;
import com.undoschool.session.dto.SessionResponse;
import com.undoschool.session.entity.Session;
import com.undoschool.session.repository.SessionRepository;
import com.undoschool.teacher.entity.Teacher;
import com.undoschool.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferingService {

    private final OfferingRepository offeringRepo;
    private final CourseRepository courseRepo;
    private final TeacherRepository teacherRepo;
    private final SessionRepository sessionRepo;

    @Transactional
    public OfferingResponse createOffering(CreateOfferingRequest req) {
        Course course = courseRepo.findById(req.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + req.getCourseId()));
        Teacher teacher = teacherRepo.findById(req.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + req.getTeacherId()));

        Offering offering = Offering.builder()
                .course(course)
                .teacher(teacher)
                .title(req.getTitle())
                .description(req.getDescription())
                .build();

        offering = offeringRepo.save(offering);
        return toResponse(offering, List.of(), teacher.getTimezone());
    }

    @Transactional
    public List<SessionResponse> addSessions(Long offeringId, AddSessionsRequest req) {
        Offering offering = offeringRepo.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Offering not found: " + offeringId));

        String timezone = req.getTimezone();

        List<Session> sessions = req.getSessions().stream().map(input -> {
            Session s = new Session();
            s.setOffering(offering);
            s.setStartTimeUtc(TimezoneUtil.toUtc(input.getStartTime(), timezone));
            s.setEndTimeUtc(TimezoneUtil.toUtc(input.getEndTime(), timezone));
            s.setSequenceNo(input.getSequenceNo());
            return s;
        }).toList();

        sessions = sessionRepo.saveAll(sessions);
        return sessions.stream()
                .map(s -> toSessionResponse(s, timezone))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OfferingResponse> getTeacherOfferings(Long teacherId) {
        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + teacherId));

        return offeringRepo.findByTeacherId(teacherId).stream()
                .map(o -> {
                    List<Session> sessions = sessionRepo.findByOfferingIdOrderByStartTimeUtcAsc(o.getId());
                    return toResponse(o, sessions, teacher.getTimezone());
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OfferingResponse> getAvailableOfferings(String viewerTimezone) {
        return offeringRepo.findAllActive().stream()
                .map(o -> {
                    List<Session> sessions = sessionRepo.findByOfferingIdOrderByStartTimeUtcAsc(o.getId());
                    return toResponse(o, sessions, viewerTimezone);
                })
                .toList();
    }

    // ── mappers ──────────────────────────────────────────────────────────────

    public OfferingResponse toResponse(Offering o, List<Session> sessions, String timezone) {
        return OfferingResponse.builder()
                .id(o.getId())
                .courseId(o.getCourse().getId())
                .courseTitle(o.getCourse().getTitle())
                .teacherId(o.getTeacher().getId())
                .teacherName(o.getTeacher().getName())
                .title(o.getTitle())
                .description(o.getDescription())
                .status(o.getStatus().name())
                .sessions(sessions.stream()
                        .map(s -> toSessionResponse(s, timezone))
                        .toList())
                .build();
    }

    public SessionResponse toSessionResponse(Session s, String timezone) {
        return SessionResponse.builder()
                .id(s.getId())
                .sequenceNo(s.getSequenceNo())
                .startTime(TimezoneUtil.toLocalString(s.getStartTimeUtc(), timezone))
                .endTime(TimezoneUtil.toLocalString(s.getEndTimeUtc(), timezone))
                .build();
    }
}
