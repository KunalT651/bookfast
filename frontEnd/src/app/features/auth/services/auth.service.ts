import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  registerCustomer(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data);
  }

  registerProvider(data: any): Observable<any> {
  return this.http.post(`${this.apiUrl}/register-provider`, data);
}

getServiceCategories(): Observable<string[]> {
  return this.http.get<string[]>('http://localhost:8080/api/services/categories');
}

login(data: any): Observable<any> {
  return this.http.post(`${this.apiUrl}/login`, data);
}
}