import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ProviderService {
  private apiUrl = 'http://localhost:8080/api/provider';

  constructor(private http: HttpClient) {}

  getProviderProfileForCurrentUser(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/profile/me`, { withCredentials: true });
  }

  updateProviderProfile(profileData: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/profile/me`, profileData, { withCredentials: true });
  }

  uploadProfilePicture(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/profile-picture`, formData, { withCredentials: true });
  }
}