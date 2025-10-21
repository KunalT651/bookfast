import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AvailabilitySlot } from '../models/availability-slot.model';

@Injectable({ providedIn: 'root' })
export class AvailabilitySlotService {
  private apiUrl = 'http://localhost:8080/api/availability-slots';

  constructor(private http: HttpClient) {}

  getSlotsForResource(resourceId: number): Observable<AvailabilitySlot[]> {
    return this.http.get<AvailabilitySlot[]>(`${this.apiUrl}/resource/${resourceId}`);
  }

  addSlot(resourceId: number, slot: AvailabilitySlot): Observable<AvailabilitySlot> {
    return this.http.post<AvailabilitySlot>(`${this.apiUrl}/resource/${resourceId}`, slot);
  }

  deleteSlot(slotId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${slotId}`);
  }
}
