import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ResourceAvailabilityComponent } from '../resource-availability/resource-availability.component';
import { ResourceAvailability } from '../../models/resource-availability.model';

@Component({
  selector: 'app-resource-availability-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, ResourceAvailabilityComponent],
  templateUrl: './resource-availability-modal.component.html',
  styleUrls: ['./resource-availability-modal.component.css']
})
export class ResourceAvailabilityModalComponent {
  @Input() resourceId!: number;
  @Output() close = new EventEmitter<void>();

  onClose() {
    this.close.emit();
  }

  // Add slot form state
  slotDate: string = '';
  slotStart: string = '';
  slotEnd: string = '';
  get slotDay(): string {
    if (!this.slotDate) return '';
    const date = new Date(this.slotDate);
    return date.toLocaleDateString('en-US', { weekday: 'long' }).toUpperCase();
  }
  addSlot() {
    // TODO: Implement actual add logic
    alert(`Slot added: ${this.slotDate} (${this.slotDay}) ${this.slotStart}-${this.slotEnd}`);
    this.slotDate = '';
    this.slotStart = '';
    this.slotEnd = '';
  }
}
