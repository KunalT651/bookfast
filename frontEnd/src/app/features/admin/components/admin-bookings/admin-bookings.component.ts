import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookingService } from '../../../provider/services/booking.service';
import { AdminReportService } from '../../services/admin-report.service';

@Component({
  selector: 'app-admin-bookings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-bookings.component.html',
  styleUrls: ['./admin-bookings.component.css']
})
export class AdminBookingsComponent implements OnInit {
  bookings: any[] = [];
  filteredBookings: any[] = [];
  loading = false;
  errorMessage = '';
  successMessage = '';
  searchTerm = '';
  statusFilter = 'all';
  editingBooking: any = null;
  editForm: any = {};
  exporting = false;

  constructor(
    private bookingService: BookingService,
    private adminReportService: AdminReportService
  ) {}

  ngOnInit() {
    this.loadBookings();
  }

  loadBookings() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    // For now, we'll use the provider endpoint to get all bookings
    // In a real system, you'd have a dedicated admin endpoint
    this.bookingService.getBookingsByProvider().subscribe({
      next: (data) => {
        this.bookings = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load bookings. Please try again.';
        this.loading = false;
        console.error('Error loading bookings:', error);
      }
    });
  }

  searchBookings() {
    this.applyFilters();
  }

  applyFilters() {
    let filtered = [...this.bookings];

    // Apply search filter
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(booking =>
        booking.customerName?.toLowerCase().includes(term) ||
        booking.customerEmail?.toLowerCase().includes(term) ||
        booking.resourceName?.toLowerCase().includes(term) ||
        booking.providerName?.toLowerCase().includes(term)
      );
    }

    // Apply status filter
    if (this.statusFilter !== 'all') {
      filtered = filtered.filter(booking => booking.status === this.statusFilter);
    }

    this.filteredBookings = filtered;
  }

  onStatusFilterChange() {
    this.applyFilters();
  }

  startEdit(booking: any) {
    this.editingBooking = booking;
    this.editForm = {
      status: booking.status || '',
      paymentStatus: booking.paymentStatus || '',
      finalAmount: booking.finalAmount || 0
    };
  }

  saveEdit() {
    if (!this.editingBooking) return;

    // For now, we'll just update the local data
    // In a real system, you'd call an admin update endpoint
    const index = this.bookings.findIndex(b => b.id === this.editingBooking.id);
    if (index !== -1) {
      this.bookings[index] = { ...this.bookings[index], ...this.editForm };
      this.applyFilters();
    }
    this.editingBooking = null;
    this.successMessage = 'Booking updated successfully!';
    setTimeout(() => this.successMessage = '', 3000);
  }

  cancelEdit() {
    this.editingBooking = null;
    this.editForm = {};
  }

  deleteBooking(id: number) {
    if (confirm('Are you sure you want to delete this booking? This action cannot be undone.')) {
      this.bookingService.deleteBooking(id).subscribe({
        next: () => {
          this.bookings = this.bookings.filter(b => b.id !== id);
          this.applyFilters();
          this.successMessage = 'Booking deleted successfully!';
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = 'Failed to delete booking. Please try again.';
          console.error('Error deleting booking:', error);
        }
      });
    }
  }

  clearSearch() {
    this.searchTerm = '';
    this.applyFilters();
  }

  getStatusClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'confirmed': return 'status-confirmed';
      case 'pending': return 'status-pending';
      case 'cancelled': return 'status-cancelled';
      default: return 'status-default';
    }
  }

  getPaymentStatusClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'paid': return 'payment-paid';
      case 'pending': return 'payment-pending';
      case 'failed': return 'payment-failed';
      default: return 'payment-default';
    }
  }

  exportBookings() {
    this.exporting = true;
    this.errorMessage = '';
    
    this.adminReportService.exportReport('bookings', '365').subscribe({
      next: (data) => {
        // Create download link for CSV file
        const blob = new Blob([data], { type: 'text/csv;charset=utf-8;' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        const filename = `bookfast_bookings_${new Date().toISOString().split('T')[0]}.csv`;
        link.download = filename;
        link.click();
        window.URL.revokeObjectURL(url);
        
        this.successMessage = 'Bookings exported successfully!';
        this.exporting = false;
        setTimeout(() => this.successMessage = '', 3000);
        
        console.log(`✅ Bookings exported to ${filename}`);
      },
      error: (error) => {
        this.errorMessage = 'Failed to export bookings. Please try again.';
        this.exporting = false;
        console.error('Error exporting bookings:', error);
      }
    });
  }
}
