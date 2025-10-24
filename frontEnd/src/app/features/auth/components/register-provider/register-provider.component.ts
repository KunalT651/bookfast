import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register-provider',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './register-provider.component.html',
  styleUrls: ['./register-provider.component.css']
})
export class RegisterProviderComponent implements OnInit {
  registerForm: FormGroup;
  serviceCategories: any[] = [];
  error = '';
  success = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
  ) {
    this.registerForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
      serviceCategory: ['', Validators.required],
      organizationName: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit() {
    this.authService.getServiceCategories().subscribe({
      next: (cats) => this.serviceCategories = cats,
      error: () => this.serviceCategories = []
    });
  }

  passwordMatchValidator(form: FormGroup) {
    return form.get('password')!.value === form.get('confirmPassword')!.value
      ? null : { mismatch: true };
  }

onSubmit() {
  this.success = '';
  this.error = '';
  if (this.registerForm.invalid) return;

  // Add role: 'PROVIDER' to the payload
  const payload = {
    ...this.registerForm.value,
    role: 'PROVIDER'
  };

  this.authService.registerProvider(payload).subscribe({
    next: () => {
      this.success = 'Registration successful!';
      this.registerForm.reset();
    },
    error: (err: any) => {
      this.error = err.error?.message || 'Registration failed';
    }
  });
}
}