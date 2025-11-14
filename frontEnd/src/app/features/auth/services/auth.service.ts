import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  registerCustomer(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data, { withCredentials: true });
  }

  registerProvider(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register-provider`, data, { withCredentials: true });
  }

  getServiceCategories(): Observable<string[]> {
    return this.http.get<string[]>('http://localhost:8080/api/categories', { withCredentials: true });
  }

  login(data: any): Observable<any> {
    // Add withCredentials: true to allow cookies to be sent/received
    return this.http.post(`${this.apiUrl}/login`, data, { withCredentials: true });
  }

  logout(): Observable<any> {
    // Send CSRF token from cookie in header
    const csrfToken = this.getCsrfTokenFromCookie();
    let options: any = { withCredentials: true };
    if (csrfToken) {
      options.headers = { 'X-XSRF-TOKEN': csrfToken };
    }
    return this.http.post(`${this.apiUrl}/logout`, {}, options);
  }

  private getCsrfTokenFromCookie(): string | null {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    return match ? decodeURIComponent(match[1]) : null;
  }

  getCurrentUser(): Observable<any> {
    // Fetch current user info from backend using cookie
    return this.http.get(`${this.apiUrl}/me`, { withCredentials: true });
  }

    requestPasswordReset(email: string): Observable<any> {
      return this.http.post(`${this.apiUrl}/password-reset`, { email }, { withCredentials: true });
    }
}