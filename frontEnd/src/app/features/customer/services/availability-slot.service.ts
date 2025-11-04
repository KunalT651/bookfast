import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AvailabilitySlot } from '../models/availability-slot.model';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AvailabilitySlotService {
  private apiUrl = `${environment.apiUrl}/api/resources`;

  constructor(private http: HttpClient) {}

  getSlotsForResource(resourceId: number): Observable<AvailabilitySlot[]> {
    return this.http.get<AvailabilitySlot[]>(`${this.apiUrl}/${resourceId}/availability`);
  }

  addSlot(resourceId: number, slot: AvailabilitySlot): Observable<AvailabilitySlot> {
    return this.http.post<AvailabilitySlot>(`${this.apiUrl}/${resourceId}/availability`, slot);
  }

  deleteSlot(slotId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/availability/${slotId}`);
  }
}
