import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdminBookingService {
  private apiUrl = `${environment.apiUrl}/admin/bookings`;

  constructor(private http: HttpClient) {}

  getAllBookings(): Observable<any[]> {
    console.log('[AdminBookingService] Making GET request to:', this.apiUrl);
    return this.http.get<any[]>(this.apiUrl, { 
      withCredentials: true
    }).pipe(
      map((data: any) => {
        console.log('[AdminBookingService] Response received:', data);
        // Handle empty response
        if (!data) {
          console.log('[AdminBookingService] Empty response, returning empty array');
          return [];
        }
        // Handle direct array
        if (Array.isArray(data)) {
          console.log('[AdminBookingService] Response is array with', data.length, 'items');
          return data;
        }
        // Handle wrapped responses
        if (data && Array.isArray(data.data)) {
          return data.data;
        }
        if (data && Array.isArray(data.bookings)) {
          return data.bookings;
        }
        // If it's an object but not an array, log warning and return empty
        console.warn('[AdminBookingService] Unexpected response format:', typeof data, data);
        return [];
      })
    );
  }

  getBookingById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }

  updateBooking(id: number, bookingData: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, bookingData, { withCredentials: true });
  }

  deleteBooking(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }
}

