

// src/app/components/login/login.component.ts
// This component handles user login functionality.
// It's a standalone component and manages its own imports.
import { Component, OnInit } from '@angular/core';
import { CommonModule, NgIf, NgClass } from '@angular/common'; // Import CommonModule for NgIf, NgClass
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms'; // Import ReactiveFormsModule and related
import { Router, RouterLink } from '@angular/router'; // Import Router and RouterLink
import { AuthService } from '../../../services/auth.service';
import { Long } from '../../../models/types'; // Import Long type (though not directly used in template, it's a good practice to have it here if models use it)

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: true, // IMPORTANT: Mark component as standalone
  imports: [
    CommonModule, // Provides NgIf, NgClass for conditional rendering and styling
    ReactiveFormsModule, // Provides FormGroup, FormControl, Validators for reactive forms
    RouterLink, // Provides routerLink directive for navigation
    NgIf, NgClass // Explicitly import for clarity (optional, included in CommonModule)
  ]
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup; // Reactive form group for login
  errorMessage: string | null = null; // Stores error messages
  loading = false; // Flag for loading state

  constructor(
    private fb: FormBuilder, // FormBuilder for creating form controls
    private authService: AuthService, // AuthService for authentication logic
    private router: Router // Router for navigation
  ) { }

  ngOnInit(): void {
    // Initialize the login form with email and password fields and their validators
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });

    // If user is already authenticated, redirect them to the home page
    // Using the isAuthenticated$ observable here
    this.authService.isAuthenticated$.subscribe(isAuth => {
      if (isAuth) {
        this.router.navigate(['/']);
      }
    });
  }

  // Handles form submission for login
  onSubmit(): void {
    this.errorMessage = null; // Clear any previous error messages
    if (this.loginForm.invalid) {
      this.errorMessage = 'Please enter valid email and password.';
      return;
    }

    this.loading = true; // Set loading state
    const { email, password } = this.loginForm.value; // Get form values
    this.authService.login(email, password).subscribe(
      () => {
        this.loading = false; // Clear loading state
        this.router.navigate(['/']); // Redirect to home on successful login
      },
      (error) => {
        this.loading = false; // Clear loading state
        this.errorMessage = error.error || 'Login failed. Please try again.'; // Display error
        console.error('Login error:', error); // Log the detailed error
      }
    );
  }
}
