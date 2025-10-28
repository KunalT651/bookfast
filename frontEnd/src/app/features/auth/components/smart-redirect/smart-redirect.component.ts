import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserStateService } from '../../../../shared/services/user-state.service';

@Component({
  selector: 'app-smart-redirect',
  standalone: true,
  template: '<div>Redirecting...</div>'
})
export class SmartRedirectComponent implements OnInit {
  constructor(
    private userState: UserStateService,
    private router: Router
  ) {}

  ngOnInit() {
    this.userState.getLoggedIn().subscribe((loggedIn: boolean) => {
      if (loggedIn) {
        this.userState.getUser().subscribe((user: any) => {
          if (user?.role?.name === 'ADMIN') {
            this.router.navigate(['/admin/dashboard']);
          } else if (user?.role?.name === 'PROVIDER') {
            this.router.navigate(['/provider/dashboard']);
          } else if (user?.role?.name === 'CUSTOMER') {
            this.router.navigate(['/customer/home']);
          } else {
            this.router.navigate(['/login']);
          }
        });
      } else {
        this.router.navigate(['/login']);
      }
    });
  }
}
