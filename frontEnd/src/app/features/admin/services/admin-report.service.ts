import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdminReportService {
  private apiUrl = `${environment.apiUrl}/admin/reports`;

  constructor(private http: HttpClient) {}

  getSystemReports(period: string = '30'): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}?period=${period}`, { withCredentials: true });
  }

  getUserReports(period: string = '30'): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/users?period=${period}`, { withCredentials: true });
  }

  getBookingReports(period: string = '30'): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/bookings?period=${period}`, { withCredentials: true });
  }

  getRevenueReports(period: string = '30'): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/revenue?period=${period}`, { withCredentials: true });
  }

  getProviderReports(period: string = '30'): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/providers?period=${period}`, { withCredentials: true });
  }

  exportReport(reportType: string, period: string = '30'): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/export/${reportType}?period=${period}`, {
      responseType: 'blob',
      withCredentials: true
    });
  }

  getDashboardStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/dashboard`, { withCredentials: true });
  }
}
