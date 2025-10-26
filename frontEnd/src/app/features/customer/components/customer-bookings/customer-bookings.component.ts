import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
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
export class CustomerBookingsComponent implements OnInit {
  bookings: Booking[] = [];
  userId: number | null = null;
  loading = false;
  errorMessage = '';

  constructor(private bookingService: BookingService, private authService: AuthService) {}

  ngOnInit() {
    this.authService.getCurrentUser().subscribe((user: any) => {
      this.userId = user?.id || null;
      if (this.userId) {
        this.bookingService.getBookingsByCustomer(this.userId).subscribe((data: any[]) => {
          this.bookings = data.map(b => ({
            ...b,
            resourceName: b.resource?.name || '',
            providerName: b.resource?.providerId ? 'Provider #' + b.resource.providerId : '',
            date: b.startTime ? new Date(b.startTime).toLocaleDateString() : '',
            startTime: b.startTime ? new Date(b.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '',
            endTime: b.endTime ? new Date(b.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '',
            finalAmount: b.finalAmount || b.amount || 0
          }));
        });
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
    if (this.userId) {
      this.bookingService.getBookingsByCustomer(this.userId).subscribe((data: any[]) => {
        this.bookings = data.map(b => ({
          ...b,
          resourceName: b.resource?.name || '',
          providerName: b.resource?.providerId ? 'Provider #' + b.resource.providerId : '',
          date: b.startTime ? new Date(b.startTime).toLocaleDateString() : '',
          startTime: b.startTime ? new Date(b.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '',
          endTime: b.endTime ? new Date(b.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '',
          finalAmount: b.finalAmount || b.amount || 0
        }));
      });
    }
  }

  isPastBooking(booking: Booking): boolean {
  return new Date(booking.endTime || '') < new Date();
  }
}
