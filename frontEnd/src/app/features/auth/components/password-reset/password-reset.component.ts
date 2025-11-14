import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-password-reset',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './password-reset.component.html',
  styleUrls: ['./password-reset.component.css']
})
export class PasswordResetComponent {
  resetForm: FormGroup;
  loading = false;
  success = false;
  error: string | null = null;

  constructor(private fb: FormBuilder, private authService: AuthService) {
    this.resetForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  requestReset() {
    if (this.resetForm.valid) {
      this.loading = true;
      this.authService.requestPasswordReset(this.resetForm.value.email).subscribe({
        next: () => {
          this.success = true;
          this.loading = false;
        },
        error: err => {
          this.error = 'Failed to send reset email.';
          this.loading = false;
        }
      });
    }
  }
}
