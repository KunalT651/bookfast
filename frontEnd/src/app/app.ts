import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from './shared/components/navbar.component';
import { ProviderNavbarComponent } from './features/provider/components/provider-navbar/provider-navbar.component';
import { AuthService } from './features/auth/services/auth.service';
import { UserStateService } from './shared/services/user-state.service';

// ...existing code...
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
  isProviderRoute: boolean = false;

  constructor(
    private authService: AuthService,
    private userState: UserStateService,
    private router: Router
  ) {
    this.loggedIn = this.userState.getLoggedIn();
    this.router.events.subscribe(() => {
      const hiddenRoutes = ['/login', '/registration', '/provider/registration'];
      this.showNavbar = !hiddenRoutes.includes(this.router.url.split('?')[0]);
      this.isProviderRoute = this.isProviderDashboardRoute();
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
      console.log('[AppComponent] Calling getCurrentUser()...');
      this.authService.getCurrentUser().subscribe({
        next: (user) => {
          console.log('[AppComponent] getCurrentUser success:', user);
          this.userState.setUser(user);
          this.userRole = user ? user.role?.name || null : null;
        },
        error: (error) => {
          console.log('[AppComponent] getCurrentUser error:', error);
          // Don't clear user state immediately - give user a chance to see the error
          // this.userState.clear();
          // this.userRole = null;
          // this.router.navigate(['/login']);
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
    
    // Subscribe to user state changes
    this.userState.getUser().subscribe(user => {
      this.userRole = user ? user.role?.name || null : null;
      console.log('User state changed:', user);
      console.log('User role:', this.userRole);
      console.log('Is provider route:', this.isProviderRoute);
      console.log('Should show provider navbar:', this.shouldShowProviderNavbar());
    });
    
    // Check if current route is provider route
    this.isProviderRoute = this.isProviderDashboardRoute();
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

  isProviderDashboardRoute(): boolean {
    // Only show provider navbar for /provider/dashboard and its subroutes
    const path = window.location.pathname;
    return path === '/provider/dashboard' || path.startsWith('/provider/dashboard/');
  }

  shouldShowProviderNavbar(): boolean {
    // Show provider navbar only if we're on a provider route AND user role is PROVIDER
    return this.isProviderRoute && this.userRole === 'PROVIDER';
  }
  }


