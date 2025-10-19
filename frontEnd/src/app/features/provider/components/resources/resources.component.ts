import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ResourceService } from '../../services/resource.service';
import { ProviderService } from '../../services/provider.service';
import { Resource } from '../../models/resource.model';

@Component({
  selector: 'app-provider-resources',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './resources.component.html',
  styleUrls: ['./resources.component.css']
})
export class ResourcesComponent implements OnInit {
  resources: Resource[] = [];
  showForm = false;
  resourceForm: FormGroup;
  providerId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private resourceService: ResourceService,
    private providerService: ProviderService
  ) {
    this.resourceForm = this.fb.group({
      name: [''],
      description: [''],
      tags: [''],
      price: [''],
      experienceYears: [''],
      phone: [''],
      email: [''],
      imageUrl: [''],
      specialization: [''],
      status: ['active']
    });
  }

  ngOnInit() {
    this.loadProviderProfile();
  }

  loadProviderProfile() {
    // Assumes backend provides /api/provider/profile/me for current provider
    this.providerService.getProviderProfileForCurrentUser().subscribe(profile => {
      this.providerId = profile.id;
      this.loadResources();
    });
  }

  loadResources() {
    if (this.providerId === null) return;
    this.resourceService.getResources(this.providerId).subscribe(res => {
      this.resources = res;
    });
  }

  openForm() {
    this.showForm = true;
    this.resourceForm.reset();
  }

  closeForm() {
    this.showForm = false;
  }

  addResource() {
    if (this.providerId === null) {
      alert('Provider ID not found. Please log in again.');
      return;
    }
    const formValue = this.resourceForm.value;
    const resource: Resource = {
      providerId: this.providerId,
      name: formValue.name || '',
      description: formValue.description || '',
      tags: formValue.tags ? formValue.tags.split(',').map((t: string) => t.trim()) : [],
      price: Number(formValue.price) || 0,
      experienceYears: Number(formValue.experienceYears) || 0,
      phone: formValue.phone || '',
      email: formValue.email || '',
      imageUrl: formValue.imageUrl || '',
      specialization: formValue.specialization || '',
      status: formValue.status || 'active'
    };
    this.resourceService.createResource(resource).subscribe({
      next: (created) => {
        this.resources.push(created);
        this.closeForm();
      },
      error: () => alert('Failed to create resource')
    });
  }
}