import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-permanent-cleanup',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="permanent-cleanup-container">
      <h2>Permanent Cleanup - Service Resources</h2>
      
      <div class="warning-section">
        <h3>⚠️ Warning</h3>
        <p>This will permanently clean the service_resources table and prevent data from coming back.</p>
        <p><strong>This action cannot be undone!</strong></p>
      </div>

      <div class="status-section">
        <h3>Current Status</h3>
        <button (click)="checkStatus()" [disabled]="loading">
          {{ loading ? 'Checking...' : 'Check Current Status' }}
        </button>
        <div *ngIf="status" class="status-info">
          <p><strong>Service Resources Count:</strong> {{ status.service_resources_count }}</p>
          <p><strong>Resource Count:</strong> {{ status.resource_count }}</p>
          <p><strong>Cleanup Status:</strong> 
            <span [class.clean]="status.cleanup_status === 'CLEAN'" 
                  [class.needs-cleanup]="status.cleanup_status === 'NEEDS_CLEANUP'">
              {{ status.cleanup_status }}
            </span>
          </p>
        </div>
      </div>

      <div class="cleanup-section">
        <h3>Permanent Cleanup Actions</h3>
        
        <div class="button-group">
          <button (click)="permanentCleanup()" [disabled]="loading" class="danger">
            🗑️ Permanent Cleanup (Delete All Service Resources)
          </button>
          
          <button (click)="disableServiceResources()" [disabled]="loading" class="warning">
            🚫 Disable Service Resources Table
          </button>
        </div>
      </div>

      <div *ngIf="message" class="message" [class.success]="success" [class.error]="!success">
        {{ message }}
      </div>
    </div>
  `,
  styles: [`
    .permanent-cleanup-container {
      max-width: 800px;
      margin: 20px auto;
      padding: 20px;
      border: 1px solid #ddd;
      border-radius: 8px;
    }
    
    .warning-section {
      margin: 20px 0;
      padding: 15px;
      background-color: #fff3cd;
      border: 1px solid #ffeaa7;
      border-radius: 5px;
    }
    
    .warning-section h3 {
      color: #856404;
      margin-top: 0;
    }
    
    .status-section, .cleanup-section {
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
    
    .clean {
      color: #28a745;
      font-weight: bold;
    }
    
    .needs-cleanup {
      color: #dc3545;
      font-weight: bold;
    }
    
    .button-group {
      display: flex;
      flex-direction: column;
      gap: 10px;
    }
    
    button {
      padding: 12px 15px;
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
    
    .warning {
      background-color: #ffc107;
      color: #212529;
    }
    
    .danger:hover:not(:disabled) {
      background-color: #c82333;
    }
    
    .warning:hover:not(:disabled) {
      background-color: #e0a800;
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
export class PermanentCleanupComponent {
  loading = false;
  message = '';
  success = false;
  status: any = null;

  constructor(private http: HttpClient) {}

  checkStatus() {
    this.loading = true;
    this.http.get(`${environment.apiUrl}/api/cleanup/check-cleanup`, { withCredentials: true })
      .subscribe({
        next: (response: any) => {
          this.loading = false;
          this.status = response;
        },
        error: (error: any) => {
          this.loading = false;
          this.message = 'Failed to check cleanup status';
          this.success = false;
        }
      });
  }

  permanentCleanup() {
    if (!confirm('Are you sure you want to permanently delete all service resources? This cannot be undone!')) {
      return;
    }

    this.loading = true;
    this.message = '';
    
    this.http.post(`${environment.apiUrl}/api/cleanup/permanent-cleanup`, {}, { withCredentials: true })
      .subscribe({
        next: (response: any) => {
          this.loading = false;
          this.success = true;
          this.message = response.message || 'Permanent cleanup completed successfully';
          this.checkStatus();
        },
        error: (error: any) => {
          this.loading = false;
          this.success = false;
          this.message = error.error?.error || 'Failed to perform permanent cleanup';
        }
      });
  }

  disableServiceResources() {
    this.loading = true;
    this.message = '';
    
    this.http.post(`${environment.apiUrl}/api/cleanup/disable-service-resources`, {}, { withCredentials: true })
      .subscribe({
        next: (response: any) => {
          this.loading = false;
          this.success = true;
          this.message = response.message || 'Service resources table disabled successfully';
        },
        error: (error: any) => {
          this.loading = false;
          this.success = false;
          this.message = error.error?.error || 'Failed to disable service resources';
        }
      });
  }
}
