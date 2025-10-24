import { Component } from '@angular/core';
import { ResourceService } from '../../services/resource.service';
import { Resource } from '../../models/resource.model';

import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-provider-filter',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './provider-filter.component.html',
  styleUrls: ['./provider-filter.component.css']
})
export class ProviderFilterComponent {
  specialization: string = '';
  status: string = 'active';
  resources: Resource[] = [];

  constructor(private resourceService: ResourceService) {}

  filterProviders() {
    this.resourceService.filterResources(this.specialization, this.status).subscribe(resources => {
      this.resources = resources;
    });
  }
}
