
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ProviderNavbarComponent } from '../provider-navbar/provider-navbar.component';

@Component({
  selector: 'app-provider-dashboard',
  standalone: true,
  imports: [RouterOutlet, ProviderNavbarComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class ProviderDashboardComponent {}