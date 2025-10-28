import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminUserService } from '../../services/admin-user.service';

@Component({
  selector: 'app-admin-customers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-customers.component.html',
  styleUrls: ['./admin-customers.component.css']
})
export class AdminCustomersComponent implements OnInit {
  customers: any[] = [];
  filteredCustomers: any[] = [];
  loading = false;
  errorMessage = '';
  successMessage = '';
  searchTerm = '';
  editingCustomer: any = null;
  editForm: any = {};

  constructor(private adminUserService: AdminUserService) {}

  ngOnInit() {
    this.loadCustomers();
  }

  loadCustomers() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    this.adminUserService.getAllUsers().subscribe({
      next: (users) => {
        // Filter only customers
        this.customers = users.filter(user => user.role?.name === 'CUSTOMER');
        this.filteredCustomers = [...this.customers];
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load customers. Please try again.';
        this.loading = false;
        console.error('Error loading customers:', error);
      }
    });
  }

  searchCustomers() {
    if (!this.searchTerm.trim()) {
      this.filteredCustomers = [...this.customers];
      return;
    }

    const term = this.searchTerm.toLowerCase();
    this.filteredCustomers = this.customers.filter(customer =>
      customer.firstName?.toLowerCase().includes(term) ||
      customer.lastName?.toLowerCase().includes(term) ||
      customer.email?.toLowerCase().includes(term)
    );
  }

  startEdit(customer: any) {
    this.editingCustomer = customer;
    this.editForm = {
      firstName: customer.firstName || '',
      lastName: customer.lastName || '',
      email: customer.email || '',
      organizationName: customer.organizationName || '',
      serviceCategory: customer.serviceCategory || ''
    };
  }

  saveEdit() {
    if (!this.editingCustomer) return;

    this.adminUserService.updateUser(this.editingCustomer.id, this.editForm).subscribe({
      next: (updated) => {
        const index = this.customers.findIndex(c => c.id === updated.id);
        if (index !== -1) {
          this.customers[index] = updated;
          this.searchCustomers(); // Refresh filtered list
        }
        this.editingCustomer = null;
        this.successMessage = 'Customer updated successfully!';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = 'Failed to update customer. Please try again.';
        console.error('Error updating customer:', error);
      }
    });
  }

  cancelEdit() {
    this.editingCustomer = null;
    this.editForm = {};
  }

  deleteCustomer(id: number) {
    if (confirm('Are you sure you want to delete this customer? This action cannot be undone.')) {
      this.adminUserService.deleteUser(id).subscribe({
        next: () => {
          this.customers = this.customers.filter(c => c.id !== id);
          this.searchCustomers(); // Refresh filtered list
          this.successMessage = 'Customer deleted successfully!';
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = 'Failed to delete customer. Please try again.';
          console.error('Error deleting customer:', error);
        }
      });
    }
  }

  clearSearch() {
    this.searchTerm = '';
    this.searchCustomers();
  }
}
