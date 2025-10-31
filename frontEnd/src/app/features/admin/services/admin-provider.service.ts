import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdminProviderService {
  private apiUrl = `${environment.apiUrl}/api/admin/providers`;

  constructor(private http: HttpClient) {}

  getAllProviders(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, { withCredentials: true });
  }

  getProviderById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }

  createProvider(providerData: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, providerData, { withCredentials: true });
  }

  updateProvider(id: number, providerData: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, providerData, { withCredentials: true });
  }

  deleteProvider(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }

  updateProviderStatus(id: number, isActive: boolean): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/${id}/status`, { isActive }, { withCredentials: true });
  }

  getProvidersByCategory(category: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/category/${encodeURIComponent(category)}`, { withCredentials: true });
  }

  searchProviders(query: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search?q=${encodeURIComponent(query)}`, { withCredentials: true });
  }

  getProviderStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`, { withCredentials: true });
  }
}