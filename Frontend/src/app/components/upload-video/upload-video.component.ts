
// src/app/components/upload-video/upload-video.component.ts
// This component provides functionality for users to upload videos.
// It's a standalone component and manages its own imports.
import { Component, OnInit } from '@angular/core';
import { CommonModule, NgIf, NgClass } from '@angular/common'; // Import CommonModule for NgIf, NgClass
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms'; // Import ReactiveFormsModule, FormsModule and related
import { Router } from '@angular/router'; // Import Router for navigation
import { VideoService } from '../../../services/video.service';
import { VideoUploadRequest } from '../../../models/video.model';
import { Long } from '../../../models/types'; // Import Long type (not directly used in template, but good for consistency)

@Component({
  selector: 'app-upload-video',
  templateUrl: './upload-video.component.html',
  styleUrls: ['./upload-video.component.css'],
  standalone: true, // IMPORTANT: Mark component as standalone
  imports: [
    CommonModule, // Provides NgIf, NgClass
    ReactiveFormsModule, // Provides FormGroup, FormControl, Validators
    FormsModule, // Provides ngModel (though ReactiveFormsModule is preferred, ngModel is implicitly used with formControlName)
    NgIf, NgClass // Explicitly import for clarity (optional, included in CommonModule)
  ]
})
export class UploadVideoComponent implements OnInit {
  uploadForm!: FormGroup; // Reactive form group for video upload
  selectedVideoFile: File | null = null; // Stores the selected video file
  selectedThumbnailFile: File | null = null; // Stores the selected thumbnail file
  errorMessage: string | null = null; // Stores error messages
  successMessage: string | null = null; // Stores success messages
  loading = false; // Flag for loading state during upload

  constructor(
    private fb: FormBuilder, // FormBuilder for creating form controls
    private videoService: VideoService, // VideoService for handling video upload API calls
    private router: Router // Router for navigation
  ) { }

  ngOnInit(): void {
    // Initialize the upload form with video details and file controls
    this.uploadForm = this.fb.group({
      title: ['', Validators.required], // Video title is required
      description: [''],
      category: [''],
      videoFile: [null, Validators.required], // Video file itself is required
      thumbnailFile: [null] // Thumbnail file is optional
    });
  }

  // Handles selection of the video file
  onVideoFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedVideoFile = file;
      this.uploadForm.patchValue({ videoFile: file }); // Update form control value
      this.uploadForm.get('videoFile')?.updateValueAndValidity(); // Trigger validation
    } else {
      this.selectedVideoFile = null;
      this.uploadForm.patchValue({ videoFile: null });
      this.uploadForm.get('videoFile')?.updateValueAndValidity();
    }
  }

  // Handles selection of the thumbnail file
  onThumbnailFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedThumbnailFile = file;
      this.uploadForm.patchValue({ thumbnailFile: file });
    } else {
      this.selectedThumbnailFile = null;
      this.uploadForm.patchValue({ thumbnailFile: null });
    }
  }

  // Handles form submission for video upload
  onSubmit(): void {
    this.errorMessage = null; // Clear previous error messages
    this.successMessage = null; // Clear previous success messages

    // Check if form is valid and a video file is selected
    if (this.uploadForm.invalid || !this.selectedVideoFile) {
      this.errorMessage = 'Please fill in all required fields and select a video file.';
      this.uploadForm.markAllAsTouched(); // Mark all fields as touched to display validation errors
      return;
    }

    this.loading = true; // Set loading state

    // Create the video upload request object
    const uploadRequest: VideoUploadRequest = {
      title: this.uploadForm.get('title')?.value,
      description: this.uploadForm.get('description')?.value,
      category: this.uploadForm.get('category')?.value,
    };

    // Call the video service to upload the video
    this.videoService.uploadVideo(uploadRequest, this.selectedVideoFile, this.selectedThumbnailFile).subscribe(
      (response) => {
        this.loading = false; // Clear loading state
        this.successMessage = 'Video uploaded successfully!'; // Display success message
        this.uploadForm.reset(); // Reset the form
        this.selectedVideoFile = null; // Clear selected files
        this.selectedThumbnailFile = null;
        // Optionally redirect to the newly uploaded video's watch page or home page
        setTimeout(() => {
          this.router.navigate(['/watch', response.id]);
        }, 2000);
      },
      (error) => {
        this.loading = false; // Clear loading state
        this.errorMessage = error.error || 'Video upload failed. Please try again.'; // Display error
        console.error('Upload error:', error); // Log detailed error
      }
    );
  }
}
