import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule, NgIf, AsyncPipe } from '@angular/common'; // Import CommonModule and specific items
import { Router, RouterLink } from '@angular/router'; // Import Router for navigation, RouterLink for routerLink directive
import { ThemeService } from '../../../services/theme.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
  standalone: true, // IMPORTANT: Mark component as standalone
  imports: [
    CommonModule, // Provides *ngIf, AsyncPipe
    RouterLink, // Provides routerLink directive for navigation links
    NgIf, // Explicitly import NgIf for clarity (optional, included in CommonModule)
    AsyncPipe // Explicitly import AsyncPipe for clarity (optional, included in CommonModule)
  ]
})
export class NavbarComponent {
  @Output() toggleSidebar = new EventEmitter<void>(); // Event emitter for sidebar toggle

  constructor(public themeService: ThemeService, public authService: AuthService, private router: Router) { }

  // Emits an event to toggle the sidebar visibility
  onToggleSidebar(): void {
    this.toggleSidebar.emit();
  }

  // Toggles the application's theme (light/dark)
  onToggleTheme(): void {
    this.themeService.toggleTheme();
  }

  // Handles user logout, clears session and redirects to home
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']); // Redirect to home or login page after logout
  }

  // Handles upload button click, checks authentication, and navigates to upload page
  uploadVideo(): void {
    // We now use the isAuthenticated$ observable for checks in the component logic too
    this.authService.isAuthenticated$.subscribe(isAuth => {
      if (isAuth) {
        this.router.navigate(['/upload']);
      } else {
        // In a real application, consider a custom modal instead of alert()
        alert('Please log in to upload videos.');
        this.router.navigate(['/login']);
      }
    });
  }
}
