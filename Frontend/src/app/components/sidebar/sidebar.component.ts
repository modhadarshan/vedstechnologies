

// src/app/components/sidebar/sidebar.component.ts
// This component represents the application's sidebar navigation.
// It's a standalone component and manages its own imports.
import { Component, Input } from '@angular/core';
import { CommonModule, NgClass, NgIf } from '@angular/common'; // Import necessary CommonModule parts
import { RouterLink, RouterLinkActive } from '@angular/router'; // Import RouterLink and RouterLinkActive for router directives
import { AuthService } from '../../../services/auth.service';
import { map } from 'rxjs/operators'; // Import map

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css'],
  standalone: true, // IMPORTANT: Mark component as standalone
  imports: [
    CommonModule, // Provides NgClass, NgIf, NgFor (if used in future)
    RouterLink, // Provides routerLink directive for navigation links
    RouterLinkActive, // Provides routerLinkActive directive for active link styling
    NgClass, NgIf // Explicitly import for clarity (optional, included in CommonModule)
  ]
})
export class SidebarComponent {
  @Input() isOpen: boolean = true; // Input property to control sidebar visibility

  // Public observable to check authentication status for template use
  isAuthenticated$ = this.authService.currentUser$.pipe(
    map(user => !!user)
  );

  constructor(public authService: AuthService) { } // Inject AuthService for authentication status checks
}
