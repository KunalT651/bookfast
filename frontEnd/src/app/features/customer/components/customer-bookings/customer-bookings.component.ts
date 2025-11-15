import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { BookingService } from '../../services/booking.service';
import { AuthService } from '../../../auth/services/auth.service';
import { Booking } from '../../models/booking.model';

@Component({
  selector: 'app-customer-bookings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './customer-bookings.component.html',
  styleUrls: ['./customer-bookings.component.css']
})
export class CustomerBookingsComponent implements OnInit, OnDestroy {
  bookings: Booking[] = [];
  userId: number | null = null;
  loading = false;
  errorMessage = '';
  private routerSubscription?: Subscription;

  constructor(
    private bookingService: BookingService, 
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    console.log('[CustomerBookings] Component initialized');
    this.loadBookings();
    
    // Subscribe to router events to refresh when navigating to this route
    this.routerSubscription = this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        if (event.url === '/customer/bookings' || event.urlAfterRedirects === '/customer/bookings') {
          console.log('[CustomerBookings] Route activated, refreshing bookings...');
          this.loadBookings();
        }
      });
  }

  ngOnDestroy() {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
  }

  loadBookings() {
    console.log('[CustomerBookings] ===== loadBookings() called =====');
    this.loading = true;
    this.errorMessage = '';
    
    console.log('[CustomerBookings] Calling getCurrentUser()...');
    this.authService.getCurrentUser().subscribe({
      next: (user: any) => {
        console.log('[CustomerBookings] getCurrentUser response:', user);
        this.userId = user?.id || null;
        console.log('[CustomerBookings] Extracted userId:', this.userId);
        
        if (this.userId) {
          console.log('[CustomerBookings] Loading bookings for customer:', this.userId);
          this.bookingService.getBookingsByCustomer(this.userId).subscribe({
            next: (data: any[]) => {
              console.log('[CustomerBookings] ===== Bookings API Response =====');
              console.log('[CustomerBookings] Bookings received:', data?.length || 0, data);
              console.log('[CustomerBookings] Raw bookings data:', JSON.stringify(data, null, 2));
              
              this.bookings = data.map(b => ({
                ...b,
                resourceName: b.resource?.name || '',
                providerName: b.resource?.providerId ? 'Provider #' + b.resource.providerId : '',
                date: b.startTime ? new Date(b.startTime).toLocaleDateString() : '',
                startTime: b.startTime ? new Date(b.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '',
                endTime: b.endTime ? new Date(b.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '',
                finalAmount: b.finalAmount || b.amount || 0
              }));
              this.loading = false;
              console.log('[CustomerBookings] Successfully loaded', this.bookings.length, 'bookings');
              console.log('[CustomerBookings] Mapped bookings:', this.bookings);
            },
            error: (error) => {
              console.error('[CustomerBookings] ===== Error loading bookings =====');
              console.error('[CustomerBookings] Error object:', error);
              console.error('[CustomerBookings] Error status:', error?.status);
              console.error('[CustomerBookings] Error message:', error?.message);
              console.error('[CustomerBookings] Error details:', {
                status: error?.status,
                statusText: error?.statusText,
                message: error?.message,
                error: error?.error,
                url: error?.url
              });
              this.errorMessage = 'Failed to load bookings. Please try again.';
              this.loading = false;
            }
          });
        } else {
          console.warn('[CustomerBookings] No userId found. User object:', user);
          this.errorMessage = 'Unable to get user information. Please try again.';
          this.loading = false;
        }
      },
      error: (error) => {
        console.error('[CustomerBookings] ===== Error getting current user =====');
        console.error('[CustomerBookings] Error object:', error);
        console.error('[CustomerBookings] Error details:', {
          status: error?.status,
          statusText: error?.statusText,
          message: error?.message,
          error: error?.error,
          url: error?.url
        });
        this.errorMessage = 'Failed to get user information. Please try again.';
        this.loading = false;
      }
    });
  }


  cancelBooking(bookingId: number) {
    if (confirm('Are you sure you want to cancel this booking?')) {
      this.loading = true;
      this.errorMessage = '';
      
      this.bookingService.cancelBooking(bookingId).subscribe({
        next: (response: any) => {
          // Since booking is deleted, refresh the entire list
          this.refreshBookings();
          this.loading = false;
          console.log('Booking cancelled and deleted successfully');
        },
        error: (error) => {
          this.loading = false;
          this.errorMessage = 'Failed to cancel booking. Please try again.';
          console.error('Error cancelling booking:', error);
        }
      });
    }
  }

  private refreshBookings() {
    this.loadBookings();
  }

  isPastBooking(booking: Booking): boolean {
  return new Date(booking.endTime || '') < new Date();
  }
}
