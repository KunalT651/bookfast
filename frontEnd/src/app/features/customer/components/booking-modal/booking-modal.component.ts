import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Resource } from '../../models/resource.model';
import { ResourceSlotListComponent } from '../resource-slot-list/resource-slot-list.component';
import { AvailabilitySlot } from '../../models/availability-slot.model';

@Component({
  selector: 'app-booking-modal',
  standalone: true,
  imports: [CommonModule, ResourceSlotListComponent],
  templateUrl: 'booking-modal.component.html',
  styleUrls: ['booking-modal.component.css']
})
export class BookingModalComponent {
  @Input() resource!: Resource;
  @Input() open: boolean = false;
  @Output() close = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<{ resource: Resource, slots: AvailabilitySlot[] }>();

  selectedSlots: AvailabilitySlot[] = [];

  onClose() {
    this.close.emit();
  }

  onSelectSlot(slots: AvailabilitySlot[]) {
    this.selectedSlots = slots;
  }

  onConfirm() {
    if (this.selectedSlots && this.selectedSlots.length > 0) {
      this.confirm.emit({ resource: this.resource, slots: this.selectedSlots });
    }
  }
}
