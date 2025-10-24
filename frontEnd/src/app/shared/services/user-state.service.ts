import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class UserStateService {
  private loggedIn$ = new BehaviorSubject<boolean>(false);
  private user$ = new BehaviorSubject<any>(null);

  setLoggedIn(loggedIn: boolean) {
    console.log('[UserStateService] Setting loggedIn:', loggedIn);
    this.loggedIn$.next(loggedIn);
  }

  setUser(user: any) {
    console.log('[UserStateService] Setting user:', user);
    this.user$.next(user);
    this.setLoggedIn(!!user);
  }

  getLoggedIn(): Observable<boolean> {
    return this.loggedIn$.asObservable();
  }

  getUser(): Observable<any> {
    return this.user$.asObservable();
  }

  clear() {
    this.user$.next(null);
    this.loggedIn$.next(false);
  }
}
