import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResourceAvailability } from '../models/resource-availability.model';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ResourceAvailabilityService {
  private apiUrl = `${environment.apiUrl}/api/resources`;

  constructor(private http: HttpClient) {}

  getAvailabilitiesByResource(resourceId: number): Observable<ResourceAvailability[]> {
    return this.http.get<ResourceAvailability[]>(`${this.apiUrl}/${resourceId}/availability`, { withCredentials: true });
  }

  getAvailability(id: number): Observable<ResourceAvailability> {
    return this.http.get<ResourceAvailability>(`${this.apiUrl}/availability/${id}`, { withCredentials: true });
  }

  createAvailability(availability: ResourceAvailability): Observable<ResourceAvailability> {
    // Must provide resourceId for slot creation
    return this.http.post<ResourceAvailability>(`${this.apiUrl}/${availability.resourceId}/availability`, availability, { withCredentials: true });
  }

  updateAvailability(id: number, availability: ResourceAvailability): Observable<ResourceAvailability> {
    // If you want to support slot update, use this endpoint (not implemented in backend yet)
    return this.http.put<ResourceAvailability>(`${this.apiUrl}/availability/${id}`, availability, { withCredentials: true });
  }

  deleteAvailability(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/availability/${id}`, { withCredentials: true });
  }
}