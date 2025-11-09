import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-calendar-sync',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="calendar-sync">
      <div class="sync-header">
        <h3>📅 Google Calendar Sync</h3>
        <p>Sync your Google Calendar with BookFast to automatically add your bookings</p>
      </div>

      <div class="sync-status" [class.connected]="isConnected">
        <div class="status-indicator">
          <span class="status-dot" [class.connected]="isConnected"></span>
          <span class="status-text">
            {{ isConnected ? 'Connected to Google Calendar' : 'Not connected' }}
          </span>
        </div>
      </div>

      <div class="sync-content" *ngIf="!isConnected">
        <div class="auth-instructions" *ngIf="!isGmail">
          <div class="gmail-warning">
            <h4>⚠️ Gmail Required</h4>
            <p>Google Calendar integration requires a Gmail address. Your current email is: <strong>{{ currentEmail }}</strong></p>
            <p>To enable calendar sync, please:</p>
            <ul>
              <li>Log out and register with a Gmail account (e.g., yourname@gmail.com), or</li>
              <li>Update your profile to use a Gmail address</li>
            </ul>
          </div>
        </div>

        <div class="auth-instructions" *ngIf="isGmail">
          <h4>How to connect your Google Calendar:</h4>
          <ol>
            <li>Click the "Connect Google Calendar" button below</li>
            <li>Sign in to your Google account</li>
            <li>Grant permission to BookFast to access your calendar</li>
            <li>You'll be redirected back to BookFast automatically</li>
          </ol>
        </div>

        <div class="auth-actions" *ngIf="isGmail">
          <button type="button" class="btn btn-google" (click)="connectCalendar()" [disabled]="isConnecting">
            <i class="fab fa-google"></i>
            {{ isConnecting ? 'Connecting...' : 'Connect Google Calendar' }}
          </button>
        </div>
      </div>

      <div class="sync-content" *ngIf="isConnected">
        <div class="connected-info">
          <h4>✅ Calendar Connected Successfully!</h4>
          <p>Your Google Calendar is now synced with BookFast. Your bookings will be automatically added to your calendar.</p>
          
          <div class="sync-features">
            <h5>What's synced:</h5>
            <ul>
              <li>New bookings are automatically added to your Google Calendar</li>
              <li>Booking changes are reflected in both systems</li>
              <li>You'll receive calendar notifications for your bookings</li>
            </ul>
          </div>

          <div class="disconnect-section">
            <button type="button" class="btn btn-danger" (click)="disconnectCalendar()" [disabled]="isDisconnecting">
              {{ isDisconnecting ? 'Disconnecting...' : 'Disconnect Calendar' }}
            </button>
          </div>
        </div>
      </div>

      <div class="messages">
        <div class="success-message" *ngIf="successMessage">
          {{ successMessage }}
        </div>
        <div class="error-message" *ngIf="errorMessage">
          {{ errorMessage }}
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./calendar-sync.component.css']
})
export class CalendarSyncComponent implements OnInit {
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
    this.http.get<any>(`${environment.apiUrl}/calendar/status`, { withCredentials: true }).subscribe({
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

    this.http.get<any>(`${environment.apiUrl}/calendar/auth-url`, { withCredentials: true }).subscribe({
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

    this.http.post<any>(`${environment.apiUrl}/calendar/disconnect`, {}, { withCredentials: true }).subscribe({
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
