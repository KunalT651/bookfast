import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SystemReportService {
  private apiUrl = 'http://localhost:8080/api/admin/reports';

  constructor(private http: HttpClient) {}

  getReports(): Observable<any> {
    return this.http.get<any>(this.apiUrl, { withCredentials: true });
  }
}
