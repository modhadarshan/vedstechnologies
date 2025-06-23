import { Component, Input } from '@angular/core';
import { CommonModule, NgIf, DatePipe } from '@angular/common'; // Import necessary CommonModule parts
import { RouterLink } from '@angular/router'; // Import RouterLink for navigation
import { DomSanitizer, SafeUrl } from '@angular/platform-browser'; // For sanitizing URLs
import { VideoResponse } from '../../../models/video.model';
import { Long } from '../../../models/types'; // Import Long type

@Component({
  selector: 'app-video-card',
  templateUrl: './video-card.component.html',
  styleUrls: ['./video-card.component.css'],
  standalone: true, // IMPORTANT: Mark component as standalone
  imports: [
    CommonModule, // Provides NgIf, DatePipe
    RouterLink, // Provides routerLink directive
    NgIf, DatePipe // Explicitly import for clarity (optional, included in CommonModule)
  ]
})
export class VideoCardComponent {
  @Input() video!: VideoResponse; // Input property to receive a single video object

  constructor(private sanitizer: DomSanitizer) { }

  // Generates the thumbnail URL from binary data or provides a placeholder.
  getThumbnailUrl(): SafeUrl | string {
    if (this.video.thumbnailData) {
      // Assuming JPEG thumbnail; adjust mime type if different
      const blob = new Blob([new Uint8Array(this.video.thumbnailData)], { type: 'image/jpeg' });
      return this.sanitizer.bypassSecurityTrustUrl(URL.createObjectURL(blob));
    }
    return 'https://placehold.co/320x180/e0e0e0/ffffff?text=No+Thumbnail'; // Placeholder if no thumbnail
  }

  // Formats view count for better readability (e.g., 1.2K, 1M).
  formatViewCount(count: number): string {
    if (count >= 1000000) {
      return (count / 1000000).toFixed(1) + 'M views';
    } else if (count >= 1000) {
      return (count / 1000).toFixed(1) + 'K views';
    } else {
      return count + ' views';
    }
  }

  // Calculates the time elapsed since a given date string.
  timeSince(date: string): string {
    const seconds = Math.floor((new Date().getTime() - new Date(date).getTime()) / 1000);

    let interval = seconds / 31536000;
    if (interval > 1) {
      return Math.floor(interval) + ' years ago';
    }
    interval = seconds / 2592000;
    if (interval > 1) {
      return Math.floor(interval) + ' months ago';
    }
    interval = seconds / 86400;
    if (interval > 1) {
      return Math.floor(interval) + ' days ago';
    }
    interval = seconds / 3600;
    if (interval > 1) {
      return Math.floor(interval) + ' hours ago';
    }
    interval = seconds / 60;
    if (interval > 1) {
      return Math.floor(interval) + ' minutes ago';
    }
    return Math.floor(seconds) + ' seconds ago';
  }
}
