import { Injectable } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { UserStateService } from './shared/services/user-state.service';
import { inject } from '@angular/core';

// Only protect routes that require authentication
export const authGuard: CanActivateFn = (route, state) => {
  const userState = inject(UserStateService);
  const router = inject(Router);
  let loggedIn = false;
  userState.getLoggedIn().subscribe(val => loggedIn = val);

  // Public routes are always accessible
    const publicRoutes = ['/', '/login', '/registration', '/provider/registration'];
    const isPublic = publicRoutes.some(publicRoute => state.url.startsWith(publicRoute));
  console.log('[authGuard] state.url:', state.url, 'isPublic:', isPublic);
  if (isPublic) {
    return true;
  }

  // Only protect non-public routes
  if (!loggedIn) {
    console.log('[authGuard] Not logged in, redirecting to /login');
    return router.parseUrl('/login');
  }
  return true;
};
