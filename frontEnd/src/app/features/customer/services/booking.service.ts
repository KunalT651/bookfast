import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, pipe } from 'rxjs';
import { Booking } from '../models/booking.model';
import { tap } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class BookingService {
	private apiUrl = `${environment.apiUrl}/bookings`;

	constructor(private http: HttpClient) {}

		createBooking(booking: any): Observable<any> {
			return this.http.post<any>(this.apiUrl, booking, { withCredentials: true });
		}

			createMultiSlotBooking(payload: any): Observable<Booking[]> {
				return this.http.post<Booking[]>(`${this.apiUrl}/multi`, payload);
			}

		   getBookingsByCustomer(customerId: number): Observable<Booking[]> {
			   const url = `${this.apiUrl}/customer/${customerId}`;
			   console.log('[BookingService] Fetching bookings from:', url);
			   console.log('[BookingService] Full API URL:', this.apiUrl);
			   console.log('[BookingService] Customer ID:', customerId);
			   return this.http.get<Booking[]>(url, { withCredentials: true }).pipe(
				   tap({
					   next: (data) => console.log('[BookingService] Bookings response received:', data?.length || 0, data),
					   error: (err) => console.error('[BookingService] Error fetching bookings:', err)
				   })
			   );
		   }

		   deleteBooking(bookingId: number): Observable<any> {
			   return this.http.delete(`${this.apiUrl}/${bookingId}`, { withCredentials: true });
		   }

	   cancelBooking(bookingId: number): Observable<Booking> {
	     return this.http.put<Booking>(`${this.apiUrl}/${bookingId}/cancel`, {}, { withCredentials: true });
	   }

		createPaymentIntent(data: { amount: number }): Observable<{ clientSecret: string }> {
			return this.http.post<{ clientSecret: string }>(`${environment.apiUrl}/api/payments/create-intent`, data, { withCredentials: true })
				.pipe(
					tap({
						next: (response) => console.log('PaymentIntent response:', response),
						error: (err) => console.error('PaymentIntent error:', err)
					})
				);
		}
}
