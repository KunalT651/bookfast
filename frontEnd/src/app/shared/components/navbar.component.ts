import { Component } from '@angular/core';
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
      <a routerLink="/customer/home" routerLinkActive="active" class="nav-btn">Home</a>
      @if (loggedIn | async) {
        <a routerLink="/customer/bookings" routerLinkActive="active" class="nav-btn">My Bookings</a>
        <a routerLink="/customer/reviews" routerLinkActive="active" class="nav-btn">My Reviews</a>
        <a routerLink="/customer/profile/edit" routerLinkActive="active" class="nav-btn">Edit Profile</a>
        <button (click)="logout()" class="nav-btn logout">Logout</button>
      }
    </nav>
  `,
  styleUrls: ['navbar.component.css']
})
export class NavbarComponent {
  loggedIn;

  constructor(
    private authService: AuthService,
    private userState: UserStateService,
    private router: Router
  ) {
    this.loggedIn = this.userState.getLoggedIn();
  }

  logout() {
    this.authService.logout().subscribe(() => {
      this.userState.clear();
      this.router.navigate(['/login']);
    });
  }
}
