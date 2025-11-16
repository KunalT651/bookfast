import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app';

// Bootstrap application with production configuration
bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
