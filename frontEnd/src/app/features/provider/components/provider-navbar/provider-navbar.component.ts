import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-provider-navbar',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './provider-navbar.component.html',
  styleUrls: ['./provider-navbar.component.css']
})
export class ProviderNavbarComponent {
  onLogout() {
    // Implement logout logic, e.g., clear tokens and redirect
    localStorage.removeItem('jwt');
    window.location.href = '/login';
  }
}