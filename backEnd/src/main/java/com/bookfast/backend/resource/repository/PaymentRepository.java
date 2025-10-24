package com.bookfast.backend.resource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bookfast.backend.resource.model.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBookingId(Long bookingId);
}
