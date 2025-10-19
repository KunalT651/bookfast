import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common'; // <-- Import this
import { Resource } from '../../models/resource.model';

@Component({
  selector: 'app-resource-card',
  standalone: true,
  imports: [CommonModule], // <-- Add here
  templateUrl: './resource-card.component.html',
  styleUrls: ['./resource-card.component.css']
})
export class ResourceCardComponent {
  @Input() resource!: Resource;

  // Emit booking event to parent
  onBook() {
    // This should trigger navigation or booking modal in parent
    const event = new CustomEvent('book', { detail: this.resource });
    window.dispatchEvent(event);
  }
}