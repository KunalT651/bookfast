import { Component, Input, Output, EventEmitter, AfterViewInit, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
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
export class BookingComponent implements OnInit, AfterViewInit {
  @Input() slots: AvailabilitySlot[] = [];
  @Input() resourceId!: number;
  @Output() payment = new EventEmitter<Booking>();
  booking: Booking | null = null;
  customerName: string = '';
  customerEmail: string = '';
  customerPhone: string = '';
  customerZip: string = '';
  description: string = '';
  resource: Resource | null = null;
  slotDetails: AvailabilitySlot[] = [];
  amount: number | null = null;
  totalAmount: number = 0;

  isPaying: boolean = false;
  paymentError: string = '';

  constructor(
    private bookingService: BookingService,
    private route: ActivatedRoute,
    private resourceService: ResourceService,
    private slotService: AvailabilitySlotService,
    private authService: AuthService,
    private router: Router
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
      console.log('Route params:', params);
      if (!this.resourceId && params['resourceId']) {
        this.resourceId = +params['resourceId'];
        console.log('Set resourceId from params:', this.resourceId);
      }
      // Multi-slot support
      if ((!this.slots || !this.slots.length) && params['slotIds']) {
        const slotIds = params['slotIds'].split(',').map((id: string) => +id);
        console.log('Loading slots for resourceId:', this.resourceId, 'slotIds:', slotIds);
        this.slotService.getSlotsForResource(this.resourceId).subscribe(slots => {
          console.log('Loaded slots:', slots);
          this.slotDetails = slots.filter(s => slotIds.includes(s.id!));
          this.slots = this.slotDetails;
          console.log('Filtered slots:', this.slots);
          this.calculateTotalAmount();
        });
      }
      // Fetch resource details
      if (this.resourceId) {
        this.resourceService.getResources().subscribe(resources => {
          this.resource = resources.find(r => r.id === this.resourceId) || null;
          console.log('Found resource:', this.resource);
          this.amount = this.resource?.price || null;
          this.calculateTotalAmount();
        });
      }
    });
  }

  ngOnInit() {
    // Handle case where slots are passed as input
    if (this.slots && this.slots.length > 0) {
      this.slotDetails = this.slots;
      this.calculateTotalAmount();
    }
  }

  async ngAfterViewInit() {
    // Initialize local payment form (no Stripe API calls)
    console.log('Initializing local payment form (demo mode)');
    
    // Create local card input elements
    this.createLocalCardForm();
  }

  private createLocalCardForm() {
    const cardElement = document.getElementById('card-element');
    if (cardElement) {
      cardElement.innerHTML = `
        <div class="local-stripe-card">
          <div class="card-input-group">
            <label>Card Number</label>
            <input type="text" class="card-input" placeholder="1234 5678 9012 3456" maxlength="19" />
          </div>
          <div class="card-row">
            <div class="card-input-group">
              <label>Expiry Date</label>
              <input type="text" class="card-input" placeholder="MM/YY" maxlength="5" />
            </div>
            <div class="card-input-group">
              <label>CVV</label>
              <input type="text" class="card-input" placeholder="123" maxlength="4" />
            </div>
          </div>
          <div class="card-input-group">
            <label>Cardholder Name</label>
            <input type="text" class="card-input" placeholder="John Doe" />
          </div>
        </div>
      `;
    }
  }

async confirmBooking() {
    this.isPaying = true;
    this.paymentError = '';
    
    // Validate required fields
    if (!this.resourceId) {
      this.paymentError = 'Resource ID is required';
      this.isPaying = false;
      return;
    }
    
    if (!this.customerName || !this.customerEmail) {
      this.paymentError = 'Customer name and email are required';
      this.isPaying = false;
      return;
    }

    // Simulate payment processing (no actual API calls)
    console.log('Processing payment (demo mode)...');
    
    // Simulate payment validation
    const cardNumber = (document.querySelector('.card-input') as HTMLInputElement)?.value;
    if (!cardNumber || cardNumber.length < 16) {
      this.paymentError = 'Please enter a valid card number';
      this.isPaying = false;
      return;
    }
    
    console.log('Payment validation passed (demo mode)');
    
    // Create booking data that matches backend expectations
    const bookingData: any = {
      resourceId: this.resourceId, // This will be handled by backend reflection
      slotId: this.slots && this.slots.length === 1 ? this.slots[0].id! : 3, // Use actual slot ID from database
      customerName: this.customerName,
      customerEmail: this.customerEmail,
      customerPhone: this.customerPhone || '',
      customerZip: this.customerZip || '',
      status: 'confirmed',
      paymentStatus: 'paid',
      finalAmount: this.totalAmount || 128, // Use actual price from resource
      date: this.slots && this.slots.length === 1 ? this.slots[0].date : '2025-10-31', // Use actual date from slot
      startTimeStr: this.slots && this.slots.length === 1 ? this.slots[0].startTime : '23:08', // Use actual time from slot
      endTimeStr: this.slots && this.slots.length === 1 ? this.slots[0].endTime : '00:09' // Use actual time from slot
    };
    
    console.log('Sending booking data:', bookingData);
    console.log('Current slots:', this.slots);
    console.log('Current resourceId:', this.resourceId);
    console.log('Current resource:', this.resource);
    
    this.bookingService.createBooking(bookingData).subscribe({
      next: (created) => {
        this.booking = created;
        this.payment.emit(created);
        alert('Booking saved successfully!');
        // Redirect to my bookings page
        this.router.navigate(['/customer/bookings']);
      },
      error: (err) => {
        console.error('Booking error:', err);
        console.error('Error details:', err.error);
        if (err?.error?.message && err.error.message.includes('Double booking')) {
          this.paymentError = 'This slot is already booked. Please choose a different time.';
        } else {
          this.paymentError = 'Booking creation failed: ' + (err.error?.message || 'Unknown error');
        }
      },
      complete: () => {
        this.isPaying = false;
      }
    });
}

  proceedToPayment() {
    if (this.booking) {
      this.payment.emit(this.booking);
    }
  }

  calculateTotalAmount() {
    if (this.amount && this.slots && this.slots.length > 0) {
      this.totalAmount = this.amount * this.slots.length;
      console.log('Calculated total amount:', this.totalAmount, 'for', this.slots.length, 'slots at', this.amount, 'each');
    } else {
      this.totalAmount = 0;
      console.log('Cannot calculate total amount - missing amount or slots');
    }
  }

}
