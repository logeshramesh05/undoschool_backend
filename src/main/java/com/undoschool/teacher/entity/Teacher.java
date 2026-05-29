package com.undoschool.teacher.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teachers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    /** IANA timezone string e.g. "Asia/Kolkata" */
    @Column(nullable = false)
    private String timezone;
}
