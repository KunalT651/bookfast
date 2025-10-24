import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProviderService } from '../../services/provider.service';

@Component({
  selector: 'app-provider-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProviderProfileComponent implements OnInit {
  profileForm: FormGroup;
  uploading = false;
  uploadError: string | null = null;
  successMessage: string | null = null;
  errorMessage: string | null = null;
  profilePictureUrl: string | null = null; // New property for profile picture URL

  constructor(private providerService: ProviderService, private fb: FormBuilder) {
    this.profileForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit() {
    this.loadProfile();
  }

  loadProfile() {
    this.providerService.getProviderProfileForCurrentUser().subscribe({
      next: (profile) => {
        this.profileForm.patchValue({
          firstName: profile.firstName,
          lastName: profile.lastName,
          email: profile.email
        });
        this.profilePictureUrl = profile.imageUrl || null; // Populate the new property
      },
      error: (err) => {
        console.error('Failed to load provider profile', err);
        this.errorMessage = 'Failed to load profile.';
      }
    });
  }

  onSubmit() {
    this.successMessage = null;
    this.errorMessage = null;

    if (this.profileForm.invalid) {
      this.errorMessage = 'Please fill in all required fields correctly.';
      return;
    }

    this.providerService.updateProviderProfile(this.profileForm.value).subscribe({
      next: (res) => {
        this.successMessage = res.message || 'Profile updated successfully!';
        this.profileForm.patchValue({
          firstName: res.firstName,
          lastName: res.lastName,
          email: res.email
        });
      },
      error: (err) => {
        console.error('Profile update failed', err);
        this.errorMessage = err.error?.message || 'Failed to update profile.';
      }
    });
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.uploading = true;
      this.uploadError = null;
      this.successMessage = null;

      this.providerService.uploadProfilePicture(file).subscribe({
        next: (res) => {
          this.profilePictureUrl = res.url; // Update the profile picture URL upon successful upload
          this.successMessage = 'Profile picture uploaded successfully!';
          this.uploading = false;
        },
        error: () => {
          this.uploadError = 'Upload failed.';
          this.uploading = false;
        }
      });
    }
  }
}
