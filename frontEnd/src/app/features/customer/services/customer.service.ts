import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private apiUrl = 'http://localhost:8080/api/customers/profile';

  constructor(private http: HttpClient) {}

  getProfile(): Observable<any> {
    return this.http.get<any>(this.apiUrl, { withCredentials: true });
  }

  updateProfile(profile: any): Observable<any> {
    return this.http.put<any>(this.apiUrl, profile, { withCredentials: true });
  }
}
