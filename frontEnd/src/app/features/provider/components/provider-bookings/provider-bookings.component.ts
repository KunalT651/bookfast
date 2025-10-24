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
  editForm = {
    status: '',
    startTime: '',
    endTime: ''
  };

  constructor(private bookingService: BookingService, private authService: AuthService) {}

  ngOnInit() {
    // Get provider ID from authenticated user
    this.authService.getCurrentUser().subscribe((user: any) => {
      if (user && user.id) {
        this.providerId = user.id;
        this.bookingService.getBookingsByProvider().subscribe(data => {
          this.bookings = data;
        });
      } else {
        console.error('No authenticated provider found');
      }
    });
  }

  cancelBooking(bookingId: number) {
    if (!this.providerId) return;
    this.bookingService.providerCancelBooking(this.providerId, bookingId).subscribe(updated => {
      this.bookings = this.bookings.map(b => b.id === bookingId ? updated : b);
    });
  }

  editBooking(booking: any) {
    this.editingBooking = booking;
    this.editForm = {
      status: booking.status,
      startTime: booking.startTime,
      endTime: booking.endTime
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
    
    this.bookingService.providerEditBooking(this.providerId, this.editingBooking.id, this.editForm).subscribe(updated => {
      this.bookings = this.bookings.map(b => b.id === this.editingBooking.id ? updated : b);
      this.closeEditModal();
    });
  }
}
