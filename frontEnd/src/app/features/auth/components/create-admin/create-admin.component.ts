import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-create-admin',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  template: `
    <div class="create-admin-container">
      <h2>Create Admin User</h2>
      <form [formGroup]="adminForm" (ngSubmit)="onSubmit()">
        <div class="form-group">
          <label for="firstName">First Name:</label>
          <input type="text" id="firstName" formControlName="firstName" required>
        </div>
        
        <div class="form-group">
          <label for="lastName">Last Name:</label>
          <input type="text" id="lastName" formControlName="lastName" required>
        </div>
        
        <div class="form-group">
          <label for="email">Email:</label>
          <input type="email" id="email" formControlName="email" required>
        </div>
        
        <div class="form-group">
          <label for="password">Password:</label>
          <input type="password" id="password" formControlName="password" required>
        </div>
        
        <button type="submit" [disabled]="adminForm.invalid || loading">
          {{ loading ? 'Creating...' : 'Create Admin' }}
        </button>
      </form>
      
      <div *ngIf="message" class="message" [class.success]="success" [class.error]="!success">
        {{ message }}
      </div>
    </div>
  `,
  styles: [`
    .create-admin-container {
      max-width: 400px;
      margin: 50px auto;
      padding: 20px;
      border: 1px solid #ddd;
      border-radius: 8px;
    }
    
    .form-group {
      margin-bottom: 15px;
    }
    
    label {
      display: block;
      margin-bottom: 5px;
      font-weight: bold;
    }
    
    input {
      width: 100%;
      padding: 8px;
      border: 1px solid #ccc;
      border-radius: 4px;
    }
    
    button {
      width: 100%;
      padding: 10px;
      background-color: #007bff;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
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
  `]
})
export class CreateAdminComponent {
  adminForm: FormGroup;
  loading = false;
  message = '';
  success = false;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient
  ) {
    this.adminForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit() {
    if (this.adminForm.invalid) return;

    this.loading = true;
    this.message = '';

    const adminData = this.adminForm.value;
    
    this.http.post(`${environment.apiUrl}/admin/create-admin`, adminData, { withCredentials: true })
      .subscribe({
        next: (response: any) => {
          this.loading = false;
          this.success = true;
          this.message = response.message || 'Admin user created successfully!';
          this.adminForm.reset();
        },
        error: (error: any) => {
          this.loading = false;
          this.success = false;
          this.message = error.error?.error || 'Failed to create admin user';
        }
      });
  }
}
