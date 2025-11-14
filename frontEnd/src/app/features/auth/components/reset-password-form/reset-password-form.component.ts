import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-reset-password-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reset-password-form.component.html',
  styleUrls: ['./reset-password-form.component.css']
})
export class ResetPasswordFormComponent implements OnInit {
  resetForm: FormGroup;
  token: string = '';
  loading = false;
  success = false;
  error: string | null = null;
  validatingToken = true;
  tokenValid = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.resetForm = this.fb.group({
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit() {
    // Get token from URL query params
    this.route.queryParams.subscribe(params => {
      this.token = params['token'] || '';
      if (this.token) {
        this.validateToken();
      } else {
        this.validatingToken = false;
        this.error = 'Invalid reset link';
      }
    });
  }

  validateToken() {
    this.authService.validateResetToken(this.token).subscribe({
      next: (response: any) => {
        this.tokenValid = response.valid;
        this.validatingToken = false;
        if (!this.tokenValid) {
          this.error = 'This reset link is invalid or has expired';
        }
      },
      error: () => {
        this.tokenValid = false;
        this.validatingToken = false;
        this.error = 'This reset link is invalid or has expired';
      }
    });
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('newPassword');
    const confirmPassword = form.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  resetPassword() {
    if (this.resetForm.valid && this.token) {
      this.loading = true;
      this.error = null;

      const newPassword = this.resetForm.value.newPassword;

      this.authService.resetPassword(this.token, newPassword).subscribe({
        next: () => {
          this.success = true;
          this.loading = false;
          // Redirect to login after 3 seconds
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to reset password';
          this.loading = false;
        }
      });
    }
  }

  get newPassword() {
    return this.resetForm.get('newPassword');
  }

  get confirmPassword() {
    return this.resetForm.get('confirmPassword');
  }
}

