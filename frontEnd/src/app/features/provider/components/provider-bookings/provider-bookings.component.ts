import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookingService } from '../../services/booking.service';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-provider-bookings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './provider-bookings.component.html',
  styleUrls: ['./provider-bookings.component.css']
})
export class ProviderBookingsComponent implements OnInit {
  bookings: any[] = [];
  providerId: number | null = null;
  showEditModal = false;
  editingBooking: any = null;
  loading = false;
  errorMessage = '';
  successMessage = '';
  editForm = {
    status: '',
    startTime: '',
    endTime: ''
  };

  constructor(private bookingService: BookingService, private authService: AuthService) {}

  ngOnInit() {
    console.log('[ProviderBookings] Component initialized, loading bookings...');
    this.loadBookings();
  }

  loadBookings() {
    console.log('[ProviderBookings] Starting to load bookings...');
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    this.authService.getCurrentUser().subscribe({
      next: (user: any) => {
        console.log('[ProviderBookings] Current user:', user);
        if (user && user.id) {
          this.providerId = user.id;
          console.log('[ProviderBookings] Provider ID:', this.providerId);
          console.log('[ProviderBookings] Making API call to get bookings...');
          
          this.bookingService.getBookingsByProvider().subscribe({
            next: (data) => {
              console.log('[ProviderBookings] Bookings received:', data);
              this.bookings = data;
              this.loading = false;
            },
            error: (error) => {
              this.errorMessage = 'Failed to load bookings. Please try again.';
              this.loading = false;
              console.error('[ProviderBookings] Error loading bookings:', error);
            }
          });
        } else {
          this.errorMessage = 'No authenticated provider found';
          this.loading = false;
          console.error('[ProviderBookings] No authenticated provider found');
        }
      },
      error: (error) => {
        this.errorMessage = 'Failed to get current user';
        this.loading = false;
        console.error('[ProviderBookings] Error getting current user:', error);
      }
    });
  }


  deleteBooking(bookingId: number) {
    if (!this.providerId) return;
    if (confirm('Are you sure you want to delete this booking? This action cannot be undone.')) {
      console.log('[ProviderBookings] Deleting booking:', bookingId);
      this.bookingService.deleteBooking(bookingId).subscribe({
        next: () => {
          this.bookings = this.bookings.filter(b => b.id !== bookingId);
          console.log('[ProviderBookings] Booking deleted successfully');
          this.errorMessage = ''; // Clear any previous errors
          this.successMessage = 'Booking deleted successfully!';
          setTimeout(() => this.successMessage = '', 3000); // Clear after 3 seconds
        },
        error: (error) => {
          this.errorMessage = 'Failed to delete booking. Please try again.';
          console.error('[ProviderBookings] Error deleting booking:', error);
        }
      });
    }
  }

  editBooking(booking: any) {
    console.log('[ProviderBookings] Opening edit modal for booking:', booking);
    this.editingBooking = booking;
    
    // Format datetime for input fields
    const formatDateTimeForInput = (dateTime: any) => {
      if (!dateTime) return '';
      const date = new Date(dateTime);
      if (isNaN(date.getTime())) return '';
      return date.toISOString().slice(0, 16); // Format: YYYY-MM-DDTHH:MM
    };
    
    this.editForm = {
      status: booking.status || '',
      startTime: formatDateTimeForInput(booking.startTime),
      endTime: formatDateTimeForInput(booking.endTime)
    };
    this.showEditModal = true;
  }

  closeEditModal() {
    this.showEditModal = false;
    this.editingBooking = null;
    this.editForm = {
      status: '',
      startTime: '',
      endTime: ''
    };
  }

  submitEdit() {
    if (!this.providerId || !this.editingBooking) return;
    
    // Convert datetime strings back to proper format for backend
    const editData = {
      status: this.editForm.status,
      startTime: this.editForm.startTime ? new Date(this.editForm.startTime).toISOString() : null,
      endTime: this.editForm.endTime ? new Date(this.editForm.endTime).toISOString() : null
    };
    
    console.log('[ProviderBookings] Editing booking:', this.editingBooking.id, 'with data:', editData);
    console.log('[ProviderBookings] Provider ID:', this.providerId);
    console.log('[ProviderBookings] JWT Token present:', !!document.cookie.includes('jwt'));
    
    this.bookingService.providerEditBooking(this.providerId, this.editingBooking.id, editData).subscribe({
      next: (updated) => {
        this.bookings = this.bookings.map(b => b.id === this.editingBooking.id ? updated : b);
        console.log('[ProviderBookings] Booking updated successfully:', updated);
        this.closeEditModal();
        this.errorMessage = ''; // Clear any previous errors
        this.successMessage = 'Booking updated successfully!';
        setTimeout(() => this.successMessage = '', 3000); // Clear after 3 seconds
      },
      error: (error) => {
        this.errorMessage = 'Failed to update booking. Please try again.';
        console.error('[ProviderBookings] Error updating booking:', error);
        console.error('[ProviderBookings] Error status:', error.status);
        console.error('[ProviderBookings] Error message:', error.message);
        console.error('[ProviderBookings] Error details:', error.error);
      }
    });
  }
}
