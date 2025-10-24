package com.bookfast.backend.resource.repository;

import com.bookfast.backend.resource.model.Resource;

import com.bookfast.backend.resource.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByResource(Resource resource);

    List<Review> findByResourceOrderByDateDesc(Resource resource);

    List<Review> findByCustomerId(Long customerId);
}
