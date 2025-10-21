import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Booking } from '../models/booking.model';

@Injectable({ providedIn: 'root' })
export class BookingService {
	private apiUrl = '/api/bookings';

	constructor(private http: HttpClient) {}

		createBooking(booking: Booking): Observable<Booking> {
			return this.http.post<Booking>(this.apiUrl, booking);
		}

		getBookingsByCustomer(customerId: number): Observable<Booking[]> {
			return this.http.get<Booking[]>(`${this.apiUrl}/customer/${customerId}`);
		}

		deleteBooking(bookingId: number): Observable<any> {
			return this.http.delete(`${this.apiUrl}/${bookingId}`);
		}
}
