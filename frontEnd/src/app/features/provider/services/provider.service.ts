import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ProviderService {
  private apiUrl = `${environment.apiUrl}/api/provider`;

  constructor(private http: HttpClient) {}

  getProviderProfileForCurrentUser(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/profile`, { withCredentials: true });
  }

  updateProviderProfile(profileData: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/profile`, profileData, { withCredentials: true });
  }

  uploadProfilePicture(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/profile/picture`, formData, { withCredentials: true });
  }

  syncWithGoogleCalendar(authCode: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/calendar/sync`, { authCode }, { withCredentials: true });
  }

  markUnavailableDates(startDate: string, endDate: string, reason: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/unavailable-dates`, {
      startDate,
      endDate,
      reason
    }, { withCredentials: true });
  }

  getUnavailableDates(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/unavailable-dates`, { withCredentials: true });
  }

  removeUnavailableDate(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/unavailable-dates/${id}`, { withCredentials: true });
  }
}