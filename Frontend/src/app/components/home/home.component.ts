import { Component, OnInit } from '@angular/core';
import { CommonModule, NgIf, NgFor } from '@angular/common'; // Import necessary CommonModule parts
import { VideoGridComponent } from '../video-grid/video-grid.component'; // Import child component
import { VideoService } from '../../../services/video.service';
import { VideoResponse } from '../../../models/video.model';
import { Long } from '../../../models/types'; // Import Long type

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  standalone: true, // IMPORTANT: Mark component as standalone
  imports: [
    CommonModule, // Provides *ngIf, *ngFor
    VideoGridComponent, // Make VideoGridComponent available in this component's template
    NgIf, NgFor // Explicitly import for clarity (optional, included in CommonModule)
  ]
})
export class HomeComponent implements OnInit {
  videos: VideoResponse[] = [];
  currentPage = 0;
  pageSize = 12;
  hasMoreVideos = true;
  isLoading = false;

  constructor(private videoService: VideoService) { }

  ngOnInit(): void {
    this.loadVideos();
  }

  // Loads videos from the backend, handles pagination and appends to existing videos.
  loadVideos(): void {
    if (this.isLoading || !this.hasMoreVideos) {
      return;
    }
    this.isLoading = true;
    this.videoService.getAllVideos(this.currentPage, this.pageSize).subscribe(
      (data) => {
        this.videos = [...this.videos, ...data.content]; // Append new videos
        this.hasMoreVideos = !data.last; // Check if there are more pages
        this.currentPage++; // Increment page number for next load
        this.isLoading = false;
      },
      (error) => {
        console.error('Error loading videos:', error);
        this.isLoading = false;
        // Implement better user feedback for errors (e.g., toast messages)
      }
    );
  }

  // Simple scroll detection for infinite scrolling.
  // In a real application, consider using Angular's `@angular/cdk/scrolling` module
  // for more robust and performant infinite scrolling.
  onScroll(): void {
    // Detects when user scrolls near the bottom of the page
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 500 && !this.isLoading && this.hasMoreVideos) {
      this.loadVideos();
    }
  }
}
