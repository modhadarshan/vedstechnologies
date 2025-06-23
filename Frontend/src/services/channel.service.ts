

// src/app/services/channel.service.ts
// This service handles API calls related to YouTube channels (subscribe, unsubscribe, get subscriber count).
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs'; // Import 'of' for creating observables
import { AuthService } from './auth.service'; // Import AuthService for authentication headers
import { environment } from '../../src/environments/environment'; // Assuming environment.ts for API URL
import { Long } from '../models/types'; // Import Long type

@Injectable({
  providedIn: 'root' // Makes the service a singleton and available throughout the application
})
export class ChannelService {
  private baseUrl = environment.apiUrl; // Base URL for your Spring Boot backend API

  constructor(private http: HttpClient, private authService: AuthService) { }

  // Subscribes the current authenticated user to a channel.
  subscribeToChannel(channelId: Long): Observable<void> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<void>(`${this.baseUrl}/channels/${channelId}/subscribe`, {}, { headers });
  }

  // Unsubscribes the current authenticated user from a channel.
  unsubscribeFromChannel(channelId: Long): Observable<void> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete<void>(`${this.baseUrl}/channels/${channelId}/unsubscribe`, { headers });
  }

  // Checks if the current authenticated user is subscribed to a channel.
  isSubscribed(channelId: Long): Observable<boolean> {
    const headers = this.authService.getAuthHeaders();
    // If user is not authenticated, they cannot be subscribed.
    if (!this.authService.isAuthenticated()) {
      return of(false); // Return an observable of false
    }
    return this.http.get<boolean>(`${this.baseUrl}/channels/${channelId}/is-subscribed`, { headers });
  }

  // Gets the total subscriber count for a channel.
  getSubscriberCount(channelId: Long): Observable<number> {
    // Subscriber count is public, no authentication required.
    return this.http.get<number>(`${this.baseUrl}/channels/${channelId}/subscriber-count`);
  }
}
