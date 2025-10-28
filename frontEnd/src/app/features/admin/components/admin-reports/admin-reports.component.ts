import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminReportService } from '../../services/admin-report.service';

@Component({
  selector: 'app-admin-reports',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-reports.component.html',
  styleUrls: ['./admin-reports.component.css']
})
export class AdminReportsComponent implements OnInit {
  reports: any = {};
  loading = false;
  errorMessage = '';
  selectedPeriod = '30'; // days
  selectedReportType = 'overview';

  constructor(private adminReportService: AdminReportService) {}

  ngOnInit() {
    this.loadReports();
  }

  loadReports() {
    this.loading = true;
    this.errorMessage = '';
    
    this.adminReportService.getSystemReports(this.selectedPeriod).subscribe({
      next: (reports) => {
        this.reports = reports;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load system reports';
        this.loading = false;
        console.error('Error loading reports:', error);
      }
    });
  }

  onPeriodChange() {
    this.loadReports();
  }

  onReportTypeChange() {
    // Filter or reload specific report type
    this.loadReports();
  }

  exportReport(reportType: string) {
    this.adminReportService.exportReport(reportType, this.selectedPeriod).subscribe({
      next: (data) => {
        // Create download link
        const blob = new Blob([data], { type: 'application/vnd.ms-excel' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `${reportType}_report_${new Date().toISOString().split('T')[0]}.xlsx`;
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        this.errorMessage = 'Failed to export report';
        console.error('Error exporting report:', error);
      }
    });
  }

  refreshReports() {
    this.loadReports();
  }
}
