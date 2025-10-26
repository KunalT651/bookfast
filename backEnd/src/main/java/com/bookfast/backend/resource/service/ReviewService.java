package com.bookfast.backend.resource.service;

import com.bookfast.backend.resource.model.Resource;
import com.bookfast.backend.resource.model.Review;
import com.bookfast.backend.resource.repository.ReviewRepository;
import com.bookfast.backend.resource.repository.ResourceRepository;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class ReviewService {
    public void deleteReview(Resource resource, Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    public void deleteReviewById(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    public Review getReviewById(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    private final ReviewRepository reviewRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, ResourceRepository resourceRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
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

    public List<ReviewDetailsDTO> getDetailedReviewsByProvider(Long providerId) {
        // Get all resources for this provider
        List<Resource> providerResources = resourceRepository.findByProviderId(providerId);
        List<ReviewDetailsDTO> detailedReviews = new ArrayList<>();

        for (Resource resource : providerResources) {
            List<Review> reviews = reviewRepository.findByResourceOrderByDateDesc(resource);
            for (Review review : reviews) {
                ReviewDetailsDTO dto = new ReviewDetailsDTO(review);

                // Get customer details from User table
                if (review.getCustomerId() != null) {
                    Optional<User> customer = userRepository.findById(review.getCustomerId());
                    if (customer.isPresent()) {
                        User customerUser = customer.get();
                        dto.customerEmail = customerUser.getEmail();
                    }
                }

                // Get provider details from User table
                if (resource.getProviderId() != null) {
                    Optional<User> provider = userRepository.findById(resource.getProviderId());
                    if (provider.isPresent()) {
                        User providerUser = provider.get();
                        dto.providerName = providerUser.getFirstName() + " " + providerUser.getLastName();
                        dto.providerEmail = providerUser.getEmail();
                        dto.providerOrganization = providerUser.getOrganizationName();
                        dto.providerServiceCategory = providerUser.getServiceCategory();
                    }
                }

                detailedReviews.add(dto);
            }
        }
        return detailedReviews;
    }

    // DTO class for detailed review information
    public static class ReviewDetailsDTO {
        public Long id;
        public Long customerId;
        public String customerName;
        public String customerEmail;
        public Double rating;
        public String comment;
        public String date;
        public Long resourceId;
        public String resourceName;
        public String resourceDescription;
        public Double resourcePrice;
        public String resourceSpecialization;
        public Long providerId;
        public String providerName;
        public String providerEmail;
        public String providerOrganization;
        public String providerServiceCategory;

        public ReviewDetailsDTO() {}

        public ReviewDetailsDTO(Review review) {
            this.id = review.getId();
            this.customerId = review.getCustomerId();
            this.customerName = review.getCustomerName();
            this.rating = review.getRating();
            this.comment = review.getComment();
            this.date = review.getDate() != null ? review.getDate().toString() : null;
            
            if (review.getResource() != null) {
                this.resourceId = review.getResource().getId();
                this.resourceName = review.getResource().getName();
                this.resourceDescription = review.getResource().getDescription();
                this.resourcePrice = review.getResource().getPrice();
                this.resourceSpecialization = review.getResource().getSpecialization();
                this.providerId = review.getResource().getProviderId();
            }
        }
    }
}
