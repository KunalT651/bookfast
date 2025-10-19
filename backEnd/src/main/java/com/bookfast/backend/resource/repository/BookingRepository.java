package com.bookfast.backend.resource.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookfast.backend.resource.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByResourceId(Long resourceId);
    List<Booking> findByCustomerId(Long customerId);
}