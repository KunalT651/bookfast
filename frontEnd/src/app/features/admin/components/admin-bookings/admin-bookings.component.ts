import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminBookingService } from '../../services/admin-booking.service';
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
    private adminBookingService: AdminBookingService,
    private adminReportService: AdminReportService
  ) {}

  ngOnInit() {
    this.loadBookings();
  }

  loadBookings() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    console.log('[AdminBookingsComponent] Loading bookings using AdminBookingService');
    this.adminBookingService.getAllBookings().subscribe({
      next: (data: any) => {
        console.log('[AdminBookingsComponent] Received bookings in next callback:', data);
        // Handle both array and object responses
        if (Array.isArray(data)) {
          this.bookings = data;
          console.log('[AdminBookingsComponent] Set bookings array with', data.length, 'items');
        } else if (data && Array.isArray(data.bookings)) {
          this.bookings = data.bookings;
        } else if (data && Array.isArray(data.data)) {
          this.bookings = data.data;
        } else if (data === null || data === undefined) {
          console.log('[AdminBookingsComponent] Null/undefined response, setting empty array');
          this.bookings = [];
        } else {
          console.warn('[AdminBookingsComponent] Unexpected data format:', typeof data, data);
          this.bookings = [];
        }
        this.applyFilters();
        this.loading = false;
        this.errorMessage = '';
      },
      error: (error: any) => {
        console.error('[AdminBookingsComponent] Error callback triggered');
        console.error('[AdminBookingsComponent] Full error object:', error);
        console.error('[AdminBookingsComponent] Error status:', error?.status);
        console.error('[AdminBookingsComponent] Error message:', error?.message);
        console.error('[AdminBookingsComponent] Error URL:', error?.url);
        console.error('[AdminBookingsComponent] Error error:', error?.error);
        
        // If status is 200, the response might be in error.error
        if (error?.status === 200) {
          console.log('[AdminBookingsComponent] Status 200 but in error callback, checking error.error');
          const responseData = error?.error;
          if (Array.isArray(responseData)) {
            console.log('[AdminBookingsComponent] Found array in error.error, using it');
            this.bookings = responseData;
            this.applyFilters();
            this.loading = false;
            this.errorMessage = '';
            return;
          } else if (responseData === null || responseData === undefined || responseData === '') {
            console.log('[AdminBookingsComponent] Empty response with status 200, setting empty array');
            this.bookings = [];
            this.applyFilters();
            this.loading = false;
            this.errorMessage = '';
            return;
          }
        }
        
        // For actual errors
        this.errorMessage = error?.error?.message || error?.message || 'Failed to load bookings. Please try again.';
        this.loading = false;
        this.bookings = [];
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

    this.loading = true;
    this.adminBookingService.updateBooking(this.editingBooking.id, this.editForm).subscribe({
      next: (updatedBooking: any) => {
        const index = this.bookings.findIndex(b => b.id === this.editingBooking.id);
        if (index !== -1) {
          this.bookings[index] = updatedBooking;
          this.applyFilters();
        }
        this.editingBooking = null;
        this.successMessage = 'Booking updated successfully!';
        this.loading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error: any) => {
        this.errorMessage = 'Failed to update booking. Please try again.';
        this.loading = false;
        console.error('Error updating booking:', error);
      }
    });
  }

  cancelEdit() {
    this.editingBooking = null;
    this.editForm = {};
  }

  deleteBooking(id: number) {
    if (confirm('Are you sure you want to delete this booking? This action cannot be undone.')) {
      this.loading = true;
      this.adminBookingService.deleteBooking(id).subscribe({
        next: () => {
          this.bookings = this.bookings.filter(b => b.id !== id);
          this.applyFilters();
          this.successMessage = 'Booking deleted successfully!';
          this.loading = false;
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error: any) => {
          this.errorMessage = 'Failed to delete booking. Please try again.';
          this.loading = false;
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
    this.successMessage = '';
    
    // Export all bookings from last 365 days
    this.adminReportService.exportReport('bookings', '365').subscribe({
      next: (data: any) => {
        // Create download link for CSV file
        const blob = new Blob([data], { type: 'text/csv;charset=utf-8;' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        const date = new Date().toISOString().split('T')[0];
        const filename = `bookfast_bookings_${date}.csv`;
        link.download = filename;
        link.click();
        window.URL.revokeObjectURL(url);
        
        // Show success message with booking count
        const bookingCount = this.bookings.length;
        this.successMessage = `Successfully exported ${bookingCount} booking${bookingCount !== 1 ? 's' : ''} to CSV!`;
        this.exporting = false;
        setTimeout(() => this.successMessage = '', 5000);
        
        console.log(`✅ Exported ${bookingCount} bookings to ${filename}`);
      },
      error: (error: any) => {
        this.errorMessage = 'Failed to export bookings. Please try again.';
        this.exporting = false;
        console.error('Error exporting bookings:', error);
        setTimeout(() => this.errorMessage = '', 5000);
      }
    });
  }
}
