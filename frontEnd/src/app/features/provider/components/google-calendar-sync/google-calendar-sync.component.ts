import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-google-calendar-sync',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './google-calendar-sync.component.html',
  styleUrls: ['./google-calendar-sync.component.css']
})
export class GoogleCalendarSyncComponent implements OnInit {
  isConnected = false;
  isGmail = false;
  currentEmail = '';
  isConnecting = false;
  isDisconnecting = false;
  successMessage = '';
  errorMessage = '';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.checkCalendarStatus();
  }

  checkCalendarStatus() {
    this.http.get<any>(`${environment.apiUrl}/api/calendar/status`).subscribe({
      next: (response) => {
        this.isConnected = response.connected;
        this.isGmail = response.isGmail;
        this.currentEmail = response.email;
      },
      error: (error) => {
        console.error('Error checking calendar status:', error);
        this.errorMessage = 'Failed to check calendar status';
      }
    });
  }

  connectCalendar() {
    this.isConnecting = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.http.get<any>(`${environment.apiUrl}/api/calendar/auth-url`).subscribe({
      next: (response) => {
        // Open Google OAuth in a popup window
        const popup = window.open(
          response.authUrl,
          'googleAuth',
          'width=500,height=600,scrollbars=yes,resizable=yes'
        );

        // Listen for the popup to close or receive a message
        const checkClosed = setInterval(() => {
          if (popup?.closed) {
            clearInterval(checkClosed);
            this.isConnecting = false;
            // Check status again after popup closes
            setTimeout(() => this.checkCalendarStatus(), 1000);
          }
        }, 1000);

        // Listen for message from popup (if using postMessage)
        window.addEventListener('message', (event) => {
          if (event.origin !== window.location.origin) return;
          
          if (event.data.type === 'GOOGLE_AUTH_SUCCESS') {
            clearInterval(checkClosed);
            popup?.close();
            this.isConnecting = false;
            this.successMessage = 'Calendar connected successfully!';
            this.checkCalendarStatus();
          } else if (event.data.type === 'GOOGLE_AUTH_ERROR') {
            clearInterval(checkClosed);
            popup?.close();
            this.isConnecting = false;
            this.errorMessage = event.data.error || 'Failed to connect calendar';
          }
        });
      },
      error: (error) => {
        this.isConnecting = false;
        this.errorMessage = error.error?.error || 'Failed to initiate calendar connection';
      }
    });
  }

  disconnectCalendar() {
    this.isDisconnecting = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.http.post<any>(`${environment.apiUrl}/api/calendar/disconnect`, {}).subscribe({
      next: (response) => {
        this.isDisconnecting = false;
        this.successMessage = response.message;
        this.isConnected = false;
      },
      error: (error) => {
        this.isDisconnecting = false;
        this.errorMessage = error.error?.error || 'Failed to disconnect calendar';
      }
    });
  }
}
