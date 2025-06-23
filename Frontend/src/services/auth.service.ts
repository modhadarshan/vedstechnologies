

// src/app/services/auth.service.ts
// This service handles user authentication, including registration, login, and managing the current user's state.
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs'; // Import 'of' for creating observables
import { tap, catchError, map } from 'rxjs/operators'; // Import 'map'

import { UserResponse } from '../models/user.model';
import { environment } from '../../src/environments/environment'; // Assuming you have environment.ts for API URL

@Injectable({
  providedIn: 'root' // Makes the service a singleton and available throughout the application
})
export class AuthService {
  private baseUrl = environment.apiUrl; // Base URL for your Spring Boot backend API

  private currentUserSubject: BehaviorSubject<UserResponse | null>; // Holds the current user's data
  public currentUser$: Observable<UserResponse | null>; // Public observable for components to subscribe to user changes
  public isAuthenticated$: Observable<boolean>; // Public observable for authentication status

  constructor(private http: HttpClient) {
    // Initialize with user data from local storage if available
    const storedUser = localStorage.getItem('currentUser');
    this.currentUserSubject = new BehaviorSubject<UserResponse | null>(storedUser ? JSON.parse(storedUser) : null);
    this.currentUser$ = this.currentUserSubject.asObservable();
    // Derive isAuthenticated$ from currentUser$
    this.isAuthenticated$ = this.currentUser$.pipe(
      map(user => !!user) // Emits true if user is not null, false otherwise
    );
  }

  // Getter for the current user's value (snapshot)
  public get currentUserValue(): UserResponse | null {
    return this.currentUserSubject.value;
  }

  // Method to synchronously check if a user is authenticated.
  // This is useful for simple 'if' checks where you don't need to react to future changes.
  isAuthenticated(): boolean {
    return this.currentUserSubject.value !== null;
  }

  // Helper function to create HTTP headers with authentication details.
  // NOTE: Storing passwords in localStorage (`localStorage.getItem('password')`) for manual auth is NOT secure.
  // In a real application, you would use JWT tokens or session cookies.
  getAuthHeaders(): HttpHeaders {
    const user = this.currentUserValue;
    if (user && localStorage.getItem('password')) {
      return new HttpHeaders({
        'X-User-Email': user.email,
        'X-User-Password': localStorage.getItem('password') || '' // Fallback to empty string
      });
    }
    return new HttpHeaders(); // Return empty headers if not authenticated
  }

  // Registers a new user with the backend.
  register(email: string, password: string): Observable<UserResponse> {
    const requestBody = { email, password };
    return this.http.post<UserResponse>(`${this.baseUrl}/auth/register`, requestBody).pipe(
      tap((user) => {
        // Upon successful registration, automatically log them in (for demo simplicity).
        // WARNING: Storing plain password in localStorage is highly insecure and for demonstration ONLY.
        localStorage.setItem('currentUser', JSON.stringify(user));
        localStorage.setItem('password', password);
        this.currentUserSubject.next(user);
      }),
      catchError(this.handleError<UserResponse>('register')) // Handle API errors
    );
  }

  // Logs in an existing user with the backend.
  login(email: string, password: string): Observable<UserResponse> {
    const requestBody = { email, password };
    return this.http.post<UserResponse>(`${this.baseUrl}/auth/login`, requestBody).pipe(
      tap((user) => {
        // Store user and password (insecurely for demo) and update BehaviorSubject
        localStorage.setItem('currentUser', JSON.stringify(user));
        localStorage.setItem('password', password);
        this.currentUserSubject.next(user);
      }),
      catchError(this.handleError<UserResponse>('login')) // Handle API errors
    );
  }

  // Logs out the current user, clearing local storage and user state.
  logout(): void {
    localStorage.removeItem('currentUser');
    localStorage.removeItem('password');
    this.currentUserSubject.next(null); // Emit null to indicate no logged-in user
  }

  // Generic error handling function for HTTP requests.
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed:`, error); // Log the error to console
      // Re-throw the error so components can display specific messages (e.g., `error.error`).
      throw error;
    };
  }
}
