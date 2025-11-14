import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProviderService } from '../../services/provider.service';

@Component({
  selector: 'app-unavailable-dates',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './unavailable-dates.component.html',
  styleUrls: ['./unavailable-dates.component.css']
})
export class UnavailableDatesComponent implements OnInit {
  unavailableDates: any[] = [];
  showAddForm = false;
  addForm: FormGroup;
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private providerService: ProviderService,
    private fb: FormBuilder
  ) {
    this.addForm = this.fb.group({
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      reason: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.loadUnavailableDates();
  }

  loadUnavailableDates() {
    this.loading = true;
    this.providerService.getUnavailableDates().subscribe({
      next: (dates: any) => {
        this.unavailableDates = dates;
        this.loading = false;
      },
      error: (error: any) => {
        this.errorMessage = 'Failed to load unavailable dates';
        this.loading = false;
        console.error('Error loading unavailable dates:', error);
      }
    });
  }

  toggleAddForm() {
    this.showAddForm = !this.showAddForm;
    if (this.showAddForm) {
      this.addForm.reset();
    }
  }

  onSubmit() {
    if (this.addForm.invalid) {
      this.errorMessage = 'Please fill in all required fields';
      return;
    }

    const formValue = this.addForm.value;
    
    // Validate date range
    if (new Date(formValue.startDate) > new Date(formValue.endDate)) {
      this.errorMessage = 'End date must be after start date';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.providerService.markUnavailableDates(
      formValue.startDate,
      formValue.endDate,
      formValue.reason
    ).subscribe({
      next: (response: any) => {
        this.successMessage = 'Unavailable dates marked successfully!';
        this.loadUnavailableDates();
        this.showAddForm = false;
        this.addForm.reset();
        this.loading = false;
      },
      error: (error: any) => {
        this.errorMessage = error.error?.error || 'Failed to mark unavailable dates';
        this.loading = false;
        console.error('Error marking unavailable dates:', error);
      }
    });
  }

  removeUnavailableDate(id: number) {
    if (!confirm('Are you sure you want to remove this unavailable period?')) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.providerService.removeUnavailableDate(id).subscribe({
      next: (response: any) => {
        this.successMessage = 'Unavailable period removed successfully!';
        this.loadUnavailableDates();
        this.loading = false;
      },
      error: (error: any) => {
        this.errorMessage = error.error?.error || 'Failed to remove unavailable period';
        this.loading = false;
        console.error('Error removing unavailable date:', error);
      }
    });
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  getDuration(startDate: string, endDate: string): string {
    const start = new Date(startDate);
    const end = new Date(endDate);
    const diffTime = Math.abs(end.getTime() - start.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
    return diffDays === 1 ? '1 day' : `${diffDays} days`;
  }
}
