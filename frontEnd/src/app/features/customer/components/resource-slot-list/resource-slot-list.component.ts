import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AvailabilitySlot } from '../../models/availability-slot.model';
import { AvailabilitySlotService } from '../../services/availability-slot.service';

@Component({
  selector: 'app-resource-slot-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './resource-slot-list.component.html',
  styleUrls: ['./resource-slot-list.component.css']
})
export class ResourceSlotListComponent implements OnInit {
  selectedSlotIds: number[] = [];
  @Input() resourceId!: number;
  slots: AvailabilitySlot[] = [];
  loading = false;
  error = '';

  @Input() showBookButton: boolean = true;
  @Input() onBookSlot: (slots: AvailabilitySlot[]) => void = () => {};

  constructor(private slotService: AvailabilitySlotService) {}

  ngOnInit() {
    if (this.resourceId) {
      this.fetchSlots();
    }
  }

  fetchSlots() {
    this.loading = true;
    this.slotService.getSlotsForResource(this.resourceId).subscribe({
      next: (slots) => {
        this.slots = slots;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load slots';
        this.loading = false;
      }
    });
  }

  toggleSlotSelection(slot: AvailabilitySlot, checked: boolean) {
    if (checked) {
      if (!this.selectedSlotIds.includes(slot.id!)) {
        this.selectedSlotIds.push(slot.id!);
      }
    } else {
      this.selectedSlotIds = this.selectedSlotIds.filter(id => id !== slot.id);
    }
    // Always emit selected slots to parent
    const selectedSlots = this.slots.filter(s => this.selectedSlotIds.includes(s.id!));
    if (this.onBookSlot) {
      this.onBookSlot(selectedSlots);
    }
  }

  bookSelectedSlots() {
    const selectedSlots = this.slots.filter(slot => this.selectedSlotIds.includes(slot.id!));
    if (this.onBookSlot) {
      this.onBookSlot(selectedSlots);
    }
  }

  formatSlotDisplay(slot: AvailabilitySlot): string {
    if (!slot.date) {
      return `${slot.startTime} - ${slot.endTime}`;
    }
    
    const date = new Date(slot.date);
    const dayOfWeek = date.toLocaleDateString('en-US', { weekday: 'long' });
    const formattedDate = date.toLocaleDateString('en-US', { 
      month: 'short', 
      day: 'numeric', 
      year: 'numeric' 
    });
    
    return `${dayOfWeek}, ${formattedDate}: ${slot.startTime} - ${slot.endTime}`;
  }
}
