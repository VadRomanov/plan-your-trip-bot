package com.plan.your.trip.bot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "trip")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Trip {
    @Id
    @SequenceGenerator(name = "trip_gen", sequenceName = "trip_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trip_gen")
    private Long id;
    @Column
    private Long userId;
    @Column
    private String name;
    @Column
    private LocalDate startDt;
    @Column
    private LocalDate endDt;
    @Column
    private Boolean expired;
    @Column
    private OffsetDateTime createdAt;
}
