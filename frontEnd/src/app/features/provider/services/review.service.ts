import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private apiUrl = 'http://localhost:8080/api/reviews';

  getReviewsByResourceId(resourceId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/resource/${resourceId}`, { withCredentials: true });
  }

  constructor(private http: HttpClient) {}
}
