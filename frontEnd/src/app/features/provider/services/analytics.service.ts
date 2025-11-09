import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProviderAnalyticsService {
  private apiUrl = `${environment.apiUrl}/provider`;

  constructor(private http: HttpClient) {}

  getAnalytics(period: string = '30'): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/analytics?period=${period}`, { withCredentials: true });
  }

  getEarnings(period: string = '30'): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/earnings?period=${period}`, { withCredentials: true });
  }
}

