import { Routes } from '@angular/router';
import { authGuard } from './auth.guard';
import { RegisterComponent } from './features/auth/components/register/register.component';
import { RegisterProviderComponent } from './features/auth/components/register-provider/register-provider.component';
import { LoginComponent } from './features/auth/components/login/login.component';
import { AdminLoginComponent } from './features/auth/components/admin-login/admin-login.component';
import { CreateAdminComponent } from './features/auth/components/create-admin/create-admin.component';
import { SmartRedirectComponent } from './features/auth/components/smart-redirect/smart-redirect.component';
import { PasswordResetComponent } from './features/auth/components/password-reset/password-reset.component';

import { DashboardComponent } from './features/admin/components/dashboard/dashboard.component';
import { AdminDashboardComponent } from './features/admin/components/admin-dashboard/admin-dashboard.component';
import { ServiceCategoriesComponent } from './features/admin/components/service-categories/service-categories.component';
import { ProvidersComponent } from './features/admin/components/providers/providers.component';
import { DatabaseManagerComponent } from './features/admin/components/database-manager/database-manager.component';
import { PermanentCleanupComponent } from './features/admin/components/permanent-cleanup/permanent-cleanup.component';
import { ResourcesComponent } from './features/provider/components/resources/resources.component';
import { ProviderDashboardComponent } from './features/provider/components/dashboard/dashboard.component';
import { ProviderProfileComponent } from './features/provider/components/profile/profile.component'; // Import the refactored component
import { ProviderBookingsComponent } from './features/provider/components/provider-bookings/provider-bookings.component';
import { ProviderReviewsComponent } from './features/provider/components/provider-reviews/provider-reviews.component';
import { GoogleCalendarSyncComponent } from './features/provider/components/google-calendar-sync/google-calendar-sync.component';
import { UnavailableDatesComponent } from './features/provider/components/unavailable-dates/unavailable-dates.component';
import { ProviderAnalyticsComponent } from './features/provider/components/analytics/analytics.component';
import { ProviderEarningsComponent } from './features/provider/components/earnings/earnings.component';
import { AdminCustomersComponent } from './features/admin/components/admin-customers/admin-customers.component';
import { AdminBookingsComponent } from './features/admin/components/admin-bookings/admin-bookings.component';
import { AdminUsersComponent } from './features/admin/components/admin-users/admin-users.component';
import { AdminProvidersComponent } from './features/admin/components/admin-providers/admin-providers.component';
import { AdminReportsComponent } from './features/admin/components/admin-reports/admin-reports.component';
import { HomeComponent } from './features/customer/components/home/home.component';
import { BookingComponent } from './features/customer/components/booking/booking.component';
import { MyReviewsComponent } from './features/customer/components/my-reviews/my-reviews.component';
import { CustomerProfileComponent } from './features/customer/components/customer-profile/customer-profile.component';
import { TestDataComponent } from './features/customer/components/test-data/test-data.component';
import { TestResourceComponent } from './features/customer/components/test-resource/test-resource.component';
import { CalendarSyncComponent } from './features/customer/components/calendar-sync/calendar-sync.component';
import { CalendarCallbackComponent } from './features/auth/components/calendar-callback/calendar-callback.component';

export const routes: Routes = [
  { path: 'registration', component: RegisterComponent },
  { path: 'provider/registration', component: RegisterProviderComponent },
  { path: 'create-admin', component: CreateAdminComponent },
  { path: '', component: SmartRedirectComponent },
  { path: 'login', component: LoginComponent },
  { path: 'admin/login', component: AdminLoginComponent },
  { path: 'password-reset', component: PasswordResetComponent },

  { path: 'admin/dashboard', component: AdminDashboardComponent, canActivate: [authGuard] },
  { path: 'admin/services', component: ServiceCategoriesComponent, canActivate: [authGuard] },
  { path: 'admin/providers', component: AdminProvidersComponent, canActivate: [authGuard] },
  { path: 'admin/users', component: AdminUsersComponent, canActivate: [authGuard] },
  { path: 'admin/customers', component: AdminCustomersComponent, canActivate: [authGuard] },
  { path: 'admin/bookings', component: AdminBookingsComponent, canActivate: [authGuard] },
  { path: 'admin/reports', component: AdminReportsComponent, canActivate: [authGuard] },
  { path: 'admin/database', component: DatabaseManagerComponent, canActivate: [authGuard] },
  { path: 'admin/cleanup', component: PermanentCleanupComponent, canActivate: [authGuard] },

  { path: 'provider/dashboard', component: ProviderDashboardComponent, canActivate: [authGuard] },
  { path: 'provider/dashboard/resources', component: ResourcesComponent, canActivate: [authGuard] },
  { path: 'provider/dashboard/profile', component: ProviderProfileComponent, canActivate: [authGuard] },
  { path: 'provider/dashboard/bookings', component: ProviderBookingsComponent, canActivate: [authGuard] },
  { path: 'provider/dashboard/reviews', component: ProviderReviewsComponent, canActivate: [authGuard] },
  { path: 'provider/dashboard/calendar-sync', component: GoogleCalendarSyncComponent, canActivate: [authGuard] },
  { path: 'provider/dashboard/unavailable-dates', component: UnavailableDatesComponent, canActivate: [authGuard] },
  { path: 'provider/dashboard/analytics', component: ProviderAnalyticsComponent, canActivate: [authGuard] },
  { path: 'provider/dashboard/earnings', component: ProviderEarningsComponent, canActivate: [authGuard] },
  { path: 'customer/home', component: HomeComponent, canActivate: [authGuard] },
  { path: 'customer/reviews', component: MyReviewsComponent, canActivate: [authGuard] },
  { path: 'customer/bookings', loadComponent: () => import('./features/customer/components/customer-bookings/customer-bookings.component').then(m => m.CustomerBookingsComponent), canActivate: [authGuard] },
  { path: 'customer/profile/edit', component: CustomerProfileComponent, canActivate: [authGuard] },
  { path: 'customer/calendar-sync', component: CalendarSyncComponent, canActivate: [authGuard] },
  { path: 'calendar/callback', component: CalendarCallbackComponent },
  { path: 'callback', component: CalendarCallbackComponent },
  { path: 'test-data', component: TestDataComponent, canActivate: [authGuard] },
  { path: 'test-resource', component: TestResourceComponent, canActivate: [authGuard] },

  { path: 'booking', component: BookingComponent, canActivate: [authGuard] },

  // ...other routes...
];