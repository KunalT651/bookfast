import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DemoForm } from '../models/demo-form.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class DemoFormService {
  private apiUrl = `${environment.apiUrl}/api/demo`;

  constructor(private http: HttpClient) {}

  save(form: DemoForm): Observable<DemoForm> {
    return this.http.post<DemoForm>(this.apiUrl, form, { withCredentials: true });
  }

  getAll(): Observable<DemoForm[]> {
    return this.http.get<DemoForm[]>(this.apiUrl, { withCredentials: true });
  }
}