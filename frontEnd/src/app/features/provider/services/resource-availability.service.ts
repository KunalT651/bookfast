import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResourceAvailability } from '../models/resource-availability.model';

@Injectable({ providedIn: 'root' })
export class ResourceAvailabilityService {
  private apiUrl = 'http://localhost:8080/api/provider/availabilities';

  constructor(private http: HttpClient) {}

  getAvailabilitiesByResource(resourceId: number): Observable<ResourceAvailability[]> {
    return this.http.get<ResourceAvailability[]>(`${this.apiUrl}/resource/${resourceId}`);
  }

  getAvailability(id: number): Observable<ResourceAvailability> {
    return this.http.get<ResourceAvailability>(`${this.apiUrl}/${id}`);
  }

  createAvailability(availability: ResourceAvailability): Observable<ResourceAvailability> {
    return this.http.post<ResourceAvailability>(this.apiUrl, availability);
  }

  updateAvailability(id: number, availability: ResourceAvailability): Observable<ResourceAvailability> {
    return this.http.put<ResourceAvailability>(`${this.apiUrl}/${id}`, availability);
  }

  deleteAvailability(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}