

// src/app/components/comment-section/comment-section.component.ts
// This component manages displaying a list of comments and allowing users to add new comments.
// It's a standalone component and manages its own imports.
import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule, NgIf, NgFor } from '@angular/common'; // Import CommonModule parts
import { FormsModule } from '@angular/forms'; // Import FormsModule for ngModel
import { RouterLink } from '@angular/router'; // Import RouterLink for navigation (e.g., to login)
import { CommentComponent } from '../comment/comment.component'; // Import child component
import { CommentResponse } from '../../../models/comment.model';
import { CommentService } from '../../../services/comment.service';
import { AuthService } from '../../../services/auth.service';
import { Long } from '../../../models/types'; // Import Long type
import { map } from 'rxjs/operators'; // Import map

@Component({
  selector: 'app-comment-section',
  templateUrl: './comment-section.component.html',
  styleUrls: ['./comment-section.component.css'],
  standalone: true, // IMPORTANT: Mark component as standalone
  imports: [
    CommonModule, // Provides NgIf, NgFor for conditional rendering and list iteration
    FormsModule, // Provides ngModel for two-way data binding on input fields
    CommentComponent, // Make CommentComponent available for displaying individual comments
    RouterLink, // Provides routerLink directive
    NgIf, NgFor // Explicitly import for clarity (optional, included in CommonModule)
  ]
})
export class CommentSectionComponent implements OnChanges {
  @Input() videoId: Long | null = null; // Input: ID of the video to fetch comments for
  @Input() comments: CommentResponse[] = []; // Input: Array of top-level comments for display
  @Output() commentAdded = new EventEmitter<void>(); // Output: Emits when a new comment is successfully added

  newCommentContent: string = ''; // Model for the new comment input field

  isAuthenticated$ = this.authService.currentUser$.pipe(
    map(user => !!user)
  );

  constructor(private commentService: CommentService, public authService: AuthService) { }

  // Lifecycle hook for detecting changes in input properties
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['comments']) {
      // Logic to handle when the `comments` input array changes.
      // This is useful if comments need client-side sorting or processing.
      // Currently, the backend is expected to return sorted comments.
    }
  }

  // Handles submission of a new top-level comment.
  submitComment(): void {
    if (!this.newCommentContent.trim() || !this.videoId) {
      alert('Comment cannot be empty.'); // Use a custom modal in a real app
      return;
    }

    // Call the comment service to add the new comment
    this.commentService.addComment(this.videoId, this.newCommentContent).subscribe(
      () => {
        this.newCommentContent = ''; // Clear the input field after successful submission
        this.commentAdded.emit(); // Notify the parent component (VideoPlayerComponent) to refresh comments
      },
      (error) => {
        console.error('Error adding comment:', error);
        alert('Failed to add comment. Please try again.'); // Display error message
      }
    );
  }

  // Event handler for when a reply is added within a child CommentComponent.
  onReplyAdded(): void {
    // This method is called by a child `app-comment` when a reply is successfully added.
    // It emits to the parent `VideoPlayerComponent` to trigger a full comment list refresh,
    // ensuring both top-level comments and updated replies are displayed.
    this.commentAdded.emit();
  }
}
