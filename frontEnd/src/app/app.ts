import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from './shared/components/navbar.component';
import { ProviderNavbarComponent } from './features/provider/components/provider-navbar/provider-navbar.component';
import { AuthService } from './features/auth/services/auth.service';
import { UserStateService } from './shared/services/user-state.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent, ProviderNavbarComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class AppComponent implements OnInit {
  loggedIn;
  showNavbar = true;
  userRole: string | null = null;
  isProviderRoute = false;

  constructor(
    private authService: AuthService,
    private userState: UserStateService,
    private router: Router
  ) {
    this.loggedIn = this.userState.getLoggedIn();
    this.router.events.subscribe(() => {
      const hiddenRoutes = ['/login', '/registration', '/provider/registration'];
      this.showNavbar = !hiddenRoutes.includes(this.router.url.split('?')[0]);
      
      // Check if current route is a provider route
      const currentUrl = this.router.url.split('?')[0];
      this.isProviderRoute = currentUrl.startsWith('/provider');
      
      // Update user role from state
      this.userState.getUser().subscribe(user => {
        this.userRole = user ? user.role?.name : null;
      });
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

  shouldShowProviderNavbar(): boolean {
    return this.isProviderRoute && this.userRole === 'PROVIDER';
  }
}


