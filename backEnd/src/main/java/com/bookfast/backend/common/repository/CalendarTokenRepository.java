package com.bookfast.backend.common.repository;

import com.bookfast.backend.common.model.CalendarToken;
import com.bookfast.backend.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarTokenRepository extends JpaRepository<CalendarToken, Long> {
    
    Optional<CalendarToken> findByUserAndIsActiveTrue(User user);
    
    Optional<CalendarToken> findByUserAndIsActiveTrueAndCalendarId(User user, String calendarId);
    
    List<CalendarToken> findByUser(User user);
    
    @Query("SELECT ct FROM CalendarToken ct WHERE ct.user = :user AND ct.isActive = true AND ct.expiresAt > CURRENT_TIMESTAMP")
    Optional<CalendarToken> findValidTokenByUser(@Param("user") User user);
    
    @Query("SELECT ct FROM CalendarToken ct WHERE ct.isActive = true AND ct.expiresAt < CURRENT_TIMESTAMP")
    List<CalendarToken> findExpiredTokens();
    
    void deleteByUser(User user);
    
    @Query("SELECT COUNT(ct) FROM CalendarToken ct WHERE ct.user = :user AND ct.isActive = true")
    long countActiveTokensByUser(@Param("user") User user);
}
