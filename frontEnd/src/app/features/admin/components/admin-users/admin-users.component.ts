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
  editingUser: any = null;
  editForm: any = {};

  constructor(private adminUserService: AdminUserService) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.adminUserService.getAllUsers().subscribe(users => this.users = users);
  }

  startEdit(user: any) {
    this.editingUser = user;
    this.editForm = { ...user };
  }

  saveEdit() {
    this.adminUserService.updateUser(this.editingUser.id, this.editForm).subscribe(updated => {
      this.users = this.users.map(u => u.id === updated.id ? updated : u);
      this.editingUser = null;
    });
  }

  cancelEdit() {
    this.editingUser = null;
  }

  deleteUser(id: number) {
    this.adminUserService.deleteUser(id).subscribe(() => {
      this.users = this.users.filter(u => u.id !== id);
    });
  }
}
