
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  req = req.clone({ withCredentials: true });

  // Attach CSRF token from cookie if present
  const getCookie = (name: string): string | null => {
    const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
    return match ? decodeURIComponent(match[2]) : null;
  };
  const xsrfToken = getCookie('XSRF-TOKEN');
  if (xsrfToken) {
    req = req.clone({
      setHeaders: { 'X-XSRF-TOKEN': xsrfToken }
    });
  }

  return next(req).pipe(
    catchError((error) => {
      if (error.status === 401 || error.status === 403) {
        // Optionally, show a message or clear user state here
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};