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
    console.log('[AvailabilitySlotService] Making GET request to:', `${this.baseUrl}/${resourceId}/availability`);
    return this.http.get<AvailabilitySlot[]>(`${this.baseUrl}/${resourceId}/availability`, { withCredentials: true });
  }

  createAvailabilitySlot(
    resourceId: number,
    date: string,
    startTime: string,
    endTime: string,
    status: string,
    reason?: string
  ): Observable<AvailabilitySlot> {
    console.log('[AvailabilitySlotService] Making POST request to:', `${this.baseUrl}/${resourceId}/availability`);
    const payload: any = { date, startTime, endTime, status };
    if (reason !== undefined && reason !== null) {
      payload.reason = reason;
    }
    return this.http.post<AvailabilitySlot>(`${this.baseUrl}/${resourceId}/availability`, payload, { withCredentials: true });
  }

  updateAvailabilitySlot(
    resourceId: number,
    slotId: number,
    date: string,
    startTime: string,
    endTime: string,
    status: string,
    reason?: string
  ): Observable<AvailabilitySlot> {
    console.log('[AvailabilitySlotService] Making PUT request to:', `${this.baseUrl}/${resourceId}/availability/${slotId}`);
    const payload: any = { date, startTime, endTime, status };
    if (reason !== undefined && reason !== null) {
      payload.reason = reason;
    }
    return this.http.put<AvailabilitySlot>(`${this.baseUrl}/${resourceId}/availability/${slotId}`, payload, { withCredentials: true });
  }

  deleteAvailabilitySlot(resourceId: number, slotId: number): Observable<void> {
    console.log('[AvailabilitySlotService] Making DELETE request to:', `${this.baseUrl}/${resourceId}/availability/${slotId}`);
    return this.http.delete<void>(`${this.baseUrl}/${resourceId}/availability/${slotId}`, { withCredentials: true });
  }
}
