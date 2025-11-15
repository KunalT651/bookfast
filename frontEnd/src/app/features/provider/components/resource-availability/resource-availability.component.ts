import { Component, Input, OnInit, OnChanges, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AvailabilitySlotService } from '../../services/availability-slot.service';
import { AvailabilitySlot } from '../../models/availability-slot.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-resource-availability',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './resource-availability.component.html',
  styleUrls: []
})
export class ResourceAvailabilityComponent implements OnInit, OnChanges, OnDestroy {
  @Input() resourceId?: number;
  availabilitySlots: AvailabilitySlot[] = [];
  selectedAvailabilitySlot: AvailabilitySlot | null = null;
  
  slotForm: FormGroup; 
  error = '';
  success = '';
  private statusSubscription?: Subscription;

  constructor(
    private availabilitySlotService: AvailabilitySlotService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {
    this.slotForm = this.fb.group({
      date: ['', Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required],
      status: ['available', Validators.required],
      reason: ['']
    });
    
    // Clear reason when status changes away from unavailable and trigger change detection
    this.statusSubscription = this.slotForm.get('status')?.valueChanges.subscribe(status => {
      if (status !== 'unavailable') {
        this.slotForm.patchValue({ reason: '' }, { emitEvent: false });
      }
      this.cdr.markForCheck(); // Trigger change detection for *ngIf
    });
  }

  ngOnInit() {
    if (this.resourceId) {
      this.loadAvailabilitySlots();
    }
  }

  ngOnChanges() {
    if (this.resourceId) {
      this.loadAvailabilitySlots();
    }
  }

  loadAvailabilitySlots() {
    if (!this.resourceId) return;
    this.availabilitySlotService.getAvailabilitySlotsForResource(this.resourceId).subscribe({
      next: (data) => this.availabilitySlots = data,
      error: (err) => {
        this.error = 'Failed to load slots';
        console.error('Error loading availability slots', err);
      }
    });
  }

  getSlotDay(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { weekday: 'long' }).toUpperCase();
  }

  get isUnavailableStatus(): boolean {
    return this.slotForm.get('status')?.value === 'unavailable';
  }


  selectAvailabilitySlot(slot: AvailabilitySlot) {
    this.selectedAvailabilitySlot = slot;
    this.slotForm.patchValue({
      date: slot.date,
      startTime: slot.startTime,
      endTime: slot.endTime,
      status: slot.status,
      reason: slot.reason || ''
    });
  }

  clearForm() {
    this.selectedAvailabilitySlot = null;
    this.slotForm.reset({ status: 'available', reason: '' });
    this.error = '';
    this.success = '';
  }

  submitSlot() {
    this.error = '';
    this.success = '';
    if (this.slotForm.invalid || !this.resourceId) {
      this.error = 'Please fill all required fields and ensure a resource is selected.';
      return;
    }

    const formValue = this.slotForm.value;

    if (this.selectedAvailabilitySlot && this.selectedAvailabilitySlot.id) {
      // Update existing slot
      const reason = formValue.status === 'unavailable' ? (formValue.reason || '') : undefined;
      this.availabilitySlotService.updateAvailabilitySlot(
        this.resourceId,
        this.selectedAvailabilitySlot.id,
        formValue.date,
        formValue.startTime,
        formValue.endTime,
        formValue.status,
        reason
      ).subscribe({
        next: () => {
          this.success = 'Slot updated successfully!';
          this.loadAvailabilitySlots();
          this.clearForm();
        },
        error: (err) => {
          this.error = 'Update failed';
          console.error('Error updating availability slot', err);
        }
      });
    } else {
      // Add new slot
      const reason = formValue.status === 'unavailable' ? (formValue.reason || '') : undefined;
      this.availabilitySlotService.createAvailabilitySlot(
        this.resourceId,
        formValue.date,
        formValue.startTime,
        formValue.endTime,
        formValue.status,
        reason
      ).subscribe({
        next: () => {
          this.success = 'Slot added successfully!';
          this.loadAvailabilitySlots();
          this.clearForm();
        },
        error: (err) => {
          this.error = 'Add failed';
          console.error('Error adding availability slot', err);
        }
      });
    }
  }

  deleteSlot(id: number) {
    if (!confirm('Are you sure you want to delete this availability slot?')) {
      return;
    }
    if (!this.resourceId) {
      this.error = 'Resource ID is missing. Cannot delete slot.';
      return;
    }

    this.availabilitySlotService.deleteAvailabilitySlot(this.resourceId, id).subscribe({
      next: () => {
        this.success = 'Slot deleted successfully!';
        this.loadAvailabilitySlots();
        this.clearForm();
      },
      error: (err) => {
        this.error = 'Delete failed';
        console.error('Error deleting availability slot', err);
      }
    });
  }

  ngOnDestroy() {
    if (this.statusSubscription) {
      this.statusSubscription.unsubscribe();
    }
  }
}