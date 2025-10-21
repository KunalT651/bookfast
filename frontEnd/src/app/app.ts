
import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from './shared/components/navbar.component';
import { AuthService } from './features/auth/services/auth.service';
import { UserStateService } from './shared/services/user-state.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class AppComponent implements OnInit {
  loggedIn;
  showNavbar = true;

  constructor(
    private authService: AuthService,
    private userState: UserStateService,
    private router: Router
  ) {
    this.loggedIn = this.userState.getLoggedIn();
    this.router.events.subscribe(() => {
      const hiddenRoutes = ['/login', '/register', '/register/provider'];
      this.showNavbar = !hiddenRoutes.includes(this.router.url.split('?')[0]);
    });
  }

  ngOnInit() {
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.userState.setUser(user);
      },
      error: () => {
        this.userState.clear();
        this.router.navigate(['/login']);
      }
    });
    // Optionally, subscribe to loggedIn and redirect if false
    this.loggedIn.subscribe(isLoggedIn => {
      if (!isLoggedIn) {
        this.router.navigate(['/login']);
      }
    });
  }
}
