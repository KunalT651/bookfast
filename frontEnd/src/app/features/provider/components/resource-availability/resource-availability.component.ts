import { Component, Input, OnInit, OnChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ResourceAvailabilityService } from '../../services/resource-availability.service';
import { ResourceAvailability } from '../../models/resource-availability.model';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-resource-availability',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './resource-availability.component.html',
  //styleUrls: ['./resource-availability.component.css']
})
export class ResourceAvailabilityComponent implements OnInit, OnChanges {
  @Input() resourceId?: number;
  availabilities: ResourceAvailability[] = [];
  selectedAvailability?: ResourceAvailability;
  form: Partial<ResourceAvailability> = {};
  slotDate: string = '';
  onDateChange(dateStr: string) {
    this.slotDate = dateStr;
    this.form.date = dateStr;
    if (dateStr) {
      const date = new Date(dateStr);
      const day = date.toLocaleDateString('en-US', { weekday: 'long' }).toUpperCase();
      this.form.dayOfWeek = day;
    } else {
      this.form.dayOfWeek = '';
      this.form.date = '';
    }
  }
  daysOfWeek = [
    'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'
  ];
  error = '';
  success = '';

  constructor(private availabilityService: ResourceAvailabilityService) {}

  ngOnInit() {
    if (this.resourceId) {
      this.loadAvailabilities();
    }
  }

  ngOnChanges() {
    if (this.resourceId) {
      this.loadAvailabilities();
    }
  }

  loadAvailabilities() {
    if (!this.resourceId) return;
    this.availabilityService.getAvailabilitiesByResource(this.resourceId).subscribe({
      next: (data) => this.availabilities = data,
      error: () => this.availabilities = []
    });
  }

  selectAvailability(avail: ResourceAvailability) {
    this.selectedAvailability = avail;
    this.form = { ...avail };
  }

  clearForm() {
    this.selectedAvailability = undefined;
    this.form = {};
  }

  submit() {
    this.error = '';
    this.success = '';
    if (!this.form.date || !this.form.dayOfWeek || !this.form.startTime || !this.form.endTime) {
      this.error = 'All fields are required';
      return;
    }
    if (!this.resourceId) {
      this.error = 'No resource selected';
      return;
    }
    const payload: ResourceAvailability = {
      ...this.form,
      resourceId: this.resourceId
    } as ResourceAvailability;

    if (this.selectedAvailability && this.selectedAvailability.id) {
      this.availabilityService.updateAvailability(this.selectedAvailability.id, payload).subscribe({
        next: () => {
          this.success = 'Slot updated!';
          this.clearForm();
          this.loadAvailabilities();
        },
        error: () => this.error = 'Update failed'
      });
    } else {
      this.availabilityService.createAvailability(payload).subscribe({
        next: () => {
          this.success = 'Slot added!';
          this.clearForm();
          this.loadAvailabilities();
        },
        error: () => this.error = 'Add failed'
      });
    }
  }

  deleteAvailability(id: number) {
    this.availabilityService.deleteAvailability(id).subscribe({
      next: () => {
        this.success = 'Deleted!';
        this.clearForm();
        this.loadAvailabilities();
      },
      error: () => this.error = 'Delete failed'
    });
  }
}