
package com.bookfast.backend.resource.controller;

import com.bookfast.backend.resource.model.Resource;
import com.bookfast.backend.resource.model.Review;
import com.bookfast.backend.resource.repository.ResourceRepository;
import com.bookfast.backend.resource.service.ReviewService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:4200")
public class ReviewController {
    @DeleteMapping("/resource/{resourceId}/{reviewId}")
    public void deleteReview(@PathVariable Long resourceId, @PathVariable Long reviewId) {
        Resource resource = resourceRepository.findById(resourceId).orElse(null);
        if (resource != null) {
            reviewService.deleteReview(resource, reviewId);
        }
    }

    private final ReviewService reviewService;
    private final ResourceRepository resourceRepository;

    public ReviewController(ReviewService reviewService, ResourceRepository resourceRepository) {
        this.reviewService = reviewService;
        this.resourceRepository = resourceRepository;
    }

    @GetMapping("/customer/{customerId}")
    public List<ReviewDTO> getReviewsByCustomer(@PathVariable Long customerId) {
        List<Review> reviews = reviewService.getReviewsByCustomer(customerId);
        return reviews.stream().map(review -> new ReviewDTO(review)).toList();
    }

    static class ReviewDTO {
        public Long id;
        public String customerName;
        public Double rating;
        public String comment;
        public String date;
        public Long resourceId;
        public String resourceName;

        public ReviewDTO(Review review) {
            this.id = review.getId();
            this.customerName = review.getCustomerName();
            this.rating = review.getRating();
            this.comment = review.getComment();
            this.date = review.getDate() != null ? review.getDate().toString() : null;
            this.resourceId = review.getResource() != null ? review.getResource().getId() : null;
            this.resourceName = (review.getResource() != null && review.getResource().getName() != null)
                    ? review.getResource().getName()
                    : "";
        }
    }

    @GetMapping("/resource/{resourceId}")
    public List<Review> getReviews(@PathVariable Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId).orElse(null);
        if (resource == null)
            return List.of();
        return reviewService.getReviewsForResource(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody ReviewDTO reviewDTO) {
        Review review = reviewService.getReviewById(id);
        if (review == null) {
            return ResponseEntity.notFound().build();
        }
        // Update review fields
        review.setRating(reviewDTO.rating);
        review.setComment(reviewDTO.comment);
        review.setDate(reviewDTO.date != null ? LocalDate.parse(reviewDTO.date) : null);
        reviewService.saveReview(review);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/resource/{resourceId}/rating", produces = "application/json")
    public Double getAverageRating(@PathVariable Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId).orElse(null);
        Double rating = 0.0;
        if (resource != null) {
            rating = reviewService.getAverageRating(resource);
        }
        return rating;
    }

    @PostMapping("/resource/{resourceId}")
    public Review addReview(@PathVariable Long resourceId, @RequestBody Review review) {
        Resource resource = resourceRepository.findById(resourceId).orElse(null);
        if (resource == null)
            return null;
        return reviewService.addReview(resource, review.getCustomerName(), review.getCustomerId(), review.getRating(),
                review.getComment());
    }
}
