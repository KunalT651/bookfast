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
    // Call backend to clear cookie
    return this.http.post(`${this.apiUrl}/logout`, {}, { withCredentials: true });
  }

  getCurrentUser(): Observable<any> {
    // Fetch current user info from backend using cookie
    return this.http.get(`${this.apiUrl}/me`, { withCredentials: true });
  }
}