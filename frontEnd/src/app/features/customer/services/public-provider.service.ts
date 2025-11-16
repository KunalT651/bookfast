import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';

export interface PublicProviderInfo {
  providerName: string;
  serviceCategory: string | null;
}

@Injectable({ providedIn: 'root' })
export class PublicProviderService {
  private apiUrl = `${environment.apiUrl}/providers`;

  constructor(private http: HttpClient) {}

  getPublicProvider(providerId: number): Observable<PublicProviderInfo> {
    return this.http
      .get<PublicProviderInfo>(`${this.apiUrl}/${providerId}/public`, { withCredentials: true })
      .pipe(
        map((res: any) => ({
          providerName: res?.providerName ?? '',
          serviceCategory: res?.serviceCategory ?? null
        })),
        catchError(() => of({ providerName: '', serviceCategory: null }))
      );
  }
}

