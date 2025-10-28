import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AdminDashboardService } from '../../services/admin-dashboard.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  dashboardData: any = {
    totalUsers: 0,
    totalProviders: 0,
    totalCustomers: 0,
    totalBookings: 0,
    totalReviews: 0,
    totalRevenue: 0,
    recentBookings: [],
    recentReviews: [],
    systemStats: {
      uptime: '99.9%',
      status: 'Online',
      lastBackup: new Date().toLocaleDateString()
    }
  };
  
  loading = false;
  errorMessage = '';

  constructor(private adminDashboardService: AdminDashboardService) {}

  ngOnInit() {
    this.loadDashboardData();
  }

  loadDashboardData() {
    this.loading = true;
    this.errorMessage = '';
    
    this.adminDashboardService.getDashboardData().subscribe({
      next: (data) => {
        this.dashboardData = { ...this.dashboardData, ...data };
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load dashboard data';
        this.loading = false;
        console.error('Error loading dashboard data:', error);
      }
    });
  }

  refreshData() {
    this.loadDashboardData();
  }

  getStatusColor(status: string): string {
    switch (status.toLowerCase()) {
      case 'online': return 'green';
      case 'offline': return 'red';
      case 'maintenance': return 'orange';
      default: return 'gray';
    }
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  }
}