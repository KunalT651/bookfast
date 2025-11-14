import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from './shared/components/navbar.component';
import { AuthService } from './features/auth/services/auth.service';
import { UserStateService } from './shared/services/user-state.service';

// ...existing code...
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
      const hiddenRoutes = ['/login', '/registration', '/provider/registration'];
      this.showNavbar = !hiddenRoutes.includes(this.router.url.split('?')[0]);
    });
  }

  ngOnInit() {
    const publicRoutes = [
      '/',
      '/login',
      '/registration',
      '/provider/registration',
      '/create-admin'
    ];
  const currentUrl = this.router.url.split('?')[0];
    const isPublic = publicRoutes.some(route => currentUrl.startsWith(route));
  console.log('[AppComponent] currentUrl:', currentUrl, 'isPublic:', isPublic);
    if (!isPublic) {
      this.authService.getCurrentUser().subscribe({
        next: (user) => {
          this.userState.setUser(user);
        },
        error: () => {
          this.userState.clear();
          this.router.navigate(['/login']);
        }
      });
      this.loggedIn.subscribe(isLoggedIn => {
        const isPublic = publicRoutes.some(route => currentUrl.startsWith(route));
        console.log('[AppComponent] loggedIn:', isLoggedIn, 'currentUrl:', currentUrl, 'isPublic:', isPublic);
        if (!isLoggedIn && !isPublic) {
          console.log('[AppComponent] Redirecting to /login');
          this.router.navigate(['/login']);
        }
      });
    }
    // For public routes, do NOT call getCurrentUser or redirect
  }
  
  isPublicRoute(url: string): boolean {
    // Debug log for route matching
    const publicRoutes = [
      '/login',
      '/registration',
      '/provider/registration',
      '/create-admin'
    ];
    const result = publicRoutes.some(route => url.startsWith(route));
    console.log('[AppComponent] isPublicRoute check:', url, '=>', result);
    return result;
  }
  }


