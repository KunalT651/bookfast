import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProviderService } from '../../services/provider.service';

@Component({
  selector: 'app-google-calendar-sync',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './google-calendar-sync.component.html',
  styleUrls: ['./google-calendar-sync.component.css']
})
export class GoogleCalendarSyncComponent implements OnInit {
  authCode = '';
  isSyncing = false;
  syncMessage = '';
  errorMessage = '';
  isConnected = false;

  constructor(private providerService: ProviderService) {}

  ngOnInit() {
    this.checkConnectionStatus();
  }

  checkConnectionStatus() {
    // In a real implementation, you would check if the provider has already connected their calendar
    this.isConnected = false;
  }

  onSyncClick() {
    if (!this.authCode.trim()) {
      this.errorMessage = 'Please enter the authorization code';
      return;
    }

    this.isSyncing = true;
    this.errorMessage = '';
    this.syncMessage = '';

    this.providerService.syncWithGoogleCalendar(this.authCode).subscribe({
      next: (response) => {
        this.syncMessage = response.message || 'Calendar sync completed successfully!';
        this.isConnected = true;
        this.isSyncing = false;
        this.authCode = '';
      },
      error: (error) => {
        this.errorMessage = error.error?.error || 'Failed to sync calendar';
        this.isSyncing = false;
      }
    });
  }

  getGoogleAuthUrl() {
    // In a real implementation, this would generate the actual Google OAuth URL
    return 'https://accounts.google.com/oauth/authorize?client_id=YOUR_CLIENT_ID&redirect_uri=YOUR_REDIRECT_URI&scope=https://www.googleapis.com/auth/calendar&response_type=code';
  }

  openGoogleAuth() {
    window.open(this.getGoogleAuthUrl(), '_blank');
  }
}
