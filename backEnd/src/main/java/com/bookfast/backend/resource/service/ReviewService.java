package com.bookfast.backend.resource.service;

import com.bookfast.backend.resource.model.Resource;

import com.bookfast.backend.resource.model.Review;
import com.bookfast.backend.resource.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    public void deleteReview(Resource resource, Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    public Review getReviewById(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getReviewsForResource(Resource resource) {
        return reviewRepository.findByResourceOrderByDateDesc(resource);
    }

    public double getAverageRating(Resource resource) {
        List<Review> reviews = reviewRepository.findByResource(resource);
        if (reviews.isEmpty())
            return 0.0;
        return reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);
    }

    public Review addReview(Resource resource, String customerName, Long customerId, double rating, String comment) {
        Review review = new Review();
        review.setResource(resource);
        review.setCustomerName(customerName);
        review.setCustomerId(customerId);
        review.setRating(rating);
        review.setComment(comment);
        review.setDate(java.time.LocalDate.now());
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByCustomer(Long customerId) {
        // Assuming customerName is unique and customerId maps to customerName
        // If you have a Customer entity, you should fetch by customerId
        // For now, this is a placeholder and should be replaced with actual logic
        return reviewRepository.findByCustomerId(customerId);
    }
}
