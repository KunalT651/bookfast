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
    console.log('[ProviderAnalyticsComponent] Loading analytics for period:', this.selectedPeriod);
    this.loading = true;
    this.errorMessage = '';

    this.analyticsService.getAnalytics(this.selectedPeriod).subscribe({
      next: (data: any) => {
        console.log('[ProviderAnalyticsComponent] Analytics loaded successfully:', data);
        this.analytics = data;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('[ProviderAnalyticsComponent] Error loading analytics:', error);
        if (error.error?.error) {
          this.errorMessage = error.error.error;
        } else if (error.error?.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'Failed to load analytics. Please try again.';
        }
        this.loading = false;
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

