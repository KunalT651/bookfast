import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserStateService } from '../../../../shared/services/user-state.service';

@Component({
  selector: 'app-admin-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-login.component.html',
  styleUrls: ['./admin-login.component.css']
})
export class AdminLoginComponent {
  email = '';
  password = '';
  errorMessage = '';
  isLoading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private userState: UserStateService
  ) {}

  onSubmit() {
    console.log('=== ONSUBMIT CALLED ===');
    if (!this.email || !this.password) {
      this.errorMessage = 'Please fill in all fields';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const loginData = {
      email: this.email,
      password: this.password
    };

    console.log('About to call authService.login');
    this.authService.login(loginData).subscribe({
      next: (response) => {
        console.log('=== NEXT CALLBACK EXECUTED ===');
        console.log('Admin login response received:', response);
        console.log('Response user:', response.user);
        console.log('Response user role:', response.user?.role);
        console.log('Response user role name:', response.user?.role?.name);
        
        // Check if the user has ADMIN role
        if (response.user?.role?.name === 'ADMIN') {
          console.log('Admin role confirmed, updating user state');
          // Update user state
          this.userState.setUser(response.user);
          this.userState.setLoggedIn(true);
          
          // Verify user state was set
          this.userState.getUser().subscribe(user => {
            console.log('User state after setting:', user);
          });
          this.userState.getLoggedIn().subscribe(loggedIn => {
            console.log('Logged in state after setting:', loggedIn);
          });
          
          // Redirect to admin dashboard
          this.router.navigate(['/admin/dashboard']);
        } else {
          console.log('Admin role check failed. Role:', response.user?.role?.name);
          this.errorMessage = 'Access denied. Admin privileges required.';
          this.authService.logout();
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Admin login error:', error);
        console.error('Error details:', error.error);
        console.error('Error status:', error.status);
        this.errorMessage = 'Invalid admin credentials. Please try again.';
        this.isLoading = false;
      }
    });
  }

  goToUserLogin() {
    this.router.navigate(['/login']);
  }
}
