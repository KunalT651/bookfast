import { Component, OnInit } from '@angular/core';
import { NgIf, NgFor, NgClass } from '@angular/common';
import { DatePipe, CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReviewService } from '../../services/review.service';
import { Review } from '../../models/review.model';
import { ResourceService } from '../../services/resource.service';
import { Resource } from '../../models/resource.model';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-my-reviews',
  templateUrl: './my-reviews.component.html',
  styleUrls: ['./my-reviews.component.css'],
  imports: [DatePipe, CommonModule, FormsModule]
})
export class MyReviewsComponent implements OnInit {
  reviews: (Review & { resourceName?: string })[] = [];
  loading = true;
  showUpdateModal = false;
  editingReview: Review | null = null;
  updateRating: number = 1;
  updateComment: string = '';

  constructor(private reviewService: ReviewService, private resourceService: ResourceService, private authService: AuthService) {}

  ngOnInit() {
    // Get actual customerId from auth service
    this.authService.getCurrentUser().subscribe((user: any) => {
      if (user && user.id) {
        const customerId = user.id;
        this.reviewService.getReviewsByCustomer(customerId).subscribe(async (reviews) => {
          // Fetch resource names for each review
          for (const review of reviews) {
            if (review.resourceId) {
              const resource = await this.resourceService.getResource(review.resourceId).toPromise();
              review.resourceName = resource?.name || '';
            }
          }
          this.reviews = reviews;
          this.loading = false;
        });
      } else {
        this.loading = false;
        console.error('No authenticated user found');
      }
    });
  }

  getCustomerId(): number {
    // This method is kept for backward compatibility but should not be used
    // The actual customer ID is now retrieved in ngOnInit from AuthService
    return 1;
  }

  onUpdate(review: Review) {
    this.editingReview = { ...review };
    this.updateRating = review.rating;
    this.updateComment = review.comment;
    this.showUpdateModal = true;
  }

  closeUpdateModal() {
    this.showUpdateModal = false;
    this.editingReview = null;
  }

  submitUpdate() {
    if (!this.editingReview) return;
    const updatedReview: Review = {
      ...this.editingReview,
      rating: this.updateRating,
      comment: this.updateComment,
      date: new Date().toISOString().split('T')[0]
    };
    this.reviewService.updateReview(this.editingReview.id!, updatedReview).subscribe({
      next: () => {
        // Update local reviews list
        const idx = this.reviews.findIndex(r => r.id === this.editingReview!.id);
        if (idx > -1) {
          this.reviews[idx] = { ...this.reviews[idx], ...updatedReview };
        }
        this.closeUpdateModal();
      },
      error: () => {
        alert('Failed to update review.');
      }
    });
  }

  onDelete(review: Review) {
    if (confirm('Are you sure you want to delete this review?')) {
      this.reviewService.deleteReview(review.resourceId!, review.id!).subscribe({
        next: () => {
          this.reviews = this.reviews.filter(r => r.id !== review.id);
        },
        error: () => {
          alert('Failed to delete review.');
        }
      });
    }
  }
}
