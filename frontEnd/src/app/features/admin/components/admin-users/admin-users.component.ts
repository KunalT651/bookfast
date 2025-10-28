import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminUserService } from '../../services/admin-user.service';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.css']
})
export class AdminUsersComponent implements OnInit {
  users: any[] = [];
  filteredUsers: any[] = [];
  searchTerm = '';
  selectedUser: any = null;
  showUserModal = false;
  loading = false;
  errorMessage = '';
  successMessage = '';

  // User form fields
  userForm = {
    firstName: '',
    lastName: '',
    email: '',
    role: 'CUSTOMER',
    organizationName: '',
    serviceCategory: ''
  };

  constructor(private adminUserService: AdminUserService) {}

  ngOnInit() {
    console.log('[AdminUsersComponent] ngOnInit called');
    this.loadUsers();
  }

  loadUsers() {
    console.log('[AdminUsersComponent] loadUsers called');
    this.loading = true;
    this.errorMessage = '';
    console.log('[AdminUsersComponent] About to call adminUserService.getAllUsers()');
    this.adminUserService.getAllUsers().subscribe({
      next: (users) => {
        console.log('[AdminUsersComponent] Received users:', users);
        this.users = users;
        this.filteredUsers = users;
        this.loading = false;
      },
      error: (error) => {
        console.error('[AdminUsersComponent] Error loading users:', error);
        this.errorMessage = 'Failed to load users: ' + (error.message || error.statusText);
        this.loading = false;
      }
    });
  }

  searchUsers() {
    if (!this.searchTerm.trim()) {
      this.filteredUsers = this.users;
      return;
    }

    this.filteredUsers = this.users.filter(user =>
      user.firstName?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      user.lastName?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      user.email?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      user.role?.name?.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  openUserModal(user: any = null) {
    this.selectedUser = user;
    if (user) {
      this.userForm = {
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        email: user.email || '',
        role: user.role?.name || 'CUSTOMER',
        organizationName: user.organizationName || '',
        serviceCategory: user.serviceCategory || ''
      };
    } else {
      this.userForm = {
        firstName: '',
        lastName: '',
        email: '',
        role: 'CUSTOMER',
        organizationName: '',
        serviceCategory: ''
      };
    }
    this.showUserModal = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  closeUserModal() {
    this.showUserModal = false;
    this.selectedUser = null;
    this.errorMessage = '';
    this.successMessage = '';
  }

  saveUser() {
    if (!this.userForm.firstName || !this.userForm.lastName || !this.userForm.email) {
      this.errorMessage = 'Please fill in all required fields';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    if (this.selectedUser) {
      // Update existing user
      this.adminUserService.updateUser(this.selectedUser.id, this.userForm).subscribe({
        next: () => {
          this.successMessage = 'User updated successfully';
          this.loadUsers();
          this.closeUserModal();
          this.loading = false;
        },
        error: (error) => {
          this.errorMessage = 'Failed to update user';
          this.loading = false;
          console.error('Error updating user:', error);
        }
      });
    } else {
      // Create new user
      this.adminUserService.createUser(this.userForm).subscribe({
        next: () => {
          this.successMessage = 'User created successfully';
          this.loadUsers();
          this.closeUserModal();
          this.loading = false;
        },
        error: (error) => {
          this.errorMessage = 'Failed to create user';
          this.loading = false;
          console.error('Error creating user:', error);
        }
      });
    }
  }

  deleteUser(user: any) {
    if (!confirm(`Are you sure you want to delete user ${user.firstName} ${user.lastName}?`)) {
      return;
    }

    this.loading = true;
    this.adminUserService.deleteUser(user.id).subscribe({
      next: () => {
        this.successMessage = 'User deleted successfully';
        this.loadUsers();
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to delete user';
        this.loading = false;
        console.error('Error deleting user:', error);
      }
    });
  }

  getRoleDisplayName(role: string) {
    switch (role) {
      case 'ADMIN': return 'Administrator';
      case 'PROVIDER': return 'Service Provider';
      case 'CUSTOMER': return 'Customer';
      default: return role;
    }
  }
}