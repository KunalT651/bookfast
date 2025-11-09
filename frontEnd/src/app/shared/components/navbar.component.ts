import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService } from '../../features/auth/services/auth.service';
import { UserStateService } from '../../shared/services/user-state.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="navbar">
      <div class="navbar-brand">
        <img src="BookFast_logo.jpg" alt="BookFast" class="logo">
        <span class="brand-text">BookFast</span>
      </div>
      <div class="navbar-nav">
             @if (userRole === 'CUSTOMER') {
             <a routerLink="/customer/home" routerLinkActive="active" class="nav-btn">Home</a>
             <a routerLink="/customer/bookings" routerLinkActive="active" class="nav-btn">My Bookings</a>
             <a routerLink="/customer/reviews" routerLinkActive="active" class="nav-btn">My Reviews</a>
             <a routerLink="/customer/calendar-sync" routerLinkActive="active" class="nav-btn">📅 Calendar</a>
             <a routerLink="/customer/profile/edit" routerLinkActive="active" class="nav-btn">Edit Profile</a>
           } @else if (userRole === 'ADMIN') {
        <a routerLink="/admin/dashboard" routerLinkActive="active" class="nav-btn">Home</a>
        <a routerLink="/admin/services" routerLinkActive="active" class="nav-btn">Manage Categories</a>
        <a routerLink="/admin/providers" routerLinkActive="active" class="nav-btn">Manage Providers</a>
        <a routerLink="/admin/database" routerLinkActive="active" class="nav-btn">Database Manager</a>
        <a routerLink="/admin/cleanup" routerLinkActive="active" class="nav-btn">Permanent Cleanup</a>
      }
        @if (loggedIn | async) {
          <button (click)="logout()" class="nav-btn logout">Logout</button>
        }
      </div>
    </nav>
  `,
  styleUrls: ['navbar.component.css']
})
export class NavbarComponent implements OnInit {
  loggedIn;
  userRole: string | null = null;

  constructor(
    private authService: AuthService,
    private userState: UserStateService,
    private router: Router
  ) {
    this.loggedIn = this.userState.getLoggedIn();
  }

  ngOnInit() {
    this.userState.getUser().subscribe(user => {
      this.userRole = user ? user.role.name : null;
    });
  }

  logout() {
    this.authService.logout().subscribe(() => {
      this.userState.clear();
      this.router.navigate(['/login']);
    });
  }
}
