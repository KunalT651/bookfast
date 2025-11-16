// ...existing code...
import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Resource } from '../../models/resource.model';
import { Review } from '../../models/review.model';
import { ReviewService } from '../../services/review.service';
import { AuthService } from '../../../auth/services/auth.service';
import { PublicProviderService } from '../../services/public-provider.service';

@Component({
  selector: 'app-resource-card',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './resource-card.component.html',
  styleUrls: ['./resource-card.component.css']
})
export class ResourceCardComponent implements OnChanges {
  // ...existing code...
  @Input() resource!: Resource;
  averageRating: number = 0;
  reviews: Review[] = [];
  showReviewsModal: boolean = false;
  newReview: Review = { customerName: '', rating: 0, comment: '', date: '', customerId: undefined };

  constructor(
    private reviewService: ReviewService,
    private authService: AuthService,
    private publicProviderService: PublicProviderService
  ) {}

  ngOnInit() {
    this.loadAverageRating();
    this.ensureProviderAndCategory();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['resource']?.currentValue) {
      // Re-evaluate rating and log provider/category for diagnostics when input updates
      this.loadAverageRating();
      const prov = this.resource?.providerName ?? (this.resource?.providerId != null ? `#${this.resource.providerId}` : '');
      // Lightweight debug to verify backend enrichment reached UI
      console.debug('[ResourceCard] provider:', prov, 'category:', this.resource?.serviceCategory);
      this.ensureProviderAndCategory();
    }
  }

  private ensureProviderAndCategory() {
    if (!this.resource) return;
    const needsProvider = !this.resource.providerName || this.resource.providerName.trim().length === 0;
    const needsCategory = !this.resource.serviceCategory || this.resource.serviceCategory.trim?.().length === 0;
    if ((needsProvider || needsCategory) && this.resource.providerId != null) {
      this.publicProviderService.getPublicProvider(this.resource.providerId).subscribe(info => {
        if (needsProvider && info.providerName) {
          this.resource.providerName = info.providerName;
        }
        if (needsCategory && info.serviceCategory) {
          this.resource.serviceCategory = info.serviceCategory;
        }
      });
    }
  }

  get providerDisplayName(): string {
    if (!this.resource) return '';
    const n = (this.resource.providerName || '').trim();
    if (n.length > 0) return n;
    return this.resource.providerId != null ? `Provider #${this.resource.providerId}` : '';
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