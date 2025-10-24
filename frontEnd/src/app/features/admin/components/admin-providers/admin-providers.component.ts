import { Component, OnInit } from '@angular/core';
import { AdminProviderService } from '../../services/admin-provider.service';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-providers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-providers.component.html',
  styleUrls: ['./admin-providers.component.css']
})
export class AdminProvidersComponent implements OnInit {
  providers: any[] = [];
  error: string = '';
  success: string = '';

  constructor(private adminProviderService: AdminProviderService) {}

  ngOnInit(): void {
    this.loadProviders();
  }

  loadProviders(): void {
    this.adminProviderService.getAllProviders().subscribe({
      next: (data) => {
        this.providers = data;
        this.error = '';
      },
      error: (err) => {
        this.error = 'Failed to load providers.';
      }
    });
  }

  updateProvider(provider: any): void {
    this.adminProviderService.updateProvider(provider.id, provider).subscribe({
      next: () => {
        this.success = 'Provider updated.';
        this.error = '';
      },
      error: () => {
        this.error = 'Failed to update provider.';
        this.success = '';
      }
    });
  }

  deleteProvider(id: number): void {
    this.adminProviderService.deleteProvider(id).subscribe({
      next: () => {
        this.success = 'Provider deleted.';
        this.error = '';
        this.providers = this.providers.filter(p => p.id !== id);
      },
      error: () => {
        this.error = 'Failed to delete provider.';
        this.success = '';
      }
    });
  }
}
