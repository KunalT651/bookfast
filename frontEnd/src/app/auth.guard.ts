import { Injectable } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { UserStateService } from './shared/services/user-state.service';
import { inject } from '@angular/core';

// Only protect routes that require authentication
export const authGuard: CanActivateFn = (route, state) => {
  const userState = inject(UserStateService);
  const router = inject(Router);
  let loggedIn = false;
  let currentUser: any = null;
  
  userState.getLoggedIn().subscribe(val => loggedIn = val);
  userState.getUser().subscribe(user => currentUser = user);

  // Public routes are always accessible
  const publicRoutes = ['/', '/login', '/registration', '/provider/registration'];
  const isAdminLogin = state.url === '/admin/login';
  const isPublic = publicRoutes.some(publicRoute => state.url === publicRoute || state.url.startsWith(publicRoute + '/')) || isAdminLogin;
  console.log('[authGuard] state.url:', state.url, 'isPublic:', isPublic, 'isAdminLogin:', isAdminLogin);

  if (isPublic) {
    return true;
  }

  // Check if user is logged in
  if (!loggedIn) {
    console.log('[authGuard] Not logged in, redirecting to /login');
    return router.parseUrl('/login');
  }

  // Role-based access control
  const userRole = currentUser?.role?.name;
  console.log('[authGuard] User role:', userRole, 'URL:', state.url);

  // Admin routes - require ADMIN role
  if (state.url.startsWith('/admin')) {
    if (userRole !== 'ADMIN') {
      console.log('[authGuard] Admin access denied, redirecting to /login');
      return router.parseUrl('/login');
    }
  }

  // Provider routes - require PROVIDER role
  if (state.url.startsWith('/provider')) {
    if (userRole !== 'PROVIDER') {
      console.log('[authGuard] Provider access denied, redirecting to /login');
      return router.parseUrl('/login');
    }
  }

  // Customer routes - require CUSTOMER role
  if (state.url.startsWith('/customer')) {
    if (userRole !== 'CUSTOMER') {
      console.log('[authGuard] Customer access denied, redirecting to /login');
      return router.parseUrl('/login');
    }
  }

  return true;
};
