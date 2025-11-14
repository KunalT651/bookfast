import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-test-data',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="test-data-container">
      <h2>Test Data Setup</h2>
      <p>Click the button below to create sample resources for testing the customer home page.</p>
      
      <button (click)="createSampleData()" [disabled]="loading">
        {{ loading ? 'Creating...' : 'Create Sample Resources' }}
      </button>
      
      <div *ngIf="message" class="message" [class.success]="success" [class.error]="!success">
        {{ message }}
      </div>
      
      <div *ngIf="success" class="next-steps">
        <h3>Next Steps:</h3>
        <ol>
          <li>Go to <a href="/customer/home">Customer Home</a> to see the resources</li>
          <li>Or refresh the current page to see the resources</li>
        </ol>
      </div>
    </div>
  `,
  styles: [`
    .test-data-container {
      max-width: 600px;
      margin: 50px auto;
      padding: 20px;
      border: 1px solid #ddd;
      border-radius: 8px;
    }
    
    button {
      width: 100%;
      padding: 12px;
      background-color: #28a745;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 16px;
      margin: 20px 0;
    }
    
    button:disabled {
      background-color: #ccc;
      cursor: not-allowed;
    }
    
    .message {
      margin-top: 15px;
      padding: 10px;
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
    
    .next-steps {
      margin-top: 20px;
      padding: 15px;
      background-color: #e7f3ff;
      border: 1px solid #b3d9ff;
      border-radius: 4px;
    }
    
    .next-steps h3 {
      margin-top: 0;
      color: #0066cc;
    }
    
    .next-steps a {
      color: #0066cc;
      text-decoration: none;
    }
    
    .next-steps a:hover {
      text-decoration: underline;
    }
  `]
})
export class TestDataComponent {
  loading = false;
  message = '';
  success = false;

  constructor(private http: HttpClient) {}

  createSampleData() {
    this.loading = true;
    this.message = '';

    this.http.post('http://localhost:8080/api/test/create-sample-resources', {}, { withCredentials: true })
      .subscribe({
        next: (response: any) => {
          this.loading = false;
          this.success = true;
          this.message = response.message || 'Sample resources created successfully!';
        },
        error: (error: any) => {
          this.loading = false;
          this.success = false;
          this.message = error.error?.error || 'Failed to create sample resources';
        }
      });
  }
}
