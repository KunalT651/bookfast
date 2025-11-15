import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserStateService } from '@app/shared/services/user-state.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  error = '';
  success = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private userState: UserStateService
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  ngOnInit() {
    // Component initialization - no service categories needed for login
  }

  onSubmit() {
    if (this.loginForm.invalid) return;

    // Only send email and password to backend (backend doesn't expect serviceCategory)
    const loginData = {
      email: this.loginForm.value.email,
      password: this.loginForm.value.password
    };

    this.authService.login(loginData).subscribe({
      next: (res: any) => {
        this.success = 'Login successful!';
        this.error = '';
        // Set user state for navbar
        this.userState.setUser(res.user);

        if (res.user.role.name === 'PROVIDER') {
          // Redirect to provider dashboard (homepage with grid)
          this.router.navigate(['/provider/dashboard']);
        } else if (res.user.role.name === 'ADMIN') {
          this.router.navigate(['/admin/dashboard']);
        } else if (res.user.role.name === 'CUSTOMER') {
          this.router.navigate(['/customer/home']);
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