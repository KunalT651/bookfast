import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ProviderService {
  private apiUrl = 'http://localhost:8080/api/provider/profile';

  constructor(private http: HttpClient) {}

  getProviderProfileByUserId(userId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/user/${userId}`, { withCredentials: true });
  }

    getProviderProfileForCurrentUser(): Observable<any> {
    // Assumes backend endpoint /api/provider/profile/me returns the current provider's profile
    return this.http.get<any>(`${this.apiUrl}/me`, { withCredentials: true });
  }
}