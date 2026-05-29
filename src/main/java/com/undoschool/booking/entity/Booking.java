package com.undoschool.booking.entity;

import com.undoschool.offering.entity.Offering;
import com.undoschool.parent.entity.Parent;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_parent", columnList = "parent_id"),
    @Index(name = "idx_booking_offering", columnList = "offering_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id", nullable = false)
    private Offering offering;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.CONFIRMED;

    @CreationTimestamp
    @Column(name = "booked_at", updatable = false,
            columnDefinition = "DATETIME(6)")
    private Instant bookedAt;

    public enum BookingStatus {
        CONFIRMED, CANCELLED
    }
}
