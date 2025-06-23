
// src/app/services/comment.service.ts
// This service handles API calls related to comments and replies (add, fetch, like management).
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs'; // Import 'of' for creating observables
import { CommentRequest, CommentResponse } from '../models/comment.model';
import { AuthService } from './auth.service'; // Import AuthService for authentication headers
import { environment } from '../../src/environments/environment'; // Assuming environment.ts for API URL
import { Long } from '../models/types'; // Import Long type

@Injectable({
  providedIn: 'root' // Makes the service a singleton and available throughout the application
})
export class CommentService {
  private baseUrl = environment.apiUrl; // Base URL for your Spring Boot backend API

  constructor(private http: HttpClient, private authService: AuthService) { }

  // Adds a new comment or reply to a video.
  // If `parentCommentId` is provided, it's treated as a reply.
  addComment(videoId: Long, content: string, parentCommentId: Long | null = null): Observable<CommentResponse> {
    const requestBody: CommentRequest = { content, parentCommentId };
    const headers = this.authService.getAuthHeaders(); // Get authentication headers
    // The backend endpoint `/comments/video/{videoId}` handles both top-level comments and replies.
    // The `videoId` in the path refers to the main video the comment (or its parent) belongs to.
    return this.http.post<CommentResponse>(`${this.baseUrl}/comments/video/${videoId}`, requestBody, { headers });
  }

  // Fetches all top-level comments for a specific video.
  getCommentsForVideo(videoId: Long): Observable<CommentResponse[]> {
    const headers = this.authService.getAuthHeaders(); // Include headers to get `userLiked` status
    return this.http.get<CommentResponse[]>(`${this.baseUrl}/comments/video/${videoId}`, { headers });
  }

  // Fetches all replies for a specific parent comment.
  getRepliesForComment(commentId: Long): Observable<CommentResponse[]> {
    const headers = this.authService.getAuthHeaders(); // Include headers to get `userLiked` status
    return this.http.get<CommentResponse[]>(`${this.baseUrl}/comments/${commentId}/replies`, { headers });
  }

  // Toggles the like status for a specific comment by the current user.
  toggleCommentLike(commentId: Long): Observable<boolean> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<boolean>(`${this.baseUrl}/comments/${commentId}/like`, {}, { headers });
  }

  // Gets the total like count for a specific comment.
  getCommentLikeCount(commentId: Long): Observable<number> {
    // Like count is public, no authentication required.
    return this.http.get<number>(`${this.baseUrl}/comments/${commentId}/like-count`);
  }

  // Checks if the current authenticated user has liked a specific comment.
  hasUserLikedComment(commentId: Long): Observable<boolean> {
    const headers = this.authService.getAuthHeaders();
    // If user is not authenticated, they cannot have liked the comment.
    if (!this.authService.isAuthenticated()) {
      return of(false); // Return an observable of false
    }
    return this.http.get<boolean>(`${this.baseUrl}/comments/${commentId}/user-liked`, { headers });
  }
}
