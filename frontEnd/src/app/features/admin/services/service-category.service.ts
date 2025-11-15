import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ServiceCategoryService {
  private apiUrl = `${environment.apiUrl}/admin/categories`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<any[]> {
    // Add withCredentials: true to allow cookies to be sent/received
    return this.http.get<any[]>(this.apiUrl, { withCredentials: true });
  }
  
  create(data: any): Observable<any> {
    return this.http.post(this.apiUrl, data, { withCredentials: true });
  }

  update(id: number, data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, data, { withCredentials: true });
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`, { withCredentials: true });
  }
}