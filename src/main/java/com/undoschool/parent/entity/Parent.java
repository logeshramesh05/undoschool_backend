package com.undoschool.parent.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    /** IANA timezone string e.g. "America/New_York" */
    @Column(nullable = false)
    private String timezone;
}
