import { Routes } from '@angular/router';
import { RegisterComponent } from './features/auth/components/register/register.component';
import { RegisterProviderComponent } from './features/auth/components/register-provider/register-provider.component';
import { LoginComponent } from './features/auth/components/login/login.component';

import { DashboardComponent } from './features/admin/components/dashboard/dashboard.component';
import { ServiceCategoriesComponent } from './features/admin/components/service-categories/service-categories.component';
import { ProvidersComponent } from './features/admin/components/providers/providers.component';
import { ResourcesComponent } from './features/provider/components/resources/resources.component';
import { ProviderDashboardComponent } from './features/provider/components/dashboard/dashboard.component';

export const routes: Routes = [
  { path: 'register', component: RegisterComponent },
  { path: 'register/provider', component: RegisterProviderComponent },
  { path: '', redirectTo: 'register', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },

  { path: 'admin/dashboard', component: DashboardComponent },
  { path: 'admin/services', component: ServiceCategoriesComponent },
  { path: 'admin/providers', component: ProvidersComponent },

  {
    path: 'provider/dashboard',
    component: ProviderDashboardComponent,
    children: [
      { path: '', redirectTo: 'resources', pathMatch: 'full' },
      { path: 'resources', component: ResourcesComponent },
      // ...other child routes
    ]
  }
  // ...other routes...
];