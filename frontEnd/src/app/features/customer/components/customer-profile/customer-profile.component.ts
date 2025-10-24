import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CustomerService } from '../../services/customer.service';

@Component({
  selector: 'app-customer-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './customer-profile.component.html',
  styleUrls: ['./customer-profile.component.css']
})
export class CustomerProfileComponent implements OnInit {
  profileForm!: FormGroup;
  loading = false;
  success = false;

  constructor(private fb: FormBuilder, private customerService: CustomerService) {}

  ngOnInit() {
    this.profileForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      zip: ['']
    });
    this.loading = true;
    this.customerService.getProfile().subscribe(profile => {
      this.profileForm.patchValue(profile);
      this.loading = false;
    });
  }

  saveProfile() {
    if (this.profileForm.valid) {
      this.loading = true;
      this.customerService.updateProfile(this.profileForm.value).subscribe(() => {
        this.success = true;
        this.loading = false;
      });
    }
  }
}
