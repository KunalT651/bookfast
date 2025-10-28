package com.bookfast.backend.resource.repository;

import com.bookfast.backend.resource.model.Resource;
import com.bookfast.backend.resource.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.time.LocalDate;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByResource(Resource resource);

    List<Review> findByResourceOrderByDateDesc(Resource resource);

    List<Review> findByCustomerId(Long customerId);

    // Admin statistics methods
    @Query("SELECT COUNT(r) FROM Review r WHERE r.date >= :date")
    long countByDateAfter(@Param("date") LocalDate date);
    
    @Query("SELECT AVG(r.rating) FROM Review r")
    Double getAverageRating();
    
    // Find recent reviews for admin dashboard
    @Query("SELECT r FROM Review r ORDER BY r.date DESC")
    List<Review> findTop10ByOrderByDateDesc();
}
