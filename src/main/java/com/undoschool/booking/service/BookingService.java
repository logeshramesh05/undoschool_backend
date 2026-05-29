package com.undoschool.booking.service;

import com.undoschool.booking.dto.BookOfferingRequest;
import com.undoschool.booking.dto.BookingResponse;
import com.undoschool.booking.entity.Booking;
import com.undoschool.booking.repository.BookingRepository;
import com.undoschool.exception.BookingConflictException;
import com.undoschool.exception.ResourceNotFoundException;
import com.undoschool.util.TimezoneUtil;
import com.undoschool.offering.entity.Offering;
import com.undoschool.offering.repository.OfferingRepository;
import com.undoschool.offering.service.OfferingService;
import com.undoschool.parent.entity.Parent;
import com.undoschool.parent.repository.ParentRepository;
import com.undoschool.session.entity.Session;
import com.undoschool.session.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepo;
    private final ParentRepository parentRepo;
    private final OfferingRepository offeringRepo;
    private final SessionRepository sessionRepo;
    private final OfferingService offeringService;

    /**
     * Books an entire offering for a parent.
     *
     * Concurrency strategy:
     *  1. Lock the parent row with PESSIMISTIC_WRITE (SELECT FOR UPDATE).
     *     This serializes concurrent booking attempts by the same parent.
     *  2. Check each session of the new offering against the parent's
     *     already-booked sessions for overlap.
     *  3. Save atomically inside a single @Transactional boundary.
     */
    @Transactional
    public BookingResponse bookOffering(BookOfferingRequest req) {

        // 1. Lock parent row — prevents race conditions for same parent
        Parent parent = parentRepo.findByIdForUpdate(req.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found: " + req.getParentId()));

        Offering offering = offeringRepo.findById(req.getOfferingId())
                .orElseThrow(() -> new ResourceNotFoundException("Offering not found: " + req.getOfferingId()));

        // 2. Prevent duplicate booking of the same offering
        if (bookingRepo.existsByParentIdAndOfferingIdAndStatus(
                parent.getId(), offering.getId(), Booking.BookingStatus.CONFIRMED)) {
            throw new BookingConflictException("You have already booked this offering.");
        }

        // 3. Get all sessions of the target offering
        List<Session> newSessions = sessionRepo.findByOfferingIdOrderByStartTimeUtcAsc(offering.getId());
        if (newSessions.isEmpty()) {
            throw new IllegalStateException("Offering has no sessions yet.");
        }

        // 4. Check overlap with parent's existing confirmed bookings
        //    For each new session, query if any already-booked session overlaps.
        //    Overlap: start1 < end2 AND end1 > start2 (handled in JPQL query)
        for (Session session : newSessions) {
            List<Session> conflicts = sessionRepo.findConflictingSessions(
                    parent.getId(),
                    session.getStartTimeUtc(),
                    session.getEndTimeUtc()
            );
            if (!conflicts.isEmpty()) {
                Session conflict = conflicts.get(0);
                throw new BookingConflictException(String.format(
                        "Schedule conflict: session on %s overlaps with your existing booking on %s",
                        TimezoneUtil.toLocalString(session.getStartTimeUtc(), parent.getTimezone()),
                        TimezoneUtil.toLocalString(conflict.getStartTimeUtc(), parent.getTimezone())
                ));
            }
        }

        // 5. All checks passed — save booking
        Booking booking = Booking.builder()
                .parent(parent)
                .offering(offering)
                .build();
        booking = bookingRepo.save(booking);

        return toResponse(booking, newSessions, parent.getTimezone());
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getParentBookings(Long parentId) {
        Parent parent = parentRepo.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found: " + parentId));

        return bookingRepo.findConfirmedByParentId(parentId).stream()
                .map(booking -> {
                    List<Session> sessions = sessionRepo
                            .findByOfferingIdOrderByStartTimeUtcAsc(booking.getOffering().getId());
                    return toResponse(booking, sessions, parent.getTimezone());
                })
                .toList();
    }

    // ── mapper ────────────────────────────────────────────────────────────────

    private BookingResponse toResponse(Booking booking, List<Session> sessions, String timezone) {
        return BookingResponse.builder()
                .id(booking.getId())
                .parentId(booking.getParent().getId())
                .offeringId(booking.getOffering().getId())
                .offeringTitle(booking.getOffering().getTitle())
                .status(booking.getStatus().name())
                .bookedAt(booking.getBookedAt() != null
                        ? TimezoneUtil.toLocalString(booking.getBookedAt(), timezone) : null)
                .sessions(sessions.stream()
                        .map(s -> offeringService.toSessionResponse(s, timezone))
                        .toList())
                .build();
    }
}
