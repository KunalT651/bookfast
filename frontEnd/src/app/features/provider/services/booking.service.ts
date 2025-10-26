import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class BookingService {
	private apiUrl = 'http://localhost:8080/api/bookings';

	constructor(private http: HttpClient) {}

	// Provider endpoints
	getBookingsByProvider(): Observable<any[]> {
		console.log('[BookingService] Making GET request to:', `${this.apiUrl}/provider/me`);
		return this.http.get<any[]>(`${this.apiUrl}/provider/me`, { withCredentials: true });
	}


	providerEditBooking(providerId: number, bookingId: number, payload: any) {
		console.log('[BookingService] Editing booking:', `${this.apiUrl}/provider/${providerId}/edit/${bookingId}`, payload);
		return this.http.put<any>(`${this.apiUrl}/provider/${providerId}/edit/${bookingId}`, payload, { withCredentials: true });
	}

	deleteBooking(bookingId: number) {
		console.log('[BookingService] Deleting booking:', `${this.apiUrl}/${bookingId}`);
		return this.http.delete(`${this.apiUrl}/${bookingId}`, { withCredentials: true });
	}
}

