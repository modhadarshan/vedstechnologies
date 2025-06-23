

// src/app/components/comment/comment.component.ts
// This component displays a single comment, its likes, and allows users to reply and like.
// It's a standalone component and manages its own imports, including recursive import for replies.
import { Component, Input, OnInit, EventEmitter, Output, OnDestroy } from '@angular/core';
import { CommonModule, NgIf, NgFor, AsyncPipe, DatePipe, NgClass } from '@angular/common'; // Import CommonModule parts
import { FormsModule } from '@angular/forms'; // Import FormsModule for ngModel
import { Router } from '@angular/router'; // Import Router for navigation (e.g., to login)
import { CommentResponse } from '../../../models/comment.model';
import { CommentService } from '../../../services/comment.service';
import { AuthService } from '../../../services/auth.service';
import { Long } from '../../../models/types'; // Import Long type
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs'; // Import 'of' from 'rxjs'
import { switchMap, startWith, map } from 'rxjs/operators'; // Import map

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.css'],
  standalone: true, // IMPORTANT: Mark component as standalone
  imports: [
    CommonModule, // Provides NgIf, NgFor, AsyncPipe, DatePipe, NgClass
    FormsModule, // Provides ngModel
    NgIf, NgFor, AsyncPipe, DatePipe, NgClass, // Explicitly import for clarity (optional, included in CommonModule)
    // Important for recursive components: The component itself needs to be imported if it renders itself.
    // Angular handles this circular dependency when `standalone: true` is used.
    // This is how `<app-comment>` inside its own template works.
    // Note: The CommentComponent for replies is handled directly by the component's imports.
    // No explicit self-import statement is needed here if it's implicitly resolved by Angular's compiler,
    // but I'll add a comment to indicate its recursive nature.
    // CommentComponent // Self-import for recursive rendering of replies
  ]
})
export class CommentComponent implements OnInit, OnDestroy {
  @Input() comment!: CommentResponse; // Input: The comment object to display
  @Input() mainVideoId: Long | null = null; // Input: The ID of the main video this comment belongs to (crucial for replies)
  @Output() replyAdded = new EventEmitter<void>(); // Output: Emits when a reply is successfully added to this comment

  showReplyForm: boolean = false; // Controls visibility of the reply input form
  newReplyContent: string = ''; // Model for the new reply input field
  showReplies: boolean = false; // Controls visibility of nested replies
  replies: CommentResponse[] = []; // Array to store fetched replies for this comment

  isLiked: boolean = false; // Flag if the current user liked this comment
  likeCount: number = 0; // Total like count for this comment

  private refreshLikeStatus$ = new BehaviorSubject<void>(undefined); // Subject to trigger like status/count refresh
  private refreshReplies$ = new BehaviorSubject<void>(undefined); // Subject to trigger replies refresh
  private commentLikeSubscription: Subscription | undefined; // Subscription for comment like data
  private repliesSubscription: Subscription | undefined; // Subscription for replies data

  isAuthenticated$ = this.authService.currentUser$.pipe(
    map(user => !!user)
  );

  constructor(
    private commentService: CommentService, // CommentService for comment/reply API calls
    public authService: AuthService, // AuthService for authentication status (made public for template access)
    private router: Router // Router for navigation
  ) { }

  ngOnInit(): void {
    // Initial check for like status and count of the comment
    this.commentLikeSubscription = this.refreshLikeStatus$.pipe(
      startWith(undefined), // Triggers an immediate initial load
      switchMap(() => this.isAuthenticated$.pipe( // Use isAuthenticated$ observable
        switchMap(isAuth => isAuth ? this.commentService.hasUserLikedComment(this.comment.id) : of(false) as Observable<boolean>)
      ))
    ).subscribe((liked: boolean) => this.isLiked = liked);

    this.refreshLikeStatus$.pipe(
      startWith(undefined), // Triggers an immediate initial load
      switchMap(() => this.commentService.getCommentLikeCount(this.comment.id))
    ).subscribe((count: number) => this.likeCount = count);

    // Load replies when `showReplies` becomes true or on explicit `refreshReplies$` trigger.
    // It fetches replies only if `showReplies` is true.
    this.repliesSubscription = this.refreshReplies$.pipe(
      startWith(undefined), // Ensures initial check if already showing replies or loading
      switchMap(() => this.showReplies ? this.commentService.getRepliesForComment(this.comment.id) : of([]) as Observable<CommentResponse[]>)
    ).subscribe((replies: CommentResponse[]) => this.replies = replies);

    // If the comment initially has replies, load them to display the "View replies" count.
    // If you want to auto-expand replies if `comment.replyCount > 0`, you could set `this.showReplies = true` here.
    // For now, it only loads when `toggleReplies` is clicked.
  }

  // Unsubscribe from all active subscriptions to prevent memory leaks when component is destroyed.
  ngOnDestroy(): void {
    this.commentLikeSubscription?.unsubscribe();
    this.repliesSubscription?.unsubscribe();
  }

  // Toggles the visibility of the reply input form.
  toggleReplyForm(): void {
    this.isAuthenticated$.subscribe(isAuth => { // Use isAuthenticated$
      if (!isAuth) {
        // In a real application, consider a custom modal instead of alert()
        alert('Please log in to reply to comments.');
        this.router.navigate(['/login']);
        return;
      }
      this.showReplyForm = !this.showReplyForm;
    });
  }

  // Handles submission of a new reply to this comment.
  submitReply(): void {
    if (!this.newReplyContent.trim() || this.mainVideoId === null) {
      alert('Reply cannot be empty and requires the main video context.');
      return;
    }
    // Call the comment service to add the reply.
    // The `mainVideoId` is passed down from `VideoPlayerComponent` to `CommentSectionComponent`
    // and then to `CommentComponent` to ensure the backend receives the correct video context for the reply.
    this.commentService.addComment(this.mainVideoId, this.newReplyContent, this.comment.id).subscribe(
      () => {
        this.newReplyContent = ''; // Clear the reply input field
        this.showReplyForm = false; // Hide the reply form
        this.showReplies = true; // Automatically expand replies to show the new one
        this.refreshReplies$.next(); // Trigger refresh for the replies list
        this.replyAdded.emit(); // Notify the parent comment section to refresh its list (e.g., update reply counts)
      },
      (error) => {
        console.error('Error adding reply:', error);
        alert('Failed to add reply. Please try again.'); // Display error message
      }
    );
  }

  // Toggles the like status for this comment.
  toggleCommentLike(): void {
    this.isAuthenticated$.subscribe(isAuth => { // Use isAuthenticated$
      if (!isAuth) {
        // In a real application, consider a custom modal instead of alert()
        alert('Please log in to like comments.');
        this.router.navigate(['/login']);
        return;
      }
      this.commentService.toggleCommentLike(this.comment.id).subscribe(
        (liked: boolean) => { // Explicitly type 'liked'
          this.isLiked = liked;
          this.refreshLikeStatus$.next(); // Trigger refresh for like count and status
        },
        (error) => {
          console.error('Error toggling comment like:', error);
          alert('Failed to toggle like. Please try again.');
        }
      );
    });
  }

  // Toggles the visibility of nested replies.
  toggleReplies(): void {
    this.showReplies = !this.showReplies;
    if (this.showReplies) {
      this.refreshReplies$.next(); // Trigger loading replies if they are now visible
    }
  }

  // Formats time since for better readability (e.g., "5 minutes ago").
  timeSince(date: string): string {
    const seconds = Math.floor((new Date().getTime() - new Date(date).getTime()) / 1000);

    let interval = seconds / 31536000;
    if (interval > 1) {
      return Math.floor(interval) + ' years ago';
    }
    interval = seconds / 2592000;
    if (interval > 1) {
      return Math.floor(interval) + ' months ago';
    }
    interval = seconds / 86400;
    if (interval > 1) {
      return Math.floor(interval) + ' days ago';
    }
    interval = seconds / 3600;
    if (interval > 1) {
      return Math.floor(interval) + ' hours ago';
    }
    interval = seconds / 60;
    if (interval > 1) {
      return Math.floor(interval) + ' minutes ago';
    }
    return Math.floor(seconds) + ' seconds ago';
  }
}
