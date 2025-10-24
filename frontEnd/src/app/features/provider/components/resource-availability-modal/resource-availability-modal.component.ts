import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ResourceAvailabilityComponent } from '../resource-availability/resource-availability.component';

@Component({
  selector: 'app-resource-availability-modal',
  standalone: true,
  imports: [CommonModule, ResourceAvailabilityComponent],
  templateUrl: './resource-availability-modal.component.html',
  styleUrls: ['./resource-availability-modal.component.css']
})
export class ResourceAvailabilityModalComponent {
  @Input() resourceId!: number;
  @Output() close = new EventEmitter<void>();

  onClose() {
    this.close.emit();
  }
}
