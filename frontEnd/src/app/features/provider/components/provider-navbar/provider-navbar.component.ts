import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProviderService } from '../../services/provider.service';

@Component({
  selector: 'app-provider-navbar',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './provider-navbar.component.html',
  styleUrls: ['./provider-navbar.component.css']
})
export class ProviderNavbarComponent implements OnInit {
  profilePictureUrl: string | null = null;

  constructor(private providerService: ProviderService) {}

  ngOnInit() {
    this.loadProfilePicture();
  }

  loadProfilePicture() {
    this.providerService.getProviderProfileForCurrentUser().subscribe({
      next: (profile) => {
        this.profilePictureUrl = profile.imageUrl || null;
      },
      error: (err) => {
        console.error('Failed to load profile picture', err);
      }
    });
  }

  onLogout() {
    // Implement logout logic, e.g., clear tokens and redirect
    localStorage.removeItem('jwt');
    window.location.href = '/login';
  }
}