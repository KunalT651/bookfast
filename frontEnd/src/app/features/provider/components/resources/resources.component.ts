import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ResourceAvailabilityModalComponent } from '../resource-availability-modal/resource-availability-modal.component';
import { ResourceService } from '../../services/resource.service';
import { ProviderService } from '../../services/provider.service';
import { AuthService } from '../../../auth/services/auth.service';
import { Resource } from '../../models/resource.model';
import { AvailabilitySlot } from '../../models/availability-slot.model';
import { AvailabilitySlotService } from '../../services/availability-slot.service';

@Component({
  selector: 'app-provider-resources',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ResourceAvailabilityModalComponent],
  templateUrl: './resources.component.html',
  styleUrls: ['./resources.component.css']
})
export class ResourcesComponent implements OnInit {
  showAvailabilityModal = false;
  selectedResource: Resource | null = null;
  resources: Resource[] = [];
  showForm = false;
  resourceForm: FormGroup;
  availabilityForm: FormGroup;
  providerId: number | null = null;
  editingResource: Resource | null = null;
  availabilitySlots: AvailabilitySlot[] = [];
  
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private fb: FormBuilder,
    private resourceService: ResourceService,
    private providerService: ProviderService,
    private authService: AuthService,
    private availabilitySlotService: AvailabilitySlotService // Inject AvailabilitySlotService
  ) {
    this.resourceForm = this.fb.group({
      name: [''],
      description: [''],
      tags: [''],
      price: [''],
      experienceYears: [''],
      phone: [''],
      email: [''],
      imageUrl: [''],
      specialization: [''],
      status: ['active']
    });
    this.availabilityForm = this.fb.group({
      date: ['', Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required],
      status: ['available']
    });
  }

  ngOnInit(): void {
    this.loadProviderProfile();
  }

  deleteResource(resource: Resource) {
    if (!resource.id) {
      this.errorMessage = 'Resource ID not found.';
      setTimeout(() => this.errorMessage = '', 3000);
      return;
    }
    if (!confirm('Are you sure you want to delete this resource?')) return;
    
    console.log('[ResourcesComponent] Deleting resource:', resource.id);
    this.resourceService.deleteResource(resource.id).subscribe({
      next: () => {
        console.log('[ResourcesComponent] Resource deleted successfully');
        this.resources = this.resources.filter(r => r.id !== resource.id);
        this.successMessage = 'Resource deleted successfully!';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        console.error('[ResourcesComponent] Failed to delete resource:', err);
        this.errorMessage = 'Failed to delete resource';
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }

  openAvailability(resource: Resource) {
    this.selectedResource = resource;
    this.showAvailabilityModal = true;
    this.loadAvailabilitySlots(resource.id!); // Load slots when modal opens
  }

  closeAvailability() {
    this.showAvailabilityModal = false;
    this.selectedResource = null;
    this.availabilitySlots = []; // Clear slots when modal closes
  }

  loadAvailabilitySlots(resourceId: number) {
    console.log('[ResourcesComponent] Loading availability slots for resource:', resourceId);
    this.availabilitySlotService.getAvailabilitySlotsForResource(resourceId).subscribe({
      next: (slots) => {
        console.log('[ResourcesComponent] Availability slots loaded:', slots);
        this.availabilitySlots = slots;
      },
      error: (err) => {
        console.error('[ResourcesComponent] Failed to load availability slots:', err);
        this.errorMessage = 'Failed to load availability slots';
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }

  addAvailabilitySlot() {
    if (this.availabilityForm.invalid || this.selectedResource === null) {
      return;
    }

    const formValue = this.availabilityForm.value;
    const resourceId = this.selectedResource.id!;
    
    console.log('[ResourcesComponent] Creating availability slot for resource:', resourceId);

    this.availabilitySlotService.createAvailabilitySlot(
      resourceId,
      formValue.date,
      formValue.startTime,
      formValue.endTime,
      formValue.status
    ).subscribe({
      next: (newSlot) => {
        console.log('[ResourcesComponent] Availability slot created:', newSlot);
        this.availabilitySlots.push(newSlot);
        this.availabilityForm.reset({ status: 'available' });
        this.successMessage = 'Availability slot added successfully!';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        console.error('[ResourcesComponent] Failed to add availability slot:', err);
        this.errorMessage = 'Failed to add availability slot';
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }

  deleteAvailabilitySlot(slotId: number) {
    if (!confirm('Are you sure you want to delete this availability slot?')) {
      return;
    }
    console.log('[ResourcesComponent] Deleting availability slot:', slotId);
    this.availabilitySlotService.deleteAvailabilitySlot(this.selectedResource!.id!, slotId).subscribe({
      next: () => {
        console.log('[ResourcesComponent] Availability slot deleted successfully');
        this.availabilitySlots = this.availabilitySlots.filter(s => s.id !== slotId);
        this.successMessage = 'Availability slot deleted successfully!';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        console.error('[ResourcesComponent] Failed to delete availability slot:', err);
        this.errorMessage = 'Failed to delete availability slot';
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }

  loadProviderProfile() {
    // Get provider ID from current authenticated user (needed for creating resources)
    this.authService.getCurrentUser().subscribe({
      next: (user: any) => {
        console.log('[ResourcesComponent] Current user:', user);
        if (user && user.id) {
          this.providerId = user.id;
          console.log('[ResourcesComponent] Provider ID:', this.providerId);
        } else {
          console.error('[ResourcesComponent] No authenticated provider found');
          this.errorMessage = 'Failed to load provider information. Please log in again.';
        }
        // Load resources regardless (uses /me endpoint which doesn't need providerId)
        this.loadResources();
      },
      error: (error) => {
        console.error('[ResourcesComponent] Error getting current user:', error);
        // Still try to load resources (might work if auth cookie is valid)
        this.loadResources();
      }
    });
  }

  loadResources() {
    console.log('[ResourcesComponent] Loading resources for current provider');
    this.loading = true;
    this.errorMessage = '';
    
    // Use /me endpoint which gets resources for the authenticated provider
    this.resourceService.getResourcesForCurrentProvider().subscribe({
      next: (res: Resource[]) => {
        console.log('[ResourcesComponent] Resources loaded:', res);
        this.resources = res;
        this.loading = false;
      },
      error: (error) => {
        console.error('[ResourcesComponent] Error loading resources:', error);
        console.error('[ResourcesComponent] Error details:', error.error);
        this.errorMessage = 'Failed to load resources. Please try again.';
        this.loading = false;
      }
    });
  }

  openForm() {
    this.showForm = true;
    this.editingResource = null;
    this.resourceForm.reset();
  }

  openEditForm(resource: Resource) {
    this.showForm = true;
    this.editingResource = resource;
    this.resourceForm.patchValue({
      name: resource.name,
      description: resource.description,
      tags: resource.tags ? resource.tags.join(', ') : '',
      price: resource.price,
      experienceYears: resource.experienceYears,
      phone: resource.phone,
      email: resource.email,
      imageUrl: resource.imageUrl,
      specialization: resource.specialization,
      status: resource.status
    });
  }

  closeForm() {
    this.showForm = false;
    if (this.editingResource) {
      // Update existing resource
      const formValue = this.resourceForm.value;
      const updated: Resource = {
        id: this.editingResource.id, // Ensure id is always set
        ...this.editingResource,
        name: formValue.name || '',
        description: formValue.description || '',
        tags: formValue.tags ? formValue.tags.split(',').map((t: string) => t.trim()) : [],
        price: Number(formValue.price) || 0,
        experienceYears: Number(formValue.experienceYears) || 0,
        phone: formValue.phone || '',
        email: formValue.email || '',
        imageUrl: formValue.imageUrl || '',
        specialization: formValue.specialization || '',
        status: formValue.status || 'active'
      };
      console.log('[ResourcesComponent] Updating resource:', updated.id);
      this.resourceService.updateResource(updated).subscribe({
        next: (res: Resource) => {
          console.log('[ResourcesComponent] Resource updated successfully:', res);
          this.resources = this.resources.map(r => r.id === res.id ? res : r);
          this.editingResource = null;
          this.successMessage = 'Resource updated successfully!';
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => {
          console.error('[ResourcesComponent] Failed to update resource:', err);
          this.errorMessage = 'Failed to update resource';
          setTimeout(() => this.errorMessage = '', 3000);
        }
      });
    }
  }

  onSubmit() {
    if (this.editingResource) {
      this.closeForm();
    }
    else {
      this.addResource();
    }
  }

  addResource() {
    if (this.providerId === null) {
      this.errorMessage = 'Provider ID not found. Please log in again.';
      setTimeout(() => this.errorMessage = '', 3000);
      return;
    }
    const formValue = this.resourceForm.value;
    const resource: Resource = {
      providerId: this.providerId,
      name: formValue.name || '',
      description: formValue.description || '',
      tags: formValue.tags ? formValue.tags.split(',').map((t: string) => t.trim()) : [],
      price: Number(formValue.price) || 0,
      experienceYears: Number(formValue.experienceYears) || 0,
      phone: formValue.phone || '',
      email: formValue.email || '',
      imageUrl: formValue.imageUrl || '',
      specialization: formValue.specialization || '',
      status: formValue.status || 'active'
    };
    
    console.log('[ResourcesComponent] Creating resource:', resource);
    this.resourceService.createResource(resource).subscribe({
      next: (created: Resource) => {
        console.log('[ResourcesComponent] Resource created successfully:', created);
        this.resources.push(created);
        this.editingResource = null;
        this.showForm = false;
        this.successMessage = 'Resource created successfully!';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        console.error('[ResourcesComponent] Failed to create resource:', err);
        this.errorMessage = 'Failed to create resource';
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }
}