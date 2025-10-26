
package com.bookfast.backend.resource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookfast.backend.resource.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByResource_Id(Long resourceId);

    List<Booking> findByCustomerId(Long customerId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.resource r WHERE r.providerId = :providerId")
    List<Booking> findByResourceProviderId(@Param("providerId") Long providerId);

    // Aggregate total revenue (CAD)
    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(b.finalAmount), 0) FROM Booking b WHERE b.status = 'COMPLETED'")
    Double sumTotalAmount();

    // Count bookings by status
    long countByStatus(String status);

    // Find overlapping bookings for a resource
    @org.springframework.data.jpa.repository.Query("SELECT b FROM Booking b WHERE b.resource.id = :resourceId AND b.status <> 'cancelled' AND ((b.startTime < :endTime AND b.endTime > :startTime))")
    List<Booking> findOverlappingBookings(
            @org.springframework.data.repository.query.Param("resourceId") Long resourceId,
            @org.springframework.data.repository.query.Param("startTime") java.time.LocalDateTime startTime,
            @org.springframework.data.repository.query.Param("endTime") java.time.LocalDateTime endTime);
}