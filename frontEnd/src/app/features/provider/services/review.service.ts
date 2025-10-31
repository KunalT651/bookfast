import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ReviewService {
    private apiUrl = `${environment.apiUrl}/api/reviews`;

    constructor(private http: HttpClient) {}

    // Provider endpoints
    getReviewsByProvider(): Observable<any[]> {
        console.log('[ReviewService] Making GET request to:', `${this.apiUrl}/provider/me`);
        return this.http.get<any[]>(`${this.apiUrl}/provider/me`, { withCredentials: true });
    }

    deleteReviewByProvider(reviewId: number) {
        console.log('[ReviewService] Deleting review:', `${this.apiUrl}/provider/${reviewId}`);
        return this.http.delete(`${this.apiUrl}/provider/${reviewId}`, { withCredentials: true });
    }
}