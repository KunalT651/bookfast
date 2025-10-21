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
            endTime: b.endTime ? new Date(b.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : ''
          }));
        });
      }
    });
  }

  deleteBooking(bookingId: number) {
    this.bookingService.deleteBooking(bookingId).subscribe(() => {
      this.bookings = this.bookings.filter(b => b.id !== bookingId);
    });
  }

  isPastBooking(booking: Booking): boolean {
  return new Date(booking.endTime || '') < new Date();
  }
}
