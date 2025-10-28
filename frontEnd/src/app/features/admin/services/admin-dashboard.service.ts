import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AdminDashboardService {
    private apiUrl = 'http://localhost:8080/api/admin';

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
