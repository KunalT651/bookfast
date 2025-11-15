import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProviderAnalyticsService } from '../../services/analytics.service';

@Component({
  selector: 'app-provider-earnings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './earnings.component.html',
  styleUrls: ['./earnings.component.css']
})
export class ProviderEarningsComponent implements OnInit {
  earnings: any = null;
  loading = false;
  errorMessage = '';
  selectedPeriod = '30';

  constructor(private analyticsService: ProviderAnalyticsService) {}

  ngOnInit() {
    this.loadEarnings();
  }

  loadEarnings() {
    console.log('[ProviderEarningsComponent] Loading earnings for period:', this.selectedPeriod);
    this.loading = true;
    this.errorMessage = '';

    this.analyticsService.getEarnings(this.selectedPeriod).subscribe({
      next: (data: any) => {
        console.log('[ProviderEarningsComponent] Earnings loaded successfully:', data);
        this.earnings = data;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('[ProviderEarningsComponent] Error loading earnings:', error);
        if (error.error?.error) {
          this.errorMessage = error.error.error;
        } else if (error.error?.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'Failed to load earnings data. Please try again.';
        }
        this.loading = false;
      }
    });
  }

  onPeriodChange() {
    this.loadEarnings();
  }
}

