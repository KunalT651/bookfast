import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AdminDashboardService {
    private apiUrl = `${environment.apiUrl}/api/admin`;

    constructor(private http: HttpClient) {}

    getDashboardData(): Observable<any> {
        console.log('[AdminDashboardService] Making GET request to:', `${this.apiUrl}/dashboard`);
        return this.http.get<any>(`${this.apiUrl}/dashboard`, { withCredentials: true });
    }

    getSystemStats(): Observable<any> {
        console.log('[AdminDashboardService] Making GET request to:', `${this.apiUrl}/stats`);
        return this.http.get<any>(`${this.apiUrl}/stats`, { withCredentials: true });
    }

    getRecentActivity(): Observable<any> {
        console.log('[AdminDashboardService] Making GET request to:', `${this.apiUrl}/recent-activity`);
        return this.http.get<any>(`${this.apiUrl}/recent-activity`, { withCredentials: true });
    }
}
