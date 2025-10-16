import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-provider-dashboard',
  standalone: true,
  imports: [RouterOutlet], // <-- Add this line
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class ProviderDashboardComponent {}