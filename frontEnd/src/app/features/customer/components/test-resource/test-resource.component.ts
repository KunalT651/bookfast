import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-test-resource',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="test-resource-container">
      <h2>Test Resource System</h2>
      
      <div class="test-section">
        <h3>Test Resource Creation</h3>
        <button (click)="createTestResource()" [disabled]="loading">
          {{ loading ? 'Creating...' : 'Create Test Resource' }}
        </button>
      </div>

      <div class="test-section">
        <h3>Check Resources</h3>
        <button (click)="checkResources()" [disabled]="loading">
          {{ loading ? 'Checking...' : 'Check All Resources' }}
        </button>
      </div>

      <div class="test-section">
        <h3>Customer Resources</h3>
        <button (click)="checkCustomerResources()" [disabled]="loading">
          {{ loading ? 'Checking...' : 'Check Customer Resources' }}
        </button>
      </div>

      <div *ngIf="message" class="message" [class.success]="success" [class.error]="!success">
        {{ message }}
      </div>

      <div *ngIf="resourceData" class="resource-data">
        <h4>Resource Data:</h4>
        <pre>{{ resourceData | json }}</pre>
      </div>
    </div>
  `,
  styles: [`
    .test-resource-container {
      max-width: 800px;
      margin: 20px auto;
      padding: 20px;
      border: 1px solid #ddd;
      border-radius: 8px;
    }
    
    .test-section {
      margin: 20px 0;
      padding: 15px;
      border: 1px solid #eee;
      border-radius: 5px;
    }
    
    button {
      padding: 10px 15px;
      background-color: #007bff;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      margin: 5px;
    }
    
    button:disabled {
      background-color: #ccc;
      cursor: not-allowed;
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
    
    .resource-data {
      margin-top: 20px;
      padding: 15px;
      background-color: #f8f9fa;
      border-radius: 4px;
    }
    
    pre {
      white-space: pre-wrap;
      word-wrap: break-word;
    }
  `]
})
export class TestResourceComponent {
  loading = false;
  message = '';
  success = false;
  resourceData: any = null;

  constructor(private http: HttpClient) {}

  createTestResource() {
    this.loading = true;
    this.message = '';
    
    this.http.post(`${environment.apiUrl}/api/test-resource/create-test-resource', {}, { withCredentials: true })
      .subscribe({
        next: (response: any) => {
          this.loading = false;
          this.success = true;
          this.message = response.message || 'Test resource created successfully';
          this.resourceData = response;
        },
        error: (error: any) => {
          this.loading = false;
          this.success = false;
          this.message = error.error?.error || 'Failed to create test resource';
        }
      });
  }

  checkResources() {
    this.loading = true;
    this.message = '';
    
    this.http.get(`${environment.apiUrl}/api/test-resource/check-resources`, { withCredentials: true })
      .subscribe({
        next: (response: any) => {
          this.loading = false;
          this.success = true;
          this.message = `Found ${response.total_resources} total resources, ${response.active_resources} active`;
          this.resourceData = response;
        },
        error: (error: any) => {
          this.loading = false;
          this.success = false;
          this.message = error.error?.error || 'Failed to check resources';
        }
      });
  }

  checkCustomerResources() {
    this.loading = true;
    this.message = '';
    
    this.http.get(`${environment.apiUrl}/api/resources`, { withCredentials: true })
      .subscribe({
        next: (response: any) => {
          this.loading = false;
          this.success = true;
          this.message = `Customer endpoint returned ${response.length} resources`;
          this.resourceData = response;
        },
        error: (error: any) => {
          this.loading = false;
          this.success = false;
          this.message = error.error?.error || 'Failed to check customer resources';
        }
      });
  }
}
