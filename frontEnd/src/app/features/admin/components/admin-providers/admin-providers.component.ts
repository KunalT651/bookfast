import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminProviderService } from '../../services/admin-provider.service';
import { ServiceCategoryService } from '../../services/service-category.service';

@Component({
  selector: 'app-admin-providers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-providers.component.html',
  styleUrls: ['./admin-providers.component.css']
})
export class AdminProvidersComponent implements OnInit {
  providers: any[] = [];
  filteredProviders: any[] = [];
  serviceCategories: any[] = [];
  searchTerm = '';
  selectedProvider: any = null;
  showProviderModal = false;
  loading = false;
  errorMessage = '';
  successMessage = '';

  // Provider form fields
  providerForm = {
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    organizationName: '',
    serviceCategory: '',
    isActive: true
  };

  constructor(
    private adminProviderService: AdminProviderService,
    private serviceCategoryService: ServiceCategoryService
  ) {}

  ngOnInit() {
    this.loadProviders();
    this.loadServiceCategories();
  }

  loadProviders() {
    this.loading = true;
    this.errorMessage = '';
    this.adminProviderService.getAllProviders().subscribe({
      next: (providers) => {
        this.providers = providers;
        this.filteredProviders = providers;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load providers';
        this.loading = false;
        console.error('Error loading providers:', error);
      }
    });
  }

  loadServiceCategories() {
    this.serviceCategoryService.getAll().subscribe({
      next: (categories) => {
        this.serviceCategories = categories;
      },
      error: (error) => {
        console.error('Error loading service categories:', error);
      }
    });
  }

  searchProviders() {
    if (!this.searchTerm.trim()) {
      this.filteredProviders = this.providers;
      return;
    }

    this.filteredProviders = this.providers.filter(provider =>
      provider.firstName?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      provider.lastName?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      provider.email?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      provider.organizationName?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      provider.serviceCategory?.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  openProviderModal(provider: any = null) {
    this.selectedProvider = provider;
    if (provider) {
      this.providerForm = {
        firstName: provider.firstName || '',
        lastName: provider.lastName || '',
        email: provider.email || '',
        password: '', // Password not shown for editing
        organizationName: provider.organizationName || '',
        serviceCategory: provider.serviceCategory || '',
        isActive: provider.isActive !== false
      };
    } else {
      this.providerForm = {
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        organizationName: '',
        serviceCategory: '',
        isActive: true
      };
    }
    this.showProviderModal = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  closeProviderModal() {
    this.showProviderModal = false;
    this.selectedProvider = null;
    this.errorMessage = '';
    this.successMessage = '';
  }

  saveProvider() {
    if (!this.providerForm.firstName || !this.providerForm.lastName || !this.providerForm.email) {
      this.errorMessage = 'Please fill in all required fields';
      return;
    }

    // Validate password for new providers
    if (!this.selectedProvider && (!this.providerForm.password || this.providerForm.password.trim().length < 6)) {
      this.errorMessage = 'Password is required and must be at least 6 characters long';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    if (this.selectedProvider) {
      // Update existing provider
      this.adminProviderService.updateProvider(this.selectedProvider.id, this.providerForm).subscribe({
        next: () => {
          this.successMessage = 'Provider updated successfully';
          this.loadProviders();
          this.closeProviderModal();
          this.loading = false;
        },
        error: (error) => {
          this.errorMessage = 'Failed to update provider';
          this.loading = false;
          console.error('Error updating provider:', error);
        }
      });
    } else {
      // Create new provider - only send password for new providers
      const createData = { ...this.providerForm };
      this.adminProviderService.createProvider(createData).subscribe({
        next: (createdProvider) => {
          this.successMessage = 'Provider created successfully';
          // Refresh the providers list to show the newly created provider
          this.loadProviders();
          this.closeProviderModal();
          this.loading = false;
          console.log('Provider created:', createdProvider);
        },
        error: (error) => {
          this.errorMessage = error.error?.error || error.error?.message || 'Failed to create provider';
          this.loading = false;
          console.error('Error creating provider:', error);
        }
      });
    }
  }

  deleteProvider(provider: any) {
    if (!confirm(`Are you sure you want to delete provider ${provider.firstName} ${provider.lastName}?`)) {
      return;
    }

    this.loading = true;
    this.adminProviderService.deleteProvider(provider.id).subscribe({
      next: () => {
        this.successMessage = 'Provider deleted successfully';
        this.loadProviders();
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to delete provider';
        this.loading = false;
        console.error('Error deleting provider:', error);
      }
    });
  }

  toggleProviderStatus(provider: any) {
    const newStatus = !provider.isActive;
    this.adminProviderService.updateProviderStatus(provider.id, newStatus).subscribe({
      next: () => {
        provider.isActive = newStatus;
        this.successMessage = `Provider ${newStatus ? 'activated' : 'deactivated'} successfully`;
      },
      error: (error) => {
        this.errorMessage = 'Failed to update provider status';
        console.error('Error updating provider status:', error);
      }
    });
  }

  getStatusBadgeClass(isActive: boolean) {
    return isActive ? 'status-active' : 'status-inactive';
  }

  getStatusText(isActive: boolean) {
    return isActive ? 'Active' : 'Inactive';
  }
}