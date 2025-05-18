package com.plan.your.trip.bot.repository;

import com.plan.your.trip.bot.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findAllByUserIdOrderByStartDt(Long userId);
}
