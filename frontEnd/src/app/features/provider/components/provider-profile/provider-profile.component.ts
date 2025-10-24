import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ProviderService } from '../../services/provider.service';

@Component({
  selector: 'app-provider-profile',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './provider-profile.component.html',
  styleUrls: ['./provider-profile.component.css']
})
export class ProviderProfileComponent implements OnInit {
  profileForm: FormGroup;
  success = '';
  error = '';

  constructor(private fb: FormBuilder, private providerService: ProviderService) {
    this.profileForm = this.fb.group({
      first_name: ['', Validators.required],
      last_name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.providerService.getProviderProfileForCurrentUser().subscribe({
      next: (profile: any) => {
        this.profileForm.patchValue({
          first_name: profile.first_name || '',
          last_name: profile.last_name || '',
          email: profile.email || '',
          password: '' // Do not prefill password for security
        });
      },
      error: () => {
        this.error = 'Failed to load profile';
      }
    });
  }

  onSubmit() {
    this.success = '';
    this.error = '';
    if (this.profileForm.invalid) return;
    this.providerService.updateProviderProfile(this.profileForm.value).subscribe({
      next: () => {
        this.success = 'Profile updated successfully!';
      },
      error: () => {
        this.error = 'Failed to update profile';
      }
    });
  }
}
