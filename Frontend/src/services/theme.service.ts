
// src/app/services/theme.service.ts
// This service manages the application's theme (light/dark mode).
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root' // Makes the service a singleton and available throughout the application
})
export class ThemeService {
  private darkModeSubject = new BehaviorSubject<boolean>(false); // BehaviorSubject to hold and emit theme state
  isDarkMode$: Observable<boolean> = this.darkModeSubject.asObservable(); // Public observable for components to subscribe to

  constructor() { }

  // Initializes the theme based on user's local storage preference or system settings.
  initTheme(): void {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme) {
      // If a theme is saved in local storage, apply it
      this.darkModeSubject.next(savedTheme === 'dark');
      this.applyTheme(savedTheme === 'dark');
    } else {
      // If no theme is saved, check the user's system preference
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      this.darkModeSubject.next(prefersDark);
      this.applyTheme(prefersDark);
    }
  }

  // Toggles the current theme between light and dark mode.
  toggleTheme(): void {
    const newMode = !this.darkModeSubject.value; // Invert current mode
    this.darkModeSubject.next(newMode); // Emit new mode
    this.applyTheme(newMode); // Apply theme to the document
    localStorage.setItem('theme', newMode ? 'dark' : 'light'); // Save preference to local storage
  }

  // Applies the 'dark' class to the document's root element (<html>) based on the theme.
  private applyTheme(isDark: boolean): void {
    if (isDark) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }
}
