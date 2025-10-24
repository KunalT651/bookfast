import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Resource } from '../models/resource.model';

@Injectable({ providedIn: 'root' })
export class ResourceService {
  private apiUrl = 'http://localhost:8080/api/resources';

  constructor(private http: HttpClient) {}

  getResources(providerId: number): Observable<Resource[]> {
    return this.http.get<Resource[]>(`${this.apiUrl}/provider/${providerId}`, { withCredentials: true });
  }

  getResourcesForCurrentProvider(): Observable<Resource[]> {
    // Assumes backend endpoint /api/resources/me returns resources for logged-in provider
    return this.http.get<Resource[]>(`${this.apiUrl}/me`, { withCredentials: true });
  }

  getResource(id: number): Observable<Resource> {
    return this.http.get<Resource>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }

  createResource(resource: Resource): Observable<Resource> {
    return this.http.post<Resource>(this.apiUrl, resource, { withCredentials: true });
  }

  updateResource(resource: Resource): Observable<Resource> {
    return this.http.put<Resource>(`${this.apiUrl}/${resource.id}`, resource, { withCredentials: true });
  }

  deleteResource(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }
}