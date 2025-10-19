import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ResourceCardComponent } from '../resource-card/resource-card.component';
import { BookingModalComponent } from '../booking-modal/booking-modal.component';
import { ResourceService } from '../../services/resource.service';
import { Resource } from '../../models/resource.model';

@Component({
  selector: 'app-customer-home',
  standalone: true,
  imports: [CommonModule, FormsModule, ResourceCardComponent, BookingModalComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  resources: Resource[] = [];
  filteredResources: Resource[] = [];
  searchTerm: string = '';
  showModal: boolean = false;
  selectedResource: Resource | null = null;

  constructor(private resourceService: ResourceService) {
    // Listen for booking event from resource card
    window.addEventListener('book', (e: any) => {
      this.onBookResource(e.detail);
    });
  }

  ngOnInit() {
    this.resourceService.getResources().subscribe((res: Resource[]) => {
      this.resources = res;
      this.filteredResources = res;
    });
  }

  ngOnChanges() {
    this.filterResources();
  }

  filterResources() {
    const term = this.searchTerm.toLowerCase();
    this.filteredResources = this.resources.filter(resource => {
      return (
        resource.name?.toLowerCase().includes(term) ||
        resource.specialization?.toLowerCase().includes(term) ||
        (resource.tags && resource.tags.some(tag => tag.toLowerCase().includes(term)))
      );
    });
  }

  onBookResource(resource: Resource) {
    this.selectedResource = resource;
    this.showModal = true;
  }

  onModalClose() {
    this.showModal = false;
    this.selectedResource = null;
  }

  onModalConfirm(resource: Resource) {
    // TODO: Implement actual booking logic
    console.log('Confirmed booking for:', resource);
    this.showModal = false;
    this.selectedResource = null;
  }

  // Watch for searchTerm changes
  ngDoCheck() {
    this.filterResources();
  }
}