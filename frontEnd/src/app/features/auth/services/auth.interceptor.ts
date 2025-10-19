import { HttpInterceptorFn } from '@angular/common/http';

export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
  // Only ensure withCredentials is set
  req = req.clone({ withCredentials: true });
  return next(req);
};