import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Resource } from '../../models/resource.model';

@Component({
  selector: 'app-booking-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: 'booking-modal.component.html',
  styleUrls: ['booking-modal.component.css']
})
export class BookingModalComponent {
  @Input() resource!: Resource;
  @Input() open: boolean = false;
  @Output() close = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<Resource>();

  onClose() {
    this.close.emit();
  }

  onConfirm() {
    this.confirm.emit(this.resource);
  }
}
