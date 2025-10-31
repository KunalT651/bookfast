
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
      if (error.status === 401) {
        // Unauthorized - redirect to login
        const message = error.error?.message || '';
        if (message.toLowerCase().includes('expired') || message.toLowerCase().includes('invalid token') || message.toLowerCase().includes('unauthorized')) {
          router.navigate(['/login']);
        }
      } else if (error.status === 403) {
        // Forbidden - user is authenticated but doesn't have permission
        const publicRoutes = ['/login', '/registration', '/provider/registration', '/create-admin'];
        const currentUrl = router.url;
        const message = error.error?.message || error.error?.error || '';
        
        // Only log meaningful errors (suppress generic "OK" messages)
        if (message && message !== 'OK' && error.statusText !== 'OK' && !publicRoutes.includes(currentUrl)) {
          console.warn(`[AuthInterceptor] 403 Forbidden on ${req.method} ${req.url}:`, message);
        }
        // Silently handle 403 errors - they're expected for unauthorized access attempts
      }
      return throwError(() => error);
    })
  );
};