import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class BookingService {
	private apiUrl = 'http://localhost:8080/api/bookings';

	constructor(private http: HttpClient) {}

	// Provider endpoints
	getBookingsByProvider(): Observable<any[]> {
		return this.http.get<any[]>(`${this.apiUrl}/provider/me`, { withCredentials: true });
	}

	providerCancelBooking(providerId: number, bookingId: number) {
		return this.http.put<any>(`${this.apiUrl}/provider/${providerId}/cancel/${bookingId}`, {}, { withCredentials: true });
	}

	providerEditBooking(providerId: number, bookingId: number, payload: any) {
		return this.http.put<any>(`${this.apiUrl}/provider/${providerId}/edit/${bookingId}`, payload, { withCredentials: true });
	}
}

