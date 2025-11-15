import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProviderService } from '../../services/provider.service';
import { ProfilePictureService } from '../../../../shared/services/profile-picture.service';
import { environment } from '../../../../../environments/environment';

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
  loading = false;
  uploadError: string | null = null;
  successMessage: string | null = null;
  errorMessage: string | null = null;
  profilePictureUrl: string | null = null;
  profilePictureFullUrl: string | null = null;

  constructor(
    private providerService: ProviderService, 
    private fb: FormBuilder,
    private profilePictureService: ProfilePictureService
  ) {
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
    console.log('[ProviderProfileComponent] Loading profile...');
    this.loading = true;
    this.errorMessage = null;
    
    this.providerService.getProviderProfileForCurrentUser().subscribe({
      next: (profile) => {
        console.log('[ProviderProfileComponent] Profile loaded:', profile);
        this.profileForm.patchValue({
          firstName: profile.firstName,
          lastName: profile.lastName,
          email: profile.email
        });
        this.profilePictureUrl = profile.imageUrl || null;
        this.profilePictureFullUrl = this.constructImageUrl(profile.imageUrl);
        this.loading = false;
      },
      error: (err) => {
        console.error('[ProviderProfileComponent] Failed to load profile:', err);
        this.errorMessage = 'Failed to load profile.';
        this.loading = false;
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

    console.log('[ProviderProfileComponent] Updating profile...');
    this.loading = true;
    
    this.providerService.updateProviderProfile(this.profileForm.value).subscribe({
      next: (res) => {
        console.log('[ProviderProfileComponent] Profile updated successfully:', res);
        this.successMessage = res.message || 'Profile updated successfully!';
        this.profileForm.patchValue({
          firstName: res.firstName,
          lastName: res.lastName,
          email: res.email
        });
        this.loading = false;
      },
      error: (err) => {
        console.error('[ProviderProfileComponent] Profile update failed:', err);
        if (err.error?.error) {
          this.errorMessage = err.error.error;
        } else if (err.error?.message) {
          this.errorMessage = err.error.message;
        } else {
          this.errorMessage = 'Failed to update profile. Please try again.';
        }
        this.loading = false;
      }
    });
  }

  constructImageUrl(imageUrl: string | null | undefined): string | null {
    if (!imageUrl) {
      console.log('[ProviderProfileComponent] No imageUrl provided');
      return null;
    }
    // If it's already a full URL (http/https), return as is (deployment-ready)
    if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
      console.log('[ProviderProfileComponent] Image URL is already full URL (deployment-ready):', imageUrl);
      return imageUrl;
    }
    // Legacy support: Handle old relative URLs from local storage
    // Old format: /uploads/profile-pictures/... needs to point to backend server
    const baseUrl = environment.apiUrl.replace('/api', '');
    console.log('[ProviderProfileComponent] Constructing URL from relative path. Base URL:', baseUrl, 'Original URL:', imageUrl);
    const imagePath = imageUrl.startsWith('/') ? imageUrl : '/' + imageUrl;
    const fullUrl = baseUrl + imagePath;
    console.log('[ProviderProfileComponent] Constructed full image URL:', fullUrl);
    // Verify it's an absolute URL
    if (!fullUrl.startsWith('http://') && !fullUrl.startsWith('https://')) {
      console.error('[ProviderProfileComponent] ERROR: Constructed URL is not absolute!', fullUrl);
      // Fallback: use environment API URL
      const baseUrl = environment.apiUrl.replace('/api', '');
      return baseUrl + imagePath;
    }
    return fullUrl;
  }

  getProfileImageUrl(): string {
    return this.profilePictureFullUrl || '';
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (!file) {
      return;
    }

    // Validate file type - only PNG and JPG allowed
    const allowedTypes = ['image/png', 'image/jpeg', 'image/jpg'];
    const fileExtension = file.name.toLowerCase().split('.').pop();
    const allowedExtensions = ['png', 'jpg', 'jpeg'];
    
    const isValidType = allowedTypes.includes(file.type) || allowedExtensions.includes(fileExtension);
    
    if (!isValidType) {
      console.error('[ProviderProfileComponent] Invalid file type:', file.type, fileExtension);
      this.uploadError = 'Only PNG and JPG files are allowed.';
      event.target.value = ''; // Clear the file input
      return;
    }

    // Validate file size (optional - max 5MB)
    const maxSize = 5 * 1024 * 1024; // 5MB in bytes
    if (file.size > maxSize) {
      console.error('[ProviderProfileComponent] File too large:', file.size);
      this.uploadError = 'File size must be less than 5MB.';
      event.target.value = ''; // Clear the file input
      return;
    }

    console.log('[ProviderProfileComponent] Uploading profile picture...', {
      name: file.name,
      type: file.type,
      size: file.size
    });
    this.uploading = true;
    this.uploadError = null;
    this.successMessage = null;

    this.providerService.uploadProfilePicture(file).subscribe({
      next: (res) => {
        console.log('[ProviderProfileComponent] Profile picture uploaded successfully:', res);
        // res.url is the full URL from Imgur (deployment-ready)
        const imageUrl = res.url;
        this.profilePictureUrl = imageUrl;
        this.profilePictureFullUrl = this.constructImageUrl(imageUrl);
        this.successMessage = 'Profile picture uploaded successfully!';
        this.uploading = false;
        // Notify navbar to refresh immediately with the new image URL from database
        this.profilePictureService.notifyProfilePictureUpdated(imageUrl);
        // Reload profile to get updated imageUrl from database
        this.loadProfile();
      },
      error: (err) => {
        console.error('[ProviderProfileComponent] Profile picture upload failed:', err);
        if (err.error?.error) {
          this.uploadError = err.error.error;
        } else if (err.error?.message) {
          this.uploadError = err.error.message;
        } else {
          this.uploadError = 'Upload failed. Please try again.';
        }
        this.uploading = false;
      }
    });
  }
}
