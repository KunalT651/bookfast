import { Component, OnInit } from '@angular/core';
import { ServiceResourceService } from '../../services/service-resource.service';
import { ServiceResource } from '../../models/service-resource.model';
import { CommonModule } from '@angular/common';
import { ResourceAvailabilityComponent } from '../resource-availability/resource-availability.component';

@Component({
  selector: 'app-provider-resources',
  standalone: true,
  imports: [CommonModule, ResourceAvailabilityComponent],
  templateUrl: './resources.component.html',
  //styleUrls: ['./resources.component.css']
})
export class ResourcesComponent implements OnInit {
  resources: ServiceResource[] = [];
  selectedResource?: ServiceResource;
  providerId = 1; // Replace with actual logged-in provider ID

  constructor(private resourceService: ServiceResourceService) {}

  ngOnInit() {
    this.loadResources();
  }

  loadResources() {
    this.resourceService.getResourcesByProvider(this.providerId).subscribe(data => {
      this.resources = data;
    });
  }

  selectResource(resource: ServiceResource) {
    this.selectedResource = resource;
  }
}