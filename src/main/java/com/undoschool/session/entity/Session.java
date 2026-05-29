package com.undoschool.session.entity;

import com.undoschool.offering.entity.Offering;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "sessions", indexes = {
    @Index(name = "idx_session_offering", columnList = "offering_id"),
    @Index(name = "idx_session_time", columnList = "start_time_utc, end_time_utc")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id", nullable = false)
    private Offering offering;

    /** Always stored as UTC. Use Instant — no timezone ambiguity. */
    @Column(name = "start_time_utc", nullable = false,
            columnDefinition = "DATETIME(6)")
    private Instant startTimeUtc;

    @Column(name = "end_time_utc", nullable = false,
            columnDefinition = "DATETIME(6)")
    private Instant endTimeUtc;

    @Column(name = "sequence_no")
    private Integer sequenceNo;
}
