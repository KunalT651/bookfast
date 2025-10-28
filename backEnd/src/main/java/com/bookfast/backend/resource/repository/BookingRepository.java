
package com.bookfast.backend.resource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookfast.backend.resource.model.Booking;

import java.util.List;
import java.time.LocalDate;

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

    // Admin statistics methods
    long countByPaymentStatus(String paymentStatus);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.date >= :date")
    long countByDateAfter(@Param("date") java.time.LocalDateTime date);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.date >= :startDate AND b.date < :endDate")
    long countByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COALESCE(SUM(b.finalAmount), 0) FROM Booking b WHERE b.date >= :date")
    Double sumTotalAmountByDateAfter(@Param("date") LocalDate date);
    
    // Find recent bookings for admin dashboard
    @Query("SELECT b FROM Booking b ORDER BY b.date DESC")
    List<Booking> findTop10ByOrderByDateDesc();
}