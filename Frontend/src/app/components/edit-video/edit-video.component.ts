

// src/app/components/edit-video/edit-video.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule, NgIf, NgClass } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { VideoService } from '../../../services/video.service';
import { AuthService } from '../../../services/auth.service';
import { VideoResponse, VideoUpdateRequest } from '../../../models/video.model';
import { Long } from '../../../models/types';

@Component({
  selector: 'app-edit-video',
  templateUrl: './edit-video.component.html',
  styleUrls: ['./edit-video.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NgIf,
    NgClass
  ]
})
export class EditVideoComponent implements OnInit {
  editForm!: FormGroup;
  videoId: Long | null = null;
  video: VideoResponse | null = null;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private videoService: VideoService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.editForm = this.fb.group({
      title: ['', Validators.required],
      description: [''],
      category: ['']
    });

    this.route.paramMap.subscribe(params => {
      const id = params.get('videoId');
      if (id) {
        this.videoId = +id;
        this.loadVideoDetails(this.videoId);
      } else {
        this.router.navigate(['/']); // Redirect if no videoId
      }
    });
  }

  loadVideoDetails(videoId: Long): void {
    this.loading = true;
    this.videoService.getVideoById(videoId).subscribe(
      (data) => {
        this.video = data;
        this.loading = false;

        // Check if current user is the owner of the video
        const currentUserChannelId = this.authService.currentUserValue?.youtubeChannel?.id;
        const videoOwnerChannelId = this.video?.youtubeChannel?.id;

        if (!currentUserChannelId || currentUserChannelId !== videoOwnerChannelId) {
          alert('You do not have permission to edit this video.');
          this.router.navigate(['/watch', videoId]); // Redirect to video player if not owner
          return;
        }

        // Populate the form with fetched video data
        this.editForm.patchValue({
          title: this.video.title,
          description: this.video.description,
          category: this.video.category
        });
      },
      (error) => {
        console.error('Error loading video for edit:', error);
        this.loading = false;
        this.errorMessage = 'Could not load video details for editing.';
        this.router.navigate(['/']); // Redirect on error
      }
    );
  }

  onSubmit(): void {
    this.errorMessage = null;
    this.successMessage = null;

    if (this.editForm.invalid || this.videoId === null) {
      this.errorMessage = 'Please fill in all required fields.';
      return;
    }

    this.loading = true;
    const updateRequest: VideoUpdateRequest = {
      title: this.editForm.get('title')?.value,
      description: this.editForm.get('description')?.value,
      category: this.editForm.get('category')?.value,
    };

    this.videoService.updateVideo(this.videoId, updateRequest).subscribe(
      (response) => {
        this.loading = false;
        this.successMessage = 'Video updated successfully!';
        // Optionally redirect to the updated video's watch page
        setTimeout(() => {
          this.router.navigate(['/watch', response.id]);
        }, 2000);
      },
      (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Video update failed. Please try again.';
        console.error('Video update error:', error);
      }
    );
  }
}
