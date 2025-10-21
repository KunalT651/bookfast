import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { UserStateService } from '@app/shared/services/user-state.service';
import { ProviderService } from '../../../provider/services/provider.service';
import { Router } from '@angular/router';
import { ServiceCategoryService } from '../../../admin/services/service-category.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  error = '';
  success = '';
  serviceCategories: any[] = [];


  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private providerService: ProviderService,
    private router: Router,
    private userState: UserStateService
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      serviceCategory: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.authService.getServiceCategories().subscribe({
      next: (cats) => this.serviceCategories = cats,
      error: () => this.serviceCategories = []
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) return;

    this.authService.login(this.loginForm.value).subscribe({
      next: (res: any) => {
        this.success = 'Login successful!';
        this.error = '';
        // Set user state for navbar
        this.userState.setUser(res.user);

        if (res.user.role.name === 'PROVIDER') {
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