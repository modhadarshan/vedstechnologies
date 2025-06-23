
// src/app/components/register/register.component.ts
// This component handles user registration functionality.
// It's a standalone component and manages its own imports.
import { Component, OnInit } from '@angular/core';
import { CommonModule, NgIf, NgClass } from '@angular/common'; // Import CommonModule for NgIf, NgClass
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms'; // Import ReactiveFormsModule and related
import { Router, RouterLink } from '@angular/router'; // Import Router and RouterLink
import { AuthService } from '../../../services/auth.service';
import { Long } from '../../../models/types'; // Import Long type (not directly used in template, but good for consistency)

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  standalone: true, // IMPORTANT: Mark component as standalone
  imports: [
    CommonModule, // Provides NgIf, NgClass for conditional rendering and styling
    ReactiveFormsModule, // Provides FormGroup, FormControl, Validators for reactive forms
    RouterLink, // Provides routerLink directive for navigation
    NgIf, NgClass // Explicitly import for clarity (optional, included in CommonModule)
  ]
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup; // Reactive form group for registration
  errorMessage: string | null = null; // Stores error messages
  successMessage: string | null = null; // Stores success messages
  loading = false; // Flag for loading state

  constructor(
    private fb: FormBuilder, // FormBuilder for creating form controls
    private authService: AuthService, // AuthService for authentication logic
    private router: Router // Router for navigation
  ) { }

  ngOnInit(): void {
    // Initialize the registration form with email, password, and confirmPassword fields and their validators
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, { validator: this.passwordMatchValidator }); // Add custom validator for password match

    // If user is already authenticated, redirect them to the home page
    // Using isAuthenticated$ observable here
    this.authService.isAuthenticated$.subscribe(isAuth => {
      if (isAuth) {
        this.router.navigate(['/']);
      }
    });
  }

  // Custom validator function to check if password and confirm password match
  passwordMatchValidator(form: FormGroup) {
    return form.get('password')?.value === form.get('confirmPassword')?.value
      ? null : { 'mismatch': true }; // Return 'mismatch' error if passwords don't match
  }

  // Handles form submission for registration
  onSubmit(): void {
    this.errorMessage = null; // Clear any previous error messages
    this.successMessage = null; // Clear any previous success messages

    if (this.registerForm.invalid) {
      this.errorMessage = 'Please fix the errors in the form.'; // General error for invalid form
      return;
    }

    this.loading = true; // Set loading state
    const { email, password } = this.registerForm.value; // Get form values
    this.authService.register(email, password).subscribe(
      () => {
        this.loading = false; // Clear loading state
        this.successMessage = 'Registration successful! You can now log in.'; // Display success
        this.registerForm.reset(); // Clear the form
        // Optionally redirect to login after a short delay
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      (error) => {
        this.loading = false; // Clear loading state
        this.errorMessage = error.error || 'Registration failed. Please try again.'; // Display error
        console.error('Registration error:', error); // Log the detailed error
      }
    );
  }
}
