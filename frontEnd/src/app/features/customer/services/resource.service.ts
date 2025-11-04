import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Resource } from '../models/resource.model';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ResourceService {
  private apiUrl = `${environment.apiUrl}/resources`;

  constructor(private http: HttpClient) {}

  filterResources(specialization?: string, status?: string): Observable<Resource[]> {
    let params: any = {};
    if (specialization) params.specialization = specialization;
    if (status) params.status = status;
    return this.http.get<Resource[]>(`${this.apiUrl}/filter`, { params, withCredentials: true });
  }

  getResources(): Observable<Resource[]> {
    return this.http.get<Resource[]>(this.apiUrl, { withCredentials: true });
  }

  getResource(resourceId: number): Observable<Resource> {
    return this.http.get<Resource>(`${this.apiUrl}/${resourceId}`, { withCredentials: true });
  }
}