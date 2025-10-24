import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Resource } from '../models/resource.model';

@Injectable({ providedIn: 'root' })
export class ResourceService {
  filterResources(specialization?: string, status?: string): Observable<Resource[]> {
    let params: any = {};
    if (specialization) params.specialization = specialization;
    if (status) params.status = status;
    return this.http.get<Resource[]>(`${this.apiUrl}/filter`, { params, withCredentials: true });
  }
  private apiUrl = 'http://localhost:8080/api/resources'; // Adjust endpoint as needed

  constructor(private http: HttpClient) {}

  getResources(): Observable<Resource[]> {
    return this.http.get<Resource[]>(this.apiUrl, { withCredentials: true });
  }

  getResource(resourceId: number): Observable<Resource> {
    return this.http.get<Resource>(`${this.apiUrl}/${resourceId}`, { withCredentials: true });
  }
}