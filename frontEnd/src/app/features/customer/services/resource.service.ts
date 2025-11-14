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
    console.log('[ResourceService] Fetching resources from:', this.apiUrl);
    return this.http.get<Resource[]>(this.apiUrl, { withCredentials: true }).pipe(
      // Add error handling with catchError
      catchError((error: any) => {
        console.error('[ResourceService] Error fetching resources:', error);
        console.error('[ResourceService] Error details:', {
          status: error?.status,
          statusText: error?.statusText,
          message: error?.message,
          error: error?.error,
          url: error?.url
        });
        // Return empty array on error instead of throwing
        return of([]);
      })
    );
  }

  getResource(resourceId: number): Observable<Resource> {
    return this.http.get<Resource>(`${this.apiUrl}/${resourceId}`, { withCredentials: true });
  }
}