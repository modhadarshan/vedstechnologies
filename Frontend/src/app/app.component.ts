import { Component, OnInit } from '@angular/core';
import { CommonModule, AsyncPipe, NgClass, NgIf } from '@angular/common'; // Import necessary CommonModule parts
import { RouterOutlet } from '@angular/router'; // Import RouterOutlet for <router-outlet>
import { NavbarComponent } from './components/navbar/navbar.component'; // Import child component
import { SidebarComponent } from './components/sidebar/sidebar.component'; // Import child component
import { ThemeService } from '../services/theme.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  standalone: true, // IMPORTANT: Mark component as standalone
  imports: [
    CommonModule, // Provides NgIf, NgFor, NgClass, AsyncPipe etc. for this component and its template
    RouterOutlet, // Enables <router-outlet>
    NavbarComponent, // Make NavbarComponent available in this component's template
    SidebarComponent, // Make SidebarComponent available in this component's template
    // Explicitly listing specific directives/pipes from CommonModule is also an option for better tree-shaking
    // NgClass, AsyncPipe, NgIf // Can be explicitly listed if you only need certain parts of CommonModule
  ]
})
export class AppComponent implements OnInit {
  title = 'youtube-clone-angular';
  isSidebarOpen = true;

  constructor(public themeService: ThemeService, public authService: AuthService) { }

  ngOnInit() {
    // Initialize theme based on user's preference or system setting
    this.themeService.initTheme();
  }

  toggleSidebar(): void {
    this.isSidebarOpen = !this.isSidebarOpen;
  }
}
