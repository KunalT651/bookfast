package com.bookfast.backend.resource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.bookfast.backend.resource.model.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p WHERE p.booking.id = :bookingId")
    List<Payment> findByBookingId(@Param("bookingId") Long bookingId);
}
