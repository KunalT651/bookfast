import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-calendar-callback',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="callback-container">
      <div class="callback-content">
        <div *ngIf="isProcessing" class="processing">
          <div class="spinner"></div>
          <h3>Processing Google Calendar authorization...</h3>
          <p>Please wait while we complete the setup.</p>
        </div>
        
        <div *ngIf="isSuccess" class="success">
          <div class="success-icon">✅</div>
          <h3>Calendar Connected Successfully!</h3>
          <p>Your Google Calendar has been connected to BookFast.</p>
          <button (click)="closeWindow()" class="btn btn-primary">Continue</button>
        </div>
        
        <div *ngIf="isError" class="error">
          <div class="error-icon">❌</div>
          <h3>Connection Failed</h3>
          <p>{{ errorMessage }}</p>
          <button (click)="closeWindow()" class="btn btn-secondary">Close</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .callback-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background-color: #f5f5f5;
    }
    
    .callback-content {
      text-align: center;
      padding: 2rem;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      max-width: 400px;
    }
    
    .spinner {
      width: 40px;
      height: 40px;
      border: 4px solid #f3f3f3;
      border-top: 4px solid #3498db;
      border-radius: 50%;
      animation: spin 1s linear infinite;
      margin: 0 auto 1rem;
    }
    
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
    
    .success-icon, .error-icon {
      font-size: 3rem;
      margin-bottom: 1rem;
    }
    
    .btn {
      padding: 0.75rem 1.5rem;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 1rem;
      margin-top: 1rem;
    }
    
    .btn-primary {
      background-color: #3498db;
      color: white;
    }
    
    .btn-secondary {
      background-color: #95a5a6;
      color: white;
    }
    
    .btn:hover {
      opacity: 0.9;
    }
  `]
})
export class CalendarCallbackComponent implements OnInit {
  isProcessing = true;
  isSuccess = false;
  isError = false;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.handleCallback();
  }

  handleCallback() {
    this.route.queryParams.subscribe(params => {
      const code = params['code'];
      const error = params['error'];
      const state = params['state'];

      if (error) {
        this.handleError(error);
        return;
      }

      if (code && state) {
        this.exchangeCodeForTokens(code, state);
      } else {
        this.handleError('Missing authorization code or state parameter');
      }
    });
  }

  exchangeCodeForTokens(code: string, state: string) {
    this.http.post<any>(`${environment.apiUrl}/api/calendar/callback`, {
      code: code,
      state: state
    }).subscribe({
      next: (response) => {
        this.isProcessing = false;
        this.isSuccess = true;
        
        // Notify parent window of success
        if (window.opener) {
          window.opener.postMessage({
            type: 'GOOGLE_AUTH_SUCCESS',
            data: response
          }, window.location.origin);
        }
      },
      error: (error) => {
        this.isProcessing = false;
        this.isError = true;
        this.errorMessage = error.error?.message || 'Failed to connect calendar';
        
        // Notify parent window of error
        if (window.opener) {
          window.opener.postMessage({
            type: 'GOOGLE_AUTH_ERROR',
            error: this.errorMessage
          }, window.location.origin);
        }
      }
    });
  }

  handleError(error: string) {
    this.isProcessing = false;
    this.isError = true;
    this.errorMessage = error;
    
    // Notify parent window of error
    if (window.opener) {
      window.opener.postMessage({
        type: 'GOOGLE_AUTH_ERROR',
        error: this.errorMessage
      }, window.location.origin);
    }
  }

  closeWindow() {
    if (window.opener) {
      window.close();
    } else {
      this.router.navigate(['/provider/dashboard/calendar-sync']);
    }
  }
}
