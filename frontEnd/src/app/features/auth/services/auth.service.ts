import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseService } from '../../../shared/services/base.service';

@Injectable({ providedIn: 'root' })
export class AuthService extends BaseService {
  private authUrl = `${this.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  registerCustomer(data: any): Observable<any> {
    return this.http.post(`${this.authUrl}/register`, data, { withCredentials: true });
  }

  registerProvider(data: any): Observable<any> {
    return this.http.post(`${this.authUrl}/register-provider`, data, { withCredentials: true });
  }

  getServiceCategories(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/categories`, { withCredentials: true });
  }

  login(data: any): Observable<any> {
    // Add withCredentials: true to allow cookies to be sent/received
    return this.http.post(`${this.authUrl}/login`, data, { withCredentials: true });
  }

  logout(): Observable<any> {
    // Send CSRF token from cookie in header
    const csrfToken = this.getCsrfTokenFromCookie();
    let options: any = { withCredentials: true };
    if (csrfToken) {
      options.headers = { 'X-XSRF-TOKEN': csrfToken };
    }
    return this.http.post(`${this.authUrl}/logout`, {}, options);
  }

  private getCsrfTokenFromCookie(): string | null {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    return match ? decodeURIComponent(match[1]) : null;
  }

  getCurrentUser(): Observable<any> {
    // Fetch current user info from backend using cookie
    return this.http.get(`${this.authUrl}/me`, { withCredentials: true });
  }

    requestPasswordReset(email: string): Observable<any> {
      return this.http.post(`${this.authUrl}/password-reset`, { email }, { withCredentials: true });
    }
}