package com.undoschool.booking.repository;

import com.undoschool.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.parent.id = :parentId AND b.status = 'CONFIRMED'")
    List<Booking> findConfirmedByParentId(@Param("parentId") Long parentId);

    boolean existsByParentIdAndOfferingIdAndStatus(
            Long parentId, Long offeringId, Booking.BookingStatus status);
}
