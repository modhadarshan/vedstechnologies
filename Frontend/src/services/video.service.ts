

// src/app/services/video.service.ts
// This service handles API calls related to videos (upload, fetch, like management).
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, switchMap } from 'rxjs'; // Import 'of' for creating observables
import { VideoResponse, VideoUploadRequest } from '../models/video.model';
import { AuthService } from './auth.service'; // Import AuthService to get authentication headers
import { environment } from '../../src/environments/environment'; // Assuming environment.ts for API URL
import { Long } from '../models/types'; // Import Long type

// Interface for backend pagination response
interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number; // current page number (0-indexed)
  first: boolean;
  last: boolean;
  empty: boolean;
}

@Injectable({
  providedIn: 'root' // Makes the service a singleton and available throughout the application
})
export class VideoService {
  private baseUrl = environment.apiUrl; // Base URL for your Spring Boot backend API

  constructor(private http: HttpClient, private authService: AuthService) { }

  // Uploads a new video along with its details and optional thumbnail.
  uploadVideo(uploadRequest: VideoUploadRequest, videoFile: File, thumbnailFile: File | null): Observable<VideoResponse> {
    const formData = new FormData();
    // Append JSON request body as a Blob with application/json content type
    formData.append('uploadRequest', new Blob([JSON.stringify(uploadRequest)], { type: 'application/json' }));
    formData.append('videoFile', videoFile, videoFile.name); // Append the video file
    if (thumbnailFile) {
      formData.append('thumbnailFile', thumbnailFile, thumbnailFile.name); // Append optional thumbnail
    }

    // Get authentication headers from AuthService
    const headers = this.authService.getAuthHeaders();
    // HttpClient automatically sets 'Content-Type': 'multipart/form-data' for FormData

    return this.http.post<VideoResponse>(`${this.baseUrl}/videos`, formData, { headers });
  }

  // Fetches a single video by its ID.
  getVideoById(videoId: Long): Observable<VideoResponse> {
    // Guests can view videos, but sending auth headers allows backend to include `userLiked` status.
    const headers = this.authService.getAuthHeaders();
    return this.http.get<VideoResponse>(`${this.baseUrl}/videos/${videoId}`, { headers });
  }

  // Fetches a paginated list of all videos.
  getAllVideos(page: number, size: number): Observable<PageResponse<VideoResponse>> {
    // Guests can view videos, but sending auth headers allows backend to include `userLiked` status.
    const headers = this.authService.getAuthHeaders();
    return this.http.get<PageResponse<VideoResponse>>(`${this.baseUrl}/videos?page=${page}&size=${size}`, { headers });
  }

  // Toggles the like status for a specific video by the current user.
  toggleVideoLike(videoId: Long): Observable<boolean> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<boolean>(`${this.baseUrl}/videos/${videoId}/like`, {}, { headers });
  }

  // Gets the total like count for a specific video.
  getVideoLikeCount(videoId: Long): Observable<number> {
    // Like count is public, no authentication required.
    return this.http.get<number>(`${this.baseUrl}/videos/${videoId}/like-count`);
  }

  // Checks if the current authenticated user has liked a specific video.
  hasUserLikedVideo(videoId: Long): Observable<boolean> {
    const headers = this.authService.getAuthHeaders();
    // If user is not authenticated, they cannot have liked the video.
    // Use the isAuthenticated$ observable for this check.
    return this.authService.isAuthenticated$.pipe(
      switchMap(isAuth => {
        if (!isAuth) {
          return of(false); // Return observable of false if not authenticated
        }
        return this.http.get<boolean>(`${this.baseUrl}/videos/${videoId}/user-liked`, { headers });
      })
    );
  }
}
