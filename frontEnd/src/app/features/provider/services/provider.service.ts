import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ProviderService {
  private apiUrl = `${environment.apiUrl}/provider`;

  constructor(private http: HttpClient) {}

  getProviderProfileForCurrentUser(): Observable<any> {
    console.log('[ProviderService] Making GET request to:', `${this.apiUrl}/profile/me`);
    return this.http.get<any>(`${this.apiUrl}/profile/me`, { withCredentials: true });
  }

  updateProviderProfile(profileData: any): Observable<any> {
    console.log('[ProviderService] Making PUT request to:', `${this.apiUrl}/profile/me`);
    return this.http.put<any>(`${this.apiUrl}/profile/me`, profileData, { withCredentials: true });
  }

  uploadProfilePicture(file: File): Observable<any> {
    console.log('[ProviderService] Making POST request to:', `${this.apiUrl}/profile/upload-picture`);
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/profile/upload-picture`, formData, { withCredentials: true });
  }
}