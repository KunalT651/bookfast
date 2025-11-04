

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Review } from '../models/review.model';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private apiUrl = `${environment.apiUrl}/reviews`;

  constructor(private http: HttpClient) {}

  deleteReview(resourceId: number, reviewId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/resource/${resourceId}/${reviewId}`, { withCredentials: true });
  }

  getReviews(resourceId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/resource/${resourceId}`, { withCredentials: true });
  }

  getAverageRating(resourceId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/resource/${resourceId}/rating`, { withCredentials: true });
  }

  addReview(resourceId: number, review: Review): Observable<Review> {
    return this.http.post<Review>(`${this.apiUrl}/resource/${resourceId}`, review, { withCredentials: true });
  }

  getReviewsByCustomer(customerId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/customer/${customerId}`, { withCredentials: true });
  }

  updateReview(reviewId: number, review: Review): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${reviewId}`, review, { withCredentials: true });
  }
}
