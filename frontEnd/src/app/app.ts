import { Component } from '@angular/core';
import { DemoFormComponent } from './components/demo-form/demo-form.component';
import { RouterOutlet } from '@angular/router'; // AddedByKunal - Needed for routing in standalone components

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [DemoFormComponent, RouterOutlet], //AddedByKunal - Whenever components are standalone, they need to be imported
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class AppComponent {}
