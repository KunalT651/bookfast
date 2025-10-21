import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ServiceResource } from '../models/service-resource.model';

@Injectable({ providedIn: 'root' })
export class ServiceResourceService {
  private apiUrl = 'http://localhost:8080/api/resources';

  constructor(private http: HttpClient) {}

  getResourcesByProvider(providerId: number): Observable<ServiceResource[]> {
    return this.http.get<ServiceResource[]>(`${this.apiUrl}/provider/${providerId}`, { withCredentials: true });
  }

  getResource(id: number): Observable<ServiceResource> {
    return this.http.get<ServiceResource>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }

  createResource(resource: ServiceResource): Observable<ServiceResource> {
    return this.http.post<ServiceResource>(this.apiUrl, resource, { withCredentials: true });
  }

  updateResource(id: number, resource: ServiceResource): Observable<ServiceResource> {
    return this.http.put<ServiceResource>(`${this.apiUrl}/${id}`, resource, { withCredentials: true });
  }

  deleteResource(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }
}