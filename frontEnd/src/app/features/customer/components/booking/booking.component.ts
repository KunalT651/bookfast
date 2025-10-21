import { Component, Input, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Booking } from '../../models/booking.model';
import { AvailabilitySlot } from '../../models/availability-slot.model';
import { BookingService } from '../../services/booking.service';
import { ResourceService } from '../../services/resource.service';
import { Resource } from '../../models/resource.model';
import { AvailabilitySlotService } from '../../services/availability-slot.service';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-booking',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.css']
})
export class BookingComponent {
  @Input() slots: AvailabilitySlot[] = [];
  @Input() resourceId!: number;
  @Output() payment = new EventEmitter<Booking>();
  booking: Booking | null = null;
  customerName: string = '';
  customerEmail: string = '';
  customerPhone: string = '';
  description: string = '';
  resource: Resource | null = null;
  slotDetails: AvailabilitySlot[] = [];
  amount: number | null = null;
  totalAmount: number = 0;

  constructor(
    private bookingService: BookingService,
    private route: ActivatedRoute,
    private resourceService: ResourceService,
    private slotService: AvailabilitySlotService,
    private authService: AuthService
  ) {
    // Fetch user info for auto-population
    this.authService.getCurrentUser().subscribe((user: any) => {
      if (user) {
        this.customerName = user.name || '';
        this.customerEmail = user.email || '';
        this.customerPhone = user.phone || '';
      }
    });
    this.route.queryParams.subscribe(params => {
      if (!this.resourceId && params['resourceId']) {
        this.resourceId = +params['resourceId'];
      }
      // Multi-slot support
      if ((!this.slots || !this.slots.length) && params['slotIds']) {
        const slotIds = params['slotIds'].split(',').map((id: string) => +id);
        this.slotService.getSlotsForResource(this.resourceId).subscribe(slots => {
          this.slotDetails = slots.filter(s => slotIds.includes(s.id!));
          this.slots = this.slotDetails;
        });
      }
      // Fetch resource details
      if (this.resourceId) {
        this.resourceService.getResources().subscribe(resources => {
          this.resource = resources.find(r => r.id === this.resourceId) || null;
          this.amount = this.resource?.price || null;
          if (this.amount && this.slots) {
            this.totalAmount = this.amount * this.slots.length;
          }
        });
      }
    });
  }

  confirmBooking() {
    // Create a booking for each slot
    if (this.slots && this.slots.length) {
      this.slots.forEach(slot => {
        const booking: Booking = {
          id: 0,
          resourceId: this.resourceId,
          slotId: slot.id ?? 0,
          customerName: this.customerName,
          customerEmail: this.customerEmail,
          customerPhone: this.customerPhone,
          status: 'pending',
          paymentStatus: 'unpaid',
          amount: this.amount || 0,
        };
        this.bookingService.createBooking(booking).subscribe(
          (created: Booking) => {
            this.booking = created;
          }
        );
      });
    }
  }

  proceedToPayment() {
    if (this.booking) {
      this.payment.emit(this.booking);
    }
  }
}
