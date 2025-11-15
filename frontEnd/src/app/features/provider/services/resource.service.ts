import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Resource } from '../models/resource.model';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ResourceService {
  private apiUrl = `${environment.apiUrl}/resources`;

  constructor(private http: HttpClient) {}

  getResources(providerId: number): Observable<Resource[]> {
    console.log('[ResourceService] Making GET request to:', `${this.apiUrl}/provider/${providerId}`);
    return this.http.get<Resource[]>(`${this.apiUrl}/provider/${providerId}`, { withCredentials: true });
  }

  getResourcesForCurrentProvider(): Observable<Resource[]> {
    console.log('[ResourceService] Making GET request to:', `${this.apiUrl}/me`);
    return this.http.get<Resource[]>(`${this.apiUrl}/me`, { withCredentials: true });
  }

  getResource(id: number): Observable<Resource> {
    console.log('[ResourceService] Making GET request to:', `${this.apiUrl}/${id}`);
    return this.http.get<Resource>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }

  createResource(resource: Resource): Observable<Resource> {
    console.log('[ResourceService] Making POST request to:', this.apiUrl);
    return this.http.post<Resource>(this.apiUrl, resource, { withCredentials: true });
  }

  updateResource(resource: Resource): Observable<Resource> {
    console.log('[ResourceService] Making PUT request to:', `${this.apiUrl}/${resource.id}`);
    return this.http.put<Resource>(`${this.apiUrl}/${resource.id}`, resource, { withCredentials: true });
  }

  deleteResource(id: number): Observable<void> {
    console.log('[ResourceService] Making DELETE request to:', `${this.apiUrl}/${id}`);
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }
}