import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app';
import { NavbarComponent } from './shared/components/navbar.component';
import { AdminProvidersComponent } from './features/admin/components/admin-providers/admin-providers.component';
import { RegisterComponent } from './features/auth/components/register/register.component';
import { RegisterProviderComponent } from './features/auth/components/register-provider/register-provider.component';

@NgModule({
  declarations: [],
  imports: [
    BrowserModule,
    CommonModule,
    FormsModule,
    HttpClientModule,
    RouterOutlet,
  AppComponent,
  NavbarComponent,
  AdminProvidersComponent,
  RegisterComponent,
  RegisterProviderComponent
  ],
  providers: [],
  // Remove bootstrap array for standalone component
})
export class AppModule {}
