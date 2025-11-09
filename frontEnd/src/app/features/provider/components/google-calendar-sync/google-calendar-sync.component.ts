import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../../environments/environment';

interface Booking {
  id: number;
  customerName: string;
  customerEmail: string;
  startTime: string;
  endTime: string;
  status: string;
  resource: {
    name: string;
  };
  finalAmount?: number;
}

interface CalendarDay {
  date: Date;
  day: number;
  isCurrentMonth: boolean;
  isToday: boolean;
  bookings: Booking[];
  bookingCount: number;
}

@Component({
  selector: 'app-google-calendar-sync',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './google-calendar-sync.component.html',
  styleUrls: ['./google-calendar-sync.component.css']
})
export class GoogleCalendarSyncComponent implements OnInit {
  currentDate: Date = new Date();
  currentMonth: number = new Date().getMonth();
  currentYear: number = new Date().getFullYear();
  calendarDays: CalendarDay[] = [];
  weekDays: string[] = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
  monthNames: string[] = ['January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'];
  
  allBookings: Booking[] = [];
  loading = false;
  errorMessage = '';
  
  // Modal
  showBookingModal = false;
  selectedDate: Date | null = null;
  selectedDayBookings: Booking[] = [];
  hoveredDay: CalendarDay | null = null;
  
  // Edit booking form
  editingBooking: Booking | null = null;
  bookingForm: FormGroup;

  constructor(private http: HttpClient, private fb: FormBuilder) {
    this.bookingForm = this.fb.group({
      status: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.loadBookings();
  }

  loadBookings() {
    this.loading = true;
    this.http.get<Booking[]>(`${environment.apiUrl}/bookings/provider/me`, { withCredentials: true }).subscribe({
      next: (bookings) => {
        this.allBookings = bookings;
        this.generateCalendar();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading bookings:', error);
        this.errorMessage = 'Failed to load bookings';
        this.loading = false;
      }
    });
  }

  generateCalendar() {
    this.calendarDays = [];
    const firstDay = new Date(this.currentYear, this.currentMonth, 1);
    const lastDay = new Date(this.currentYear, this.currentMonth + 1, 0);
    const prevLastDay = new Date(this.currentYear, this.currentMonth, 0);
    
    const firstDayOfWeek = firstDay.getDay();
    const lastDateOfMonth = lastDay.getDate();
    const prevLastDate = prevLastDay.getDate();
    
    // Previous month days
    for (let i = firstDayOfWeek; i > 0; i--) {
      const date = new Date(this.currentYear, this.currentMonth - 1, prevLastDate - i + 1);
      this.calendarDays.push(this.createCalendarDay(date, false));
    }
    
    // Current month days
    for (let i = 1; i <= lastDateOfMonth; i++) {
      const date = new Date(this.currentYear, this.currentMonth, i);
      this.calendarDays.push(this.createCalendarDay(date, true));
    }
    
    // Next month days
    const remainingDays = 42 - this.calendarDays.length; // 6 rows * 7 days
    for (let i = 1; i <= remainingDays; i++) {
      const date = new Date(this.currentYear, this.currentMonth + 1, i);
      this.calendarDays.push(this.createCalendarDay(date, false));
    }
  }

  createCalendarDay(date: Date, isCurrentMonth: boolean): CalendarDay {
    const today = new Date();
    const isToday = date.toDateString() === today.toDateString();
    
    // Filter bookings for this day
    const dayBookings = this.allBookings.filter(booking => {
      const bookingDate = new Date(booking.startTime);
      return bookingDate.toDateString() === date.toDateString();
    });
    
    return {
      date: date,
      day: date.getDate(),
      isCurrentMonth: isCurrentMonth,
      isToday: isToday,
      bookings: dayBookings,
      bookingCount: dayBookings.length
    };
  }

  previousMonth() {
    if (this.currentMonth === 0) {
      this.currentMonth = 11;
      this.currentYear--;
    } else {
      this.currentMonth--;
    }
    this.generateCalendar();
  }

  nextMonth() {
    if (this.currentMonth === 11) {
      this.currentMonth = 0;
      this.currentYear++;
    } else {
      this.currentMonth++;
    }
    this.generateCalendar();
  }

  goToToday() {
    const today = new Date();
    this.currentMonth = today.getMonth();
    this.currentYear = today.getFullYear();
    this.generateCalendar();
  }

  onDayHover(day: CalendarDay) {
    this.hoveredDay = day;
  }

  onDayLeave() {
    this.hoveredDay = null;
  }

  onDayClick(day: CalendarDay) {
    this.selectedDate = day.date;
    this.selectedDayBookings = day.bookings;
    this.showBookingModal = true;
  }

  closeModal() {
    this.showBookingModal = false;
    this.selectedDate = null;
    this.selectedDayBookings = [];
    this.editingBooking = null;
  }

  editBooking(booking: Booking) {
    this.editingBooking = booking;
    this.bookingForm.patchValue({
      status: booking.status
    });
  }

  cancelEdit() {
    this.editingBooking = null;
    this.bookingForm.reset();
  }

  saveBooking() {
    if (!this.editingBooking || this.bookingForm.invalid) return;
    
    const updatedStatus = this.bookingForm.value.status;
    
    this.http.put(`${environment.apiUrl}/bookings/provider/${this.editingBooking.id}/edit/${updatedStatus}`, 
      {}, { withCredentials: true }).subscribe({
      next: () => {
        // Update local booking
        this.editingBooking!.status = updatedStatus;
        this.loadBookings();
        this.cancelEdit();
      },
      error: (error) => {
        this.errorMessage = 'Failed to update booking';
        console.error(error);
      }
    });
  }

  cancelBooking(booking: Booking) {
    if (!confirm(`Cancel booking for ${booking.customerName}?`)) return;
    
    this.http.put(`${environment.apiUrl}/bookings/provider/${booking.id}/cancel/cancelled`, 
      {}, { withCredentials: true }).subscribe({
      next: () => {
        booking.status = 'cancelled';
        this.loadBookings();
      },
      error: (error) => {
        this.errorMessage = 'Failed to cancel booking';
        console.error(error);
      }
    });
  }

  formatTime(dateString: string): string {
    return new Date(dateString).toLocaleTimeString('en-US', { 
      hour: 'numeric', 
      minute: '2-digit',
      hour12: true 
    });
  }

  formatDate(date: Date): string {
    return date.toLocaleDateString('en-US', { 
      weekday: 'long', 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    });
  }

  getStatusClass(status: string): string {
    return status.toLowerCase();
  }
}
