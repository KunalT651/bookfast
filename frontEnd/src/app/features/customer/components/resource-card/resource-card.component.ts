// ...existing code...
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Resource } from '../../models/resource.model';
import { Review } from '../../models/review.model';
import { ReviewService } from '../../services/review.service';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-resource-card',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './resource-card.component.html',
  styleUrls: ['./resource-card.component.css']
})
export class ResourceCardComponent {
  // ...existing code...
  @Input() resource!: Resource;
  averageRating: number = 0;
  reviews: Review[] = [];
  showReviewsModal: boolean = false;
  newReview: Review = { customerName: '', rating: 0, comment: '', date: '', customerId: undefined };

  constructor(private reviewService: ReviewService, private authService: AuthService) {}

  ngOnInit() {
    this.loadAverageRating();
  }

  submitReview() {
    if (this.resource && this.resource.id) {
      this.authService.getCurrentUser().subscribe((user: any) => {
        if (user && user.id) {
          const customerId = user.id;
          const reviewToSend: Review = { ...this.newReview, customerId };
          this.reviewService.addReview(this.resource.id, reviewToSend).subscribe((added: Review) => {
            this.reviews.push(added);
            this.loadAverageRating();
            this.newReview = { customerName: '', rating: 0, comment: '', date: '', customerId: undefined };
          });
        } else {
          console.error('No authenticated user found');
        }
      });
    }
  }

  getCustomerId(): number {
    // This method is kept for backward compatibility but should not be used
    // The actual customer ID is now retrieved in submitReview from AuthService
    return 1;
  }

  loadAverageRating() {
    if (this.resource?.id) {
      this.reviewService.getAverageRating(this.resource.id).subscribe(rating => {
        this.averageRating = rating;
      });
    }
  }

  openReviews() {
    if (this.resource?.id) {
      this.reviewService.getReviews(this.resource.id).subscribe(reviews => {
        this.reviews = reviews;
        this.showReviewsModal = true;
      });
    }
  }

  closeReviewsModal() {
    this.showReviewsModal = false;
  }

  onBook() {
    const event = new CustomEvent('book', { detail: this.resource });
    window.dispatchEvent(event);
  }
}