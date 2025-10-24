
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
  const jwtToken = getCookie('jwt');
  let headers: { [key: string]: string } = {};
  if (xsrfToken) {
    headers['X-XSRF-TOKEN'] = xsrfToken;
  }
  if (jwtToken) {
    headers['Authorization'] = `Bearer ${jwtToken}`;
  }
  if (Object.keys(headers).length > 0) {
    req = req.clone({ setHeaders: headers });
  }

  return next(req).pipe(
    catchError((error) => {
      if (error.status === 401 || error.status === 403) {
        // Only redirect to login if backend explicitly says session expired
        const message = error.error?.message || '';
  const publicRoutes = ['/login', '/registration', '/provider/registration', '/create-admin'];
        const currentUrl = router.url;
        if (message.toLowerCase().includes('expired') || message.toLowerCase().includes('invalid token')) {
          router.navigate(['/login']);
        } else if (!publicRoutes.includes(currentUrl)) {
          // Only show warning for protected routes
          console.warn('Access denied:', message || error.statusText);
        }
      }
      return throwError(() => error);
    })
  );
};