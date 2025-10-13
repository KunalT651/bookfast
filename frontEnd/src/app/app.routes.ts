import { Routes } from '@angular/router';
import { RegisterComponent } from './features/auth/components/register/register.component';
import { RegisterProviderComponent } from './features/auth/components/register-provider/register-provider.component';
import { LoginComponent } from './features/auth/components/login/login.component';

export const routes: Routes = [
  { path: 'register', component: RegisterComponent },
  { path: 'register/provider', component: RegisterProviderComponent },
  // ...other routes...
  { path: '', redirectTo: 'register', pathMatch: 'full' }, // Make register the default page
  { path: 'login', component: LoginComponent },
];