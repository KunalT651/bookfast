// ...existing code...
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ResourceCardComponent } from '../resource-card/resource-card.component';
import { BookingModalComponent } from '../booking-modal/booking-modal.component';
import { PaymentComponent } from '../payment/payment.component';
import { BookingComponent } from '../booking/booking.component';
import { ResourceService } from '../../services/resource.service';
import { Resource } from '../../models/resource.model';
import { AvailabilitySlot } from '../../models/availability-slot.model';

@Component({
  selector: 'app-customer-home',
  standalone: true,
  imports: [CommonModule, FormsModule, ResourceCardComponent, BookingModalComponent, PaymentComponent, BookingComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  paymentBooking: any = null;
  showPaymentForm: boolean = false;
  paymentSuccessMsg: string = '';

  onPayment(booking: any) {
    this.paymentBooking = booking;
    this.showBookingForm = false;
    this.showPaymentForm = true;
  }

  onPaymentSuccess() {
    this.showPaymentForm = false;
    this.paymentSuccessMsg = 'Payment successful! Your booking is confirmed.';
  }
  resources: Resource[] = [];
  filteredResources: Resource[] = [];
  searchTerm: string = '';
  showModal: boolean = false;
  selectedResource: Resource | null = null;
  
  // Advanced Filters
  showFilters: boolean = false;
  serviceCategories: string[] = ['Healthcare', 'Education', 'Fitness', 'Beauty', 'Tech Support', 'Home Services'];
  selectedCategory: string = '';
  selectedSpecialization: string = '';
  minPrice: number | null = null;
  maxPrice: number | null = null;
  minRating: number | null = null;
  onlyAvailable: boolean = false;

  constructor(private resourceService: ResourceService, private router: Router) {
    // Listen for booking event from resource card
    window.addEventListener('book', (e: any) => {
      this.onBookResource(e.detail);
    });
  }

  ngOnInit() {
    this.loadResources();
  }

  loadResources() {
    this.resourceService.getResources().subscribe((res: Resource[]) => {
      this.resources = res;
      this.applyFilters();
    });
  }

  ngOnChanges() {
    this.applyFilters();
  }

  toggleFilters() {
    this.showFilters = !this.showFilters;
  }

  applyFilters() {
    let filtered = [...this.resources];
    
    // Search term filter
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(resource => 
        resource.name?.toLowerCase().includes(term) ||
        resource.specialization?.toLowerCase().includes(term) ||
        (resource.tags && resource.tags.some(tag => tag.toLowerCase().includes(term)))
      );
    }
    
    // Service category filter
    if (this.selectedCategory) {
      filtered = filtered.filter(r => r.serviceCategory === this.selectedCategory);
    }
    
    // Specialization filter
    if (this.selectedSpecialization) {
      filtered = filtered.filter(r => 
        r.specialization?.toLowerCase().includes(this.selectedSpecialization.toLowerCase())
      );
    }
    
    // Price range filter
    if (this.minPrice !== null) {
      filtered = filtered.filter(r => r.price !== undefined && r.price >= this.minPrice!);
    }
    if (this.maxPrice !== null) {
      filtered = filtered.filter(r => r.price !== undefined && r.price <= this.maxPrice!);
    }
    
    // Rating filter
    if (this.minRating !== null) {
      filtered = filtered.filter(r => r.averageRating !== undefined && r.averageRating >= this.minRating!);
    }
    
    // Availability filter (simple check)
    if (this.onlyAvailable) {
      filtered = filtered.filter(r => r.status === 'active');
    }
    
    this.filteredResources = filtered;
  }

  clearFilters() {
    this.searchTerm = '';
    this.selectedCategory = '';
    this.selectedSpecialization = '';
    this.minPrice = null;
    this.maxPrice = null;
    this.minRating = null;
    this.onlyAvailable = false;
    this.applyFilters();
  }

  onBookResource(resource: Resource) {
    this.selectedResource = resource;
    this.showModal = true;
  }

  onModalClose() {
    this.showModal = false;
    this.selectedResource = null;
  }

  bookingSlots: AvailabilitySlot[] = [];
  showBookingForm: boolean = false;

  onModalConfirm(event: { resource: Resource, slots: AvailabilitySlot[] }) {
    this.selectedResource = event.resource;
    this.bookingSlots = event.slots;
    this.showModal = false;
  // Use Angular Router navigation to preserve session
  const slotIds = event.slots.map(s => s.id).join(',');
  this.router.navigate(['/booking'], { queryParams: { resourceId: event.resource.id, slotIds } });
  this.showBookingForm = false;
  }

  // Watch for changes
  ngDoCheck() {
    // Filters are applied on ngModelChange events
  }
}