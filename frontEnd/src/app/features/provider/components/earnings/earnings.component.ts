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
    this.loading = true;
    this.errorMessage = '';

    this.analyticsService.getEarnings(this.selectedPeriod).subscribe({
      next: (data: any) => {
        this.earnings = data;
        this.loading = false;
      },
      error: (error: any) => {
        this.errorMessage = 'Failed to load earnings data.';
        this.loading = false;
        console.error('Error loading earnings:', error);
      }
    });
  }

  onPeriodChange() {
    this.loadEarnings();
  }
}

