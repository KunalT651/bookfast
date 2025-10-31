import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-database-manager',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="database-manager-container">
      <h2>Database Management</h2>
      
      <div class="status-section">
        <h3>Current Status</h3>
        <button (click)="getStatus()" [disabled]="loading">Refresh Status</button>
        <div *ngIf="status" class="status-info">
          <p><strong>Service Resources:</strong> {{ status.service_resources_count }}</p>
          <p><strong>Customer Resources:</strong> {{ status.customer_resources_count }}</p>
          <p><strong>Total Resources:</strong> {{ status.total_resources }}</p>
        </div>
      </div>

      <div class="cleanup-section">
        <h3>Cleanup Operations</h3>
        
        <div class="button-group">
          <button (click)="cleanServiceResources()" [disabled]="loading" class="danger">
            Clean Service Resources Table
          </button>
          
          <button (click)="cleanCustomerResources()" [disabled]="loading" class="danger">
            Clean Customer Resources Table
          </button>
          
          <button (click)="cleanAllResources()" [disabled]="loading" class="danger">
            Clean ALL Resources
          </button>
        </div>
      </div>

      <div class="create-section">
        <h3>Create Fresh Data</h3>
        
        <div class="button-group">
          <button (click)="createFreshServiceResources()" [disabled]="loading" class="success">
            Prepare Service Resources (Clean Only)
          </button>
          
          <button (click)="createFreshCustomerResources()" [disabled]="loading" class="success">
            Create Fresh Customer Resources
          </button>
        </div>
      </div>

      <div *ngIf="message" class="message" [class.success]="success" [class.error]="!success">
        {{ message }}
      </div>
    </div>
  `,
  styles: [`
    .database-manager-container {
      max-width: 800px;
      margin: 20px auto;
      padding: 20px;
      border: 1px solid #ddd;
      border-radius: 8px;
    }
    
    .status-section, .cleanup-section, .create-section {
      margin: 20px 0;
      padding: 15px;
      border: 1px solid #eee;
      border-radius: 5px;
    }
    
    .status-info {
      background-color: #f8f9fa;
      padding: 10px;
      border-radius: 4px;
      margin-top: 10px;
    }
    
    .button-group {
      display: flex;
      flex-direction: column;
      gap: 10px;
    }
    
    button {
      padding: 10px 15px;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 14px;
    }
    
    button:disabled {
      background-color: #ccc;
      cursor: not-allowed;
    }
    
    .danger {
      background-color: #dc3545;
      color: white;
    }
    
    .success {
      background-color: #28a745;
      color: white;
    }
    
    .danger:hover:not(:disabled) {
      background-color: #c82333;
    }
    
    .success:hover:not(:disabled) {
      background-color: #218838;
    }
    
    .message {
      margin-top: 20px;
      padding: 15px;
      border-radius: 4px;
    }
    
    .success {
      background-color: #d4edda;
      color: #155724;
      border: 1px solid #c3e6cb;
    }
    
    .error {
      background-color: #f8d7da;
      color: #721c24;
      border: 1px solid #f5c6cb;
    }
  `]
})
export class DatabaseManagerComponent implements OnInit {
  loading = false;
  message = '';
  success = false;
  status: any = null;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.getStatus();
  }

  getStatus() {
    this.loading = true;
    this.http.get(`${environment.apiUrl}/api/database/status`, { withCredentials: true })
      .subscribe({
        next: (response: any) => {
          this.loading = false;
          this.status = response;
        },
        error: (error: any) => {
          this.loading = false;
          this.message = 'Failed to get database status';
          this.success = false;
        }
      });
  }

  cleanServiceResources() {
    this.loading = true;
    this.message = '';
    
    this.http.post(`${environment.apiUrl}/api/database/clean-service-resources', {}, { withCredentials: true })
      .subscribe({
        next: (response: any) => {
          this.loading = false;
          this.success = true;
          this.message = response.message || 'Service resources cleaned successfully';
          this.getStatus();
        },
        error: (error: any) => {
          this.loading = false;
          this.success = false;
          this.message = error.error?.error || 'Failed to clean service resources';
        }
      });
  }

  cleanCustomerResources() {
    this.loading = true;
    this.message = '';
    
    this.http.post(`${environment.apiUrl}/api/database/clean-customer-resources', {}, { withCredentials: true })
      .subscribe({
        next: (response: any) => {
          this.loading = false;
          this.success = true;
          this.message = response.message || 'Customer resources cleaned successfully';
          this.getStatus();
        },
        error: (error: any) => {
          this.loading = false;
          this.success = false;
          this.message = error.error?.error || 'Failed to clean customer resources';
        }
      });
  }

  cleanAllResources() {
    this.loading = true;
    this.message = '';
    
    this.http.post(`${environment.apiUrl}/api/database/clean-all-resources', {}, { withCredentials: true })
      .subscribe({
        next: (response: any) => {
          this.loading = false;
          this.success = true;
          this.message = response.message || 'All resources cleaned successfully';
          this.getStatus();
        },
        error: (error: any) => {
          this.loading = false;
          this.success = false;
          this.message = error.error?.error || 'Failed to clean all resources';
        }
      });
  }

  createFreshServiceResources() {
    this.loading = true;
    this.message = '';
    
    this.http.post(`${environment.apiUrl}/api/database/create-fresh-service-resources', {}, { withCredentials: true })
      .subscribe({
        next: (response: any) => {
          this.loading = false;
          this.success = true;
          this.message = response.message || 'Service resources table prepared for fresh data';
          this.getStatus();
        },
        error: (error: any) => {
          this.loading = false;
          this.success = false;
          this.message = error.error?.error || 'Failed to prepare service resources';
        }
      });
  }

  createFreshCustomerResources() {
    this.loading = true;
    this.message = '';
    
    this.http.post(`${environment.apiUrl}/api/database/create-fresh-customer-resources', {}, { withCredentials: true })
      .subscribe({
        next: (response: any) => {
          this.loading = false;
          this.success = true;
          this.message = response.message || 'Fresh customer resources created successfully';
          this.getStatus();
        },
        error: (error: any) => {
          this.loading = false;
          this.success = false;
          this.message = error.error?.error || 'Failed to create fresh customer resources';
        }
      });
  }
}
