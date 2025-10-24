import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ResourceAvailabilityModalComponent } from '../resource-availability-modal/resource-availability-modal.component';
import { ResourceService } from '../../services/resource.service';
import { ProviderService } from '../../services/provider.service';
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
  deleteResource(resource: Resource) {
    if (!resource.id) {
      alert('Resource ID not found.');
      return;
    }
    if (!confirm('Are you sure you want to delete this resource?')) return;
    this.resourceService.deleteResource(resource.id).subscribe({
      next: () => {
        this.resources = this.resources.filter(r => r.id !== resource.id);
      },
      error: () => alert('Failed to delete resource')
    });
  }
  showAvailabilityModal = false;
  selectedResource: Resource | null = null;
  resources: Resource[] = [];
  showForm = false;
  resourceForm: FormGroup;
  availabilityForm: FormGroup; // New form group for availability
  providerId: number | null = null;
  editingResource: Resource | null = null;
  availabilitySlots: AvailabilitySlot[] = []; // To store fetched availability slots

  constructor(
    private fb: FormBuilder,
    private resourceService: ResourceService,
    private providerService: ProviderService,
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
    this.availabilitySlotService.getAvailabilitySlotsForResource(resourceId).subscribe({
      next: (slots) => {
        this.availabilitySlots = slots;
      },
      error: (err) => {
        console.error('Failed to load availability slots', err);
        alert('Failed to load availability slots');
      }
    });
  }

  addAvailabilitySlot() {
    if (this.availabilityForm.invalid || this.selectedResource === null) {
      return;
    }

    const formValue = this.availabilityForm.value;
    const resourceId = this.selectedResource.id!;

    this.availabilitySlotService.createAvailabilitySlot(
      resourceId,
      formValue.date,
      formValue.startTime,
      formValue.endTime,
      formValue.status
    ).subscribe({
      next: (newSlot) => {
        this.availabilitySlots.push(newSlot);
        this.availabilityForm.reset({ status: 'available' }); // Reset form after successful addition
      },
      error: (err) => {
        console.error('Failed to add availability slot', err);
        alert('Failed to add availability slot');
      }
    });
  }

  deleteAvailabilitySlot(slotId: number) {
    if (!confirm('Are you sure you want to delete this availability slot?')) {
      return;
    }
    this.availabilitySlotService.deleteAvailabilitySlot(this.selectedResource!.id!, slotId).subscribe({
      next: () => {
        this.availabilitySlots = this.availabilitySlots.filter(s => s.id !== slotId);
      },
      error: (err) => {
        console.error('Failed to delete availability slot', err);
        alert('Failed to delete availability slot');
      }
    });
  }

  loadProviderProfile() {
    this.providerService.getProviderProfileForCurrentUser().subscribe((profile: any) => {
      this.providerId = profile.id;
      this.loadResources();
    });
  }

  loadResources() {
    if (this.providerId === null) return;
    this.resourceService.getResources(this.providerId).subscribe((res: Resource[]) => {
      this.resources = res;
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
      this.resourceService.updateResource(updated).subscribe({
        next: (res: Resource) => {
          this.resources = this.resources.map(r => r.id === res.id ? res : r);
          this.editingResource = null;
        },
        error: () => alert('Failed to update resource')
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
      alert('Provider ID not found. Please log in again.');
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
    this.resourceService.createResource(resource).subscribe({
      next: (created: Resource) => {
        this.resources.push(created);
        this.editingResource = null;
        this.showForm = false;
      },
      error: () => alert('Failed to create resource')
    });
  }
}