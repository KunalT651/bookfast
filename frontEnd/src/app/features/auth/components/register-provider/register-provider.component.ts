import { Component } from '@angular/core';
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
export class RegisterProviderComponent {
  registerForm: FormGroup;
  submitted = false;
  error = '';
  serviceCategories: string[] = [];

  ngOnInit() {
    this.authService.getServiceCategories().subscribe({
      next: (categories) => this.serviceCategories = categories,
      error: () => this.serviceCategories = []
    });
  }
  constructor(private fb: FormBuilder, private authService: AuthService) {
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

  passwordMatchValidator(form: FormGroup) {
    return form.get('password')!.value === form.get('confirmPassword')!.value
      ? null : { mismatch: true };
  }

  onSubmit() {
    this.submitted = true;
    if (this.registerForm.invalid) return;

    this.authService.registerProvider(this.registerForm.value).subscribe({
      next: () => {
        // Redirect to login or show success
      },
      error: (err: any) => {
        this.error = err.error?.message || 'Registration failed';
      }
    });
  }
}