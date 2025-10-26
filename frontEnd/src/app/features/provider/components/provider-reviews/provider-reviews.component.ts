import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReviewService } from '../../services/review.service';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-provider-reviews',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './provider-reviews.component.html',
  styleUrls: ['./provider-reviews.component.css']
})
export class ProviderReviewsComponent implements OnInit {
  reviews: any[] = [];
  providerId: number | null = null;
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private reviewService: ReviewService, private authService: AuthService) {}

  ngOnInit() {
    console.log('[ProviderReviews] Component initialized, loading reviews...');
    this.loadReviews();
  }

  loadReviews() {
    console.log('[ProviderReviews] Starting to load reviews...');
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    this.authService.getCurrentUser().subscribe({
      next: (user: any) => {
        console.log('[ProviderReviews] Current user:', user);
        if (user && user.id) {
          this.providerId = user.id;
          console.log('[ProviderReviews] Provider ID:', this.providerId);
          console.log('[ProviderReviews] Making API call to get reviews...');
          
          this.reviewService.getReviewsByProvider().subscribe({
            next: (data) => {
              console.log('[ProviderReviews] Reviews received:', data);
              this.reviews = data;
              this.loading = false;
            },
            error: (error) => {
              this.errorMessage = 'Failed to load reviews. Please try again.';
              this.loading = false;
              console.error('[ProviderReviews] Error loading reviews:', error);
            }
          });
        } else {
          this.errorMessage = 'No authenticated provider found';
          this.loading = false;
          console.error('[ProviderReviews] No authenticated provider found');
        }
      },
      error: (error) => {
        this.errorMessage = 'Failed to get current user';
        this.loading = false;
        console.error('[ProviderReviews] Error getting current user:', error);
      }
    });
  }

  deleteReview(reviewId: number) {
    if (!this.providerId) return;
    if (confirm('Are you sure you want to delete this review? This action cannot be undone.')) {
      console.log('[ProviderReviews] Deleting review:', reviewId);
      this.reviewService.deleteReviewByProvider(reviewId).subscribe({
        next: () => {
          this.reviews = this.reviews.filter(r => r.id !== reviewId);
          console.log('[ProviderReviews] Review deleted successfully');
          this.errorMessage = '';
          this.successMessage = 'Review deleted successfully!';
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = 'Failed to delete review. Please try again.';
          console.error('[ProviderReviews] Error deleting review:', error);
        }
      });
    }
  }

  getStars(rating: number): string[] {
    const stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(i <= rating ? '★' : '☆');
    }
    return stars;
  }
}