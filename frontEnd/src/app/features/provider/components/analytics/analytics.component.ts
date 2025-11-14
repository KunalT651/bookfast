import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProviderAnalyticsService } from '../../services/analytics.service';

@Component({
  selector: 'app-provider-analytics',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.css']
})
export class ProviderAnalyticsComponent implements OnInit {
  analytics: any = null;
  loading = false;
  errorMessage = '';
  selectedPeriod = '30';

  constructor(private analyticsService: ProviderAnalyticsService) {}

  ngOnInit() {
    this.loadAnalytics();
  }

  loadAnalytics() {
    this.loading = true;
    this.errorMessage = '';

    this.analyticsService.getAnalytics(this.selectedPeriod).subscribe({
      next: (data: any) => {
        this.analytics = data;
        this.loading = false;
        console.log('✅ Analytics loaded:', data);
      },
      error: (error: any) => {
        this.errorMessage = 'Failed to load analytics. Please try again.';
        this.loading = false;
        console.error('Error loading analytics:', error);
      }
    });
  }

  onPeriodChange() {
    this.loadAnalytics();
  }

  refreshAnalytics() {
    this.loadAnalytics();
  }
}

