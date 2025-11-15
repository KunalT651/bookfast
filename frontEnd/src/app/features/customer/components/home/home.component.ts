// ...existing code...
import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
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
  showFilters: boolean = false;
  selectedCategory: string = '';
  serviceCategories: any[] = [];
  selectedSpecialization: string = '';
  minPrice: number | null = null;
  maxPrice: number | null = null;
  minRating: number | null = null;
  onlyAvailable: boolean = false;

  constructor(
    private resourceService: ResourceService, 
    private router: Router,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {
    // Listen for booking event from resource card
    window.addEventListener('book', (e: any) => {
      this.onBookResource(e.detail);
    });
  }

  ngOnInit() {
    console.log('[HomeComponent] ngOnInit - Loading resources...');
    console.log('[HomeComponent] Component initialized at:', new Date().toISOString());
    
    // Load service categories
    try {
      console.log('[HomeComponent] About to call loadServiceCategories()');
      this.loadServiceCategories();
    } catch (err: any) {
      console.error('[HomeComponent] Error calling loadServiceCategories:', err);
    }
    
    // Load resources
    try {
      this.resourceService.getResources().subscribe({
        next: (res: Resource[]) => {
          console.log('[HomeComponent] Received resources:', res?.length || 0, res);
          try {
            this.resources = Array.isArray(res) ? res : [];
            this.filteredResources = [...this.resources];
            if (this.resources.length === 0) {
              console.warn('[HomeComponent] No resources received from API');
            } else {
              console.log('[HomeComponent] Successfully loaded', this.resources.length, 'resources');
            }
          } catch (err: any) {
            console.error('[HomeComponent] Error processing resources:', err);
            this.resources = [];
            this.filteredResources = [];
          }
        },
        error: (error: any) => {
          console.error('[HomeComponent] Error loading resources:', error);
          console.error('[HomeComponent] Error details:', {
            status: error?.status,
            statusText: error?.statusText,
            message: error?.message,
            error: error?.error,
            url: error?.url
          });
          this.resources = [];
          this.filteredResources = [];
        }
      });
    } catch (err: any) {
      console.error('[HomeComponent] Exception in ngOnInit:', err);
      this.resources = [];
      this.filteredResources = [];
    }
  }

  loadServiceCategories() {
    console.log('[HomeComponent] ===== loadServiceCategories() called =====');
    console.log('[HomeComponent] Using ResourceService to fetch categories');
    
    this.resourceService.getServiceCategories().subscribe({
      next: (categories) => {
        console.log('[HomeComponent] ===== HTTP Response Received =====');
        console.log('[HomeComponent] Received categories:', categories?.length || 0, categories);
        console.log('[HomeComponent] Categories data structure:', JSON.stringify(categories, null, 2));
        
        // Ensure we're in Angular zone
        this.ngZone.run(() => {
          this.serviceCategories = Array.isArray(categories) ? categories : [];
          
          console.log('[HomeComponent] serviceCategories after assignment:', this.serviceCategories);
          console.log('[HomeComponent] serviceCategories length:', this.serviceCategories.length);
          console.log('[HomeComponent] First category:', this.serviceCategories[0]);
          if (this.serviceCategories.length > 0) {
            console.log('[HomeComponent] First category name:', this.serviceCategories[0]?.name);
            console.log('[HomeComponent] First category id:', this.serviceCategories[0]?.id);
          }
          
          if (this.serviceCategories.length === 0) {
            console.warn('[HomeComponent] No categories received from API. The database may be empty or the endpoint returned an empty array.');
          } else {
            console.log('[HomeComponent] Successfully loaded', this.serviceCategories.length, 'categories:', this.serviceCategories);
            // Force change detection
            this.cdr.detectChanges();
            console.log('[HomeComponent] Change detection triggered');
          }
        });
      },
      error: (error) => {
        console.error('[HomeComponent] ===== HTTP Error =====');
        console.error('[HomeComponent] Error loading categories:', error);
        this.ngZone.run(() => {
          this.serviceCategories = [];
          this.cdr.detectChanges();
        });
      }
    });
  }

  filterResources() {
    try {
      if (!Array.isArray(this.resources)) {
        console.warn('[HomeComponent] filterResources: resources is not an array', this.resources);
        this.filteredResources = [];
        return;
      }

      const term = (this.searchTerm || '').toLowerCase().trim();
      
      if (!term) {
        this.filteredResources = [...this.resources];
        return;
      }

      this.filteredResources = this.resources.filter(resource => {
        if (!resource) return false;
        return (
          resource.name?.toLowerCase().includes(term) ||
          resource.specialization?.toLowerCase().includes(term) ||
          (Array.isArray(resource.tags) && resource.tags.some((tag: string) => tag?.toLowerCase().includes(term)))
        );
      });
    } catch (err: any) {
      console.error('[HomeComponent] Error in filterResources:', err);
      this.filteredResources = this.resources || [];
    }
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

  // Watch for searchTerm changes - use input event handler instead of ngDoCheck for better performance
  onSearchChange() {
    this.applyFilters();
  }

  clearFilters() {
    this.searchTerm = '';
    this.selectedCategory = '';
    this.selectedSpecialization = '';
    this.minPrice = null;
    this.maxPrice = null;
    this.minRating = null;
    this.onlyAvailable = false;
    this.filteredResources = this.resources;
  }

  trackByCategoryId(index: number, cat: any): any {
    return cat?.id || cat?.name || index;
  }

  toggleFilters() {
    this.showFilters = !this.showFilters;
  }

  applyFilters() {
    let filtered = [...this.resources];

    // Search term filter
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(resource => {
        return (
          resource.name?.toLowerCase().includes(term) ||
          resource.specialization?.toLowerCase().includes(term) ||
          (resource.tags && resource.tags.some(tag => tag.toLowerCase().includes(term)))
        );
      });
    }

    // Category filter
    if (this.selectedCategory) {
      console.log('[HomeComponent] Filtering by category:', this.selectedCategory);
      filtered = filtered.filter(resource => {
        // Use serviceCategory from provider
        const resourceCategory = resource.serviceCategory;
        const matches = resourceCategory?.toLowerCase() === this.selectedCategory.toLowerCase();
        if (!matches && resourceCategory) {
          console.log('[HomeComponent] Resource category mismatch:', {
            resourceName: resource.name,
            resourceCategory: resourceCategory,
            selectedCategory: this.selectedCategory
          });
        }
        return matches;
      });
      console.log('[HomeComponent] After category filter:', filtered.length, 'resources');
    }

    // Specialization filter
    if (this.selectedSpecialization) {
      const spec = this.selectedSpecialization.toLowerCase();
      filtered = filtered.filter(resource => 
        resource.specialization?.toLowerCase().includes(spec)
      );
    }

    // Price range filter
    if (this.minPrice !== null && this.minPrice !== undefined) {
      filtered = filtered.filter(resource => 
        resource.price !== null && resource.price !== undefined && resource.price >= this.minPrice!
      );
    }
    if (this.maxPrice !== null && this.maxPrice !== undefined) {
      filtered = filtered.filter(resource => 
        resource.price !== null && resource.price !== undefined && resource.price <= this.maxPrice!
      );
    }

    // Rating filter
    if (this.minRating !== null && this.minRating !== undefined) {
      console.log('[HomeComponent] Filtering by minimum rating:', this.minRating);
      filtered = filtered.filter(resource => {
        // Use averageRating if available, otherwise fall back to rating
        const resourceRating = resource.averageRating ?? resource.rating;
        const matches = resourceRating !== null && resourceRating !== undefined && resourceRating >= this.minRating!;
        if (!matches && resourceRating !== null && resourceRating !== undefined) {
          console.log('[HomeComponent] Resource rating below threshold:', {
            resourceName: resource.name,
            resourceRating: resourceRating,
            minRating: this.minRating
          });
        }
        return matches;
      });
      console.log('[HomeComponent] After rating filter:', filtered.length, 'resources');
    }

    // Availability filter - only show resources with available slots
    if (this.onlyAvailable) {
      console.log('[HomeComponent] Filtering by available slots only');
      filtered = filtered.filter(resource => {
        const hasSlots = resource.hasAvailableSlots === true;
        if (!hasSlots) {
          console.log('[HomeComponent] Resource has no available slots:', {
            resourceName: resource.name,
            hasAvailableSlots: resource.hasAvailableSlots
          });
        }
        return hasSlots;
      });
      console.log('[HomeComponent] After availability filter:', filtered.length, 'resources');
    }

    this.filteredResources = filtered;
  }
}