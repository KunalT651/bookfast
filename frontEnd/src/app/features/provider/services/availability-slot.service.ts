import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AvailabilitySlot } from '../models/availability-slot.model';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AvailabilitySlotService {
  private baseUrl = `${environment.apiUrl}/resources`;

  constructor(private http: HttpClient) {}

  getAvailabilitySlotsForResource(resourceId: number): Observable<AvailabilitySlot[]> {
    return this.http.get<AvailabilitySlot[]>(`${this.baseUrl}/${resourceId}/availability`, { withCredentials: true });
  }

  createAvailabilitySlot(
    resourceId: number,
    date: string,
    startTime: string,
    endTime: string,
    status: string
  ): Observable<AvailabilitySlot> {
    const payload = { date, startTime, endTime, status };
    return this.http.post<AvailabilitySlot>(`${this.baseUrl}/${resourceId}/availability`, payload, { withCredentials: true });
  }

  updateAvailabilitySlot(
    resourceId: number,
    slotId: number,
    date: string,
    startTime: string,
    endTime: string,
    status: string
  ): Observable<AvailabilitySlot> {
    const payload = { date, startTime, endTime, status };
    return this.http.put<AvailabilitySlot>(`${this.baseUrl}/${resourceId}/availability/${slotId}`, payload, { withCredentials: true });
  }

  deleteAvailabilitySlot(resourceId: number, slotId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${resourceId}/availability/${slotId}`, { withCredentials: true });
  }
}
