
import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd, RouterModule } from '@angular/router';
import { ProviderNavbarComponent } from '../provider-navbar/provider-navbar.component';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-provider-dashboard',
  standalone: true,
  imports: [RouterOutlet, RouterModule, ProviderNavbarComponent, CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class ProviderDashboardComponent implements OnInit {
  isChildRouteActive = false;

  constructor(private router: Router) {}

  ngOnInit() {
    // Check initial route
    this.updateRouteState();
    
    // Listen for route changes
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.updateRouteState();
      });
  }

  private updateRouteState() {
    const url = this.router.url.split('?')[0]; // Remove query params
    // Check if we're on a child route (not just /provider/dashboard)
    // Dashboard grid shows when on exact base route, child routes show their components
    this.isChildRouteActive = url !== '/provider/dashboard' && url.startsWith('/provider/dashboard/');
  }
}