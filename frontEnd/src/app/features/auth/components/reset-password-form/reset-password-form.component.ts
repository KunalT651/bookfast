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
  loading = false;
  success = false;
  error: string | null = null;
  token: string | null = null;
  verifying = true;
  tokenValid = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.resetForm = this.fb.group({
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  ngOnInit() {
    // Get token from query params
    this.token = this.route.snapshot.queryParamMap.get('token');
    
    if (!this.token) {
      this.error = 'Invalid reset link. Please request a new password reset.';
      this.verifying = false;
      return;
    }

    // Verify token
    this.authService.verifyResetToken(this.token).subscribe({
      next: () => {
        this.tokenValid = true;
        this.verifying = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'This reset link is invalid or has expired. Please request a new one.';
        this.verifying = false;
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
    if (this.resetForm.invalid || !this.token) {
      return;
    }

    this.loading = true;
    this.error = null;

    const { newPassword, confirmPassword } = this.resetForm.value;

    this.authService.resetPassword(this.token, newPassword, confirmPassword).subscribe({
      next: (response) => {
        this.success = true;
        this.loading = false;
        
        // Redirect to login after 3 seconds
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to reset password. Please try again.';
        this.loading = false;
      }
    });
  }

  requestNewLink() {
    this.router.navigate(['/password-reset']);
  }
}

