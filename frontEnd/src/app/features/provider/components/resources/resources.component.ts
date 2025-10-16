import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ResourceService } from '../../services/resource.service';
import { AuthService } from '../../../auth/services/auth.service';
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

  constructor(
    private fb: FormBuilder,
    private resourceService: ResourceService,
    private authService: AuthService
  ) {
    this.resourceForm = this.fb.group({
      name: [''],
      address: [''],
      description: [''],
      tags: [''],
      price: [''],
      experienceYears: [''],
      phone: [''],
      email: [''],
      imageUrl: [''],
      specialization: [''],
      status: ['']
    });
  }

  ngOnInit() {
    this.loadResources();
  }

  loadResources() {
    const providerId = this.authService.getProviderId();
    if (providerId === null) return;
    this.resourceService.getResources(providerId).subscribe(res => {
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
    const providerId = this.authService.getProviderId();
    if (providerId === null) {
      alert('Provider ID not found. Please log in again.');
      return;
    }
    const formValue = this.resourceForm.value;
    const resource: Resource = {
      providerId,
      name: formValue.name || '',
      address: formValue.address || '',
      description: formValue.description || '',
      tags: formValue.tags ? formValue.tags.split(',').map((t: string) => t.trim()) : [],
      price: Number(formValue.price) || 0,
      experienceYears: Number(formValue.experienceYears) || 0,
      phone: formValue.phone || '',
      email: formValue.email || '',
      imageUrl: formValue.imageUrl || '',
      availability: [],
      specialization: formValue.specialization || '',
      status: formValue.status || ''
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