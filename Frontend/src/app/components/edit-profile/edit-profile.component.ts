

// src/app/components/edit-profile/edit-profile.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule, NgIf, NgClass } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { UserProfileUpdateRequest, UserResponse } from '../../../models/user.model';
import { Long } from '../../../models/types'; // Import Long type

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NgIf,
    NgClass
  ]
})
export class EditProfileComponent implements OnInit {
  profileForm!: FormGroup;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  loading = false;
  currentUser: UserResponse | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;

    if (!this.currentUser) {
      this.router.navigate(['/login']); // Redirect if not logged in
      return;
    }

    // Initialize the form with current user's channel details
    this.profileForm = this.fb.group({
      channelName: [this.currentUser.youtubeChannel?.name || '', Validators.required],
      channelDescription: [this.currentUser.youtubeChannel?.description || '']
    });
  }

  onSubmit(): void {
    this.errorMessage = null;
    this.successMessage = null;

    if (this.profileForm.invalid) {
      this.errorMessage = 'Please fill in all required fields.';
      return;
    }

    this.loading = true;
    const { channelName, channelDescription } = this.profileForm.value;
    const updateRequest: UserProfileUpdateRequest = { channelName, channelDescription };

    this.authService.updateUserProfile(updateRequest).subscribe(
      (updatedUser) => {
        this.loading = false;
        this.successMessage = 'Profile updated successfully!';
        // Update local user state in AuthService if necessary (already done in AuthService.updateUserProfile)
        // Optionally navigate away after a short delay
        setTimeout(() => {
          this.router.navigate(['/']); // Navigate back to home or a profile view
        }, 2000);
      },
      (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Failed to update profile. Please try again.';
        console.error('Profile update error:', error);
      }
    );
  }
}
