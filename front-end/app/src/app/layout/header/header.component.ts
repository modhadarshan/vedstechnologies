import { Component } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

@Component({
  selector: 'app-header',
  imports: [RouterLinkWithHref],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  // Controls the visibility and animation of the mobile menu and burger icon
  menuOpen = false;

  // Toggles the 'menuOpen' state when the burger button is clicked
  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  // Closes the menu, typically when a navigation link is clicked
  closeMenu() {
    this.menuOpen = false;
  }
}
