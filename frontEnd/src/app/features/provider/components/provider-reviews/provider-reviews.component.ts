import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder } from '@angular/forms';
import { ResourceService } from '../../services/resource.service';
import { ReviewService } from '../../services/review.service';

@Component({
  selector: 'app-provider-reviews',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './provider-reviews.component.html',
  styleUrls: ['./provider-reviews.component.css'],
  providers: [ReviewService]
})
export class ProviderReviewsComponent {
  searchTerm = '';
  resources: any[] = [];
  reviews: any[] = [];
  selectedResource: any = null;

  constructor(private resourceService: ResourceService, private reviewService: ReviewService, private fb: FormBuilder) {
  this.resourceService.getResourcesForCurrentProvider().subscribe((res: any[]) => this.resources = res);
  }

  searchResources() {
    const term = this.searchTerm.toLowerCase();
    return this.resources.filter(r => r.name.toLowerCase().includes(term));
  }

  selectResource(resource: any) {
    this.selectedResource = resource;
    this.reviewService.getReviewsByResourceId(resource.id).subscribe(revs => this.reviews = revs);
  }
}
