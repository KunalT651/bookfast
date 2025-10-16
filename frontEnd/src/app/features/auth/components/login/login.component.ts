import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { ProviderService } from '../../../provider/services/provider.service'; // <-- Import this
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  error = '';
  success = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private providerService: ProviderService, // <-- Inject here
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) return;

    this.authService.login(this.loginForm.value).subscribe({
      next: (res: any) => {
        this.success = 'Login successful!';
        this.error = '';
        localStorage.setItem('token', res.token);
        localStorage.setItem('user', JSON.stringify(res.user));

if (res.user.role.name === 'PROVIDER') {
  this.providerService.getProviderProfileByUserId(res.user.id).subscribe(profile => {
    localStorage.setItem('providerProfile', JSON.stringify(profile));
    this.router.navigate(['/provider/dashboard']);
  });
} else if (res.user.role.name === 'ADMIN') {
          this.router.navigate(['/admin/dashboard']);
        } else if (res.user.role.name === 'CUSTOMER') {
          this.router.navigate(['/']);
        } else {
          this.router.navigate(['/']);
        }
      },
      error: (err: any) => {
        this.error = err.error?.message || 'Login failed';
        this.success = '';
      }
    });
  }
}