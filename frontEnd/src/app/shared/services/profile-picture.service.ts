import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ProfilePictureService {
  private profilePictureUpdated = new Subject<string | null>();
  profilePictureUpdated$ = this.profilePictureUpdated.asObservable();

  notifyProfilePictureUpdated(imageUrl: string | null) {
    console.log('[ProfilePictureService] Notifying profile picture update:', imageUrl);
    this.profilePictureUpdated.next(imageUrl);
  }
}

