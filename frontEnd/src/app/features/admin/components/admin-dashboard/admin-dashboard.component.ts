import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SystemReportService } from '../../services/system-report.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
  imports: [CommonModule]
})
export class AdminDashboardComponent implements OnInit {
  reports: any = {};
  error: string = '';

  constructor(private systemReportService: SystemReportService) {}

  ngOnInit(): void {
    this.systemReportService.getReports().subscribe({
      next: (data) => {
        this.reports = data;
        this.error = '';
      },
      error: () => {
        this.error = 'Failed to load system reports.';
      }
    });
  }
}
