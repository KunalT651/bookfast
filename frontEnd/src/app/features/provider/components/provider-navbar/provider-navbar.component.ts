import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProviderService } from '../../services/provider.service';
import { AuthService } from '../../../auth/services/auth.service';
import { UserStateService } from '../../../../shared/services/user-state.service';
import { ProfilePictureService } from '../../../../shared/services/profile-picture.service';
import { filter } from 'rxjs/operators';
import { Subscription } from 'rxjs';
import { environment } from 'environments/environment';

@Component({
  selector: 'app-provider-navbar',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './provider-navbar.component.html',
  styleUrls: ['./provider-navbar.component.css']
})
export class ProviderNavbarComponent implements OnInit, OnDestroy {
  profilePictureUrl: string | null = null;
  profilePictureFullUrl: string | null = null;
  currentRoute: string = '';
  private profilePictureSubscription?: Subscription;

  constructor(
    private providerService: ProviderService,
    private authService: AuthService,
    private userState: UserStateService,
    private router: Router,
    private profilePictureService: ProfilePictureService
  ) {
    // Track current route for active link detection and reload profile picture on navigation
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.currentRoute = event.urlAfterRedirects;
        // Reload profile picture when navigating (in case it was updated)
        if (event.urlAfterRedirects.startsWith('/provider/dashboard')) {
          this.loadProfilePicture();
        }
      });
  }

  ngOnInit() {
    this.currentRoute = this.router.url;
    this.loadProfilePicture();
    
    // Subscribe to profile picture updates
    this.profilePictureSubscription = this.profilePictureService.profilePictureUpdated$.subscribe(
      (imageUrl) => {
        console.log('[ProviderNavbarComponent] Profile picture update received from service:', imageUrl);
        // Always update - if imageUrl is null, it means no photo, show placeholder
        // If imageUrl exists, use it as logo
        this.profilePictureUrl = imageUrl || null;
        this.profilePictureFullUrl = this.constructImageUrl(imageUrl);
        console.log('[ProviderNavbarComponent] Navbar logo updated - profilePictureUrl:', this.profilePictureUrl);
        console.log('[ProviderNavbarComponent] Navbar logo updated - profilePictureFullUrl:', this.profilePictureFullUrl);
      }
    );
  }

  ngOnDestroy() {
    if (this.profilePictureSubscription) {
      this.profilePictureSubscription.unsubscribe();
    }
  }

  isActiveRoute(route: string): boolean {
    if (route === '/provider/dashboard') {
      // Home should be active ONLY on exact base dashboard route (not on child routes like /resources, /bookings, etc.)
      const url = this.currentRoute.split('?')[0]; // Remove query params
      return url === '/provider/dashboard';
    }
    // For other routes, check if current route starts with the route path
    const url = this.currentRoute.split('?')[0];
    return url.startsWith(route) && route !== '/provider/dashboard';
  }

  getProfileImageUrl(): string {
    // Always construct the URL on-the-fly to ensure it's correct
    // This ensures old relative URLs are converted to full backend URLs
    if (!this.profilePictureUrl) {
      return '';
    }
    const url = this.constructImageUrl(this.profilePictureUrl);
    console.log('[ProviderNavbarComponent] getProfileImageUrl() - Input:', this.profilePictureUrl, 'Output:', url);
    // Ensure we never return a relative URL (would cause 404 from frontend server)
    if (url && !url.startsWith('http://') && !url.startsWith('https://')) {
      console.error('[ProviderNavbarComponent] ERROR: getProfileImageUrl() returned non-absolute URL:', url);
      return ''; // Return empty to show placeholder instead of broken image
    }
    return url || '';
  }

  loadProfilePicture() {
    console.log('[ProviderNavbarComponent] Loading profile picture from database...');
    this.providerService.getProviderProfileForCurrentUser().subscribe({
      next: (profile) => {
        console.log('[ProviderNavbarComponent] Profile loaded from database. imageUrl:', profile.imageUrl);
        // Fetch imageUrl from database - if exists, use as logo; if null, show placeholder
        this.profilePictureUrl = profile.imageUrl || null;
        this.profilePictureFullUrl = this.constructImageUrl(profile.imageUrl);
        console.log('[ProviderNavbarComponent] Navbar logo set from database - profilePictureUrl:', this.profilePictureUrl);
        console.log('[ProviderNavbarComponent] Navbar logo set from database - profilePictureFullUrl:', this.profilePictureFullUrl);
      },
      error: (err) => {
        console.error('[ProviderNavbarComponent] Failed to load profile picture from database:', err);
        // On error, keep existing logo or show placeholder
        this.profilePictureUrl = null;
        this.profilePictureFullUrl = null;
      }
    });
  }

  constructImageUrl(imageUrl: string | null | undefined): string | null {
    if (!imageUrl) {
      console.log('[ProviderNavbarComponent] No imageUrl provided - showing placeholder');
      return null;
    }
    
    // If it's already a full URL (http/https), return as is (deployment-ready)
    // Imgur URLs and other cloud storage URLs are already full URLs
    if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
      console.log('[ProviderNavbarComponent] Image URL is already full URL (deployment-ready):', imageUrl);
      return imageUrl;
    }
    
    // Legacy support: Handle old relative URLs from local storage
    // Old format: /uploads/profile-pictures/... needs to point to backend server
    const baseUrl = environment.apiUrl.replace('/api', '');
    console.log('[ProviderNavbarComponent] Constructing URL from relative path. Base URL:', baseUrl, 'Original URL:', imageUrl);
    
    // Ensure the path starts with / if it doesn't already
    const imagePath = imageUrl.startsWith('/') ? imageUrl : '/' + imageUrl;
    const fullUrl = baseUrl + imagePath;
    console.log('[ProviderNavbarComponent] Constructed full image URL:', fullUrl);
    
    // Verify it's an absolute URL
    if (!fullUrl.startsWith('http://') && !fullUrl.startsWith('https://')) {
      console.error('[ProviderNavbarComponent] ERROR: Constructed URL is not absolute!', fullUrl);
      // Fallback: use environment API URL
      const baseUrl = environment.apiUrl.replace('/api', '');
      return baseUrl + imagePath;
    }
    
    return fullUrl;
  }

  onLogout() {
    console.log('[ProviderNavbarComponent] Logging out...');
    this.authService.logout().subscribe({
      next: () => {
        console.log('[ProviderNavbarComponent] Logout successful');
        this.userState.clear();
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('[ProviderNavbarComponent] Logout error:', err);
        // Clear state and redirect anyway
        this.userState.clear();
        this.router.navigate(['/login']);
      }
    });
  }
}