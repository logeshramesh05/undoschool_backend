package com.undoschool.offering.entity;

import com.undoschool.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "offerings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Offering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OfferingStatus status = OfferingStatus.ACTIVE;

    public enum OfferingStatus {
        DRAFT, ACTIVE, CANCELLED
    }
}
