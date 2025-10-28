package com.bookfast.backend.provider.repository;

import com.bookfast.backend.provider.model.UnavailableDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UnavailableDateRepository extends JpaRepository<UnavailableDate, Long> {
    List<UnavailableDate> findByProviderId(Long providerId);
    
    @Query("SELECT u FROM UnavailableDate u WHERE u.providerId = :providerId AND " +
           "((u.startDate <= :endDate AND u.endDate >= :startDate))")
    List<UnavailableDate> findConflictingDates(@Param("providerId") Long providerId, 
                                               @Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);
    
    @Query("SELECT u FROM UnavailableDate u WHERE u.providerId = :providerId AND " +
           "u.startDate <= :date AND u.endDate >= :date")
    List<UnavailableDate> findByProviderIdAndDate(@Param("providerId") Long providerId, 
                                                  @Param("date") LocalDate date);
}
