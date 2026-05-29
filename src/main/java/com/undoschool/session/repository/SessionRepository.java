package com.undoschool.session.repository;

import com.undoschool.session.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.Instant;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByOfferingIdOrderByStartTimeUtcAsc(Long offeringId);

    /**
     * Find all sessions belonging to confirmed bookings of a parent
     * that overlap with the given time window.
     *
     * Used during conflict detection in the booking flow.
     */
    @Query("""
        SELECT s FROM Session s
        JOIN Booking b ON b.offering.id = s.offering.id
        WHERE b.parent.id = :parentId
          AND b.status = 'CONFIRMED'
          AND s.startTimeUtc < :endTime
          AND s.endTimeUtc   > :startTime
        """)
    List<Session> findConflictingSessions(
            @Param("parentId") Long parentId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime
    );
}
