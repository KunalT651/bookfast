import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class BookingService {
	private apiUrl = `${environment.apiUrl}/bookings`;

	constructor(private http: HttpClient) {}

	// Provider endpoints
	getBookingsByProvider(): Observable<any[]> {
		console.log('[BookingService] Making GET request to:', `${this.apiUrl}/provider/me`);
		return this.http.get<any[]>(`${this.apiUrl}/provider/me`, { withCredentials: true });
	}


	providerEditBooking(providerId: number, bookingId: number, payload: any) {
		console.log('[BookingService] Editing booking:', `${this.apiUrl}/${bookingId}`, payload);
		return this.http.put<any>(`${this.apiUrl}/${bookingId}`, payload, { withCredentials: true });
	}

	deleteBooking(bookingId: number) {
		console.log('[BookingService] Deleting booking:', `${this.apiUrl}/${bookingId}`);
		return this.http.delete(`${this.apiUrl}/${bookingId}`, { withCredentials: true });
	}
}

