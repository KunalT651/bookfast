import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Booking } from '../../models/booking.model';
import { BookingService } from '../../services/booking.service';

@Component({
  selector: 'app-payment',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css']
})
export class PaymentComponent {
  @Input() booking!: Booking;
  @Output() paymentSuccess = new EventEmitter<void>();

  constructor(private bookingService: BookingService) {}

  pay() {
    // Simulate payment (replace with Stripe integration if needed)
  const updated: Booking = { ...this.booking, paymentStatus: 'paid' };
    this.bookingService.createBooking(updated).subscribe(() => {
      this.booking.paymentStatus = 'paid';
      this.paymentSuccess.emit();
    });
  }
}
