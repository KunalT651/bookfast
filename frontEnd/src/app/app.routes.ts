import { Routes } from '@angular/router';
import { RegisterComponent } from './features/auth/components/register/register.component';
import { RegisterProviderComponent } from './features/auth/components/register-provider/register-provider.component';
import { LoginComponent } from './features/auth/components/login/login.component';
import { CreateAdminComponent } from './features/auth/components/create-admin/create-admin.component';

import { DashboardComponent } from './features/admin/components/dashboard/dashboard.component';
import { ServiceCategoriesComponent } from './features/admin/components/service-categories/service-categories.component';
import { ProvidersComponent } from './features/admin/components/providers/providers.component';
import { DatabaseManagerComponent } from './features/admin/components/database-manager/database-manager.component';
import { PermanentCleanupComponent } from './features/admin/components/permanent-cleanup/permanent-cleanup.component';
import { ResourcesComponent } from './features/provider/components/resources/resources.component';
import { ProviderDashboardComponent } from './features/provider/components/dashboard/dashboard.component';
import { HomeComponent } from './features/customer/components/home/home.component';
import { BookingComponent } from './features/customer/components/booking/booking.component';
import { TestDataComponent } from './features/customer/components/test-data/test-data.component';
import { TestResourceComponent } from './features/customer/components/test-resource/test-resource.component';

export const routes: Routes = [
  { path: 'register', component: RegisterComponent },
  { path: 'register/provider', component: RegisterProviderComponent },
  { path: 'create-admin', component: CreateAdminComponent },
  { path: '', redirectTo: 'register', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },

  { path: 'admin/dashboard', component: DashboardComponent },
  { path: 'admin/services', component: ServiceCategoriesComponent },
  { path: 'admin/providers', component: ProvidersComponent },
  { path: 'admin/database', component: DatabaseManagerComponent },
  { path: 'admin/cleanup', component: PermanentCleanupComponent },

  {
    path: 'provider/dashboard',
    component: ProviderDashboardComponent,
    children: [
      { path: '', redirectTo: 'resources', pathMatch: 'full' },
      { path: 'resources', component: ResourcesComponent },
      // ...other child routes
    ]
  },
  { path: 'customer/home', component: HomeComponent },
  { path: 'customer/bookings', loadComponent: () => import('./features/customer/components/customer-bookings/customer-bookings.component').then(m => m.CustomerBookingsComponent) },
   { path: 'test-data', component: TestDataComponent },
   { path: 'test-resource', component: TestResourceComponent },

  { path: 'booking', component: BookingComponent },

  // ...other routes...
];