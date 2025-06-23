
// src/app/components/video-player/video-player.component.ts
// This component displays a video, its details, and manages comments and channel interactions.
// It's a standalone component and manages its own imports.
import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { CommonModule, NgIf, NgFor, AsyncPipe, DatePipe, NgClass } from '@angular/common'; // Import CommonModule parts
import { ActivatedRoute, Router, RouterLink } from '@angular/router'; // Import ActivatedRoute, Router, RouterLink
import { VideoService } from '../../../services/video.service';
import { AuthService } from '../../../services/auth.service';
import { VideoResponse } from '../../../models/video.model';
import { CommentService } from '../../../services/comment.service';
import { CommentSectionComponent } from '../comment-section/comment-section.component'; // Import child component
import { CommentResponse } from '../../../models/comment.model';
import { Subscription, interval, BehaviorSubject, Observable, of } from 'rxjs'; // Import 'of'
import { switchMap, startWith } from 'rxjs/operators';
import { ChannelService } from '../../../services/channel.service';
import { Long } from '../../../models/types'; // Import Long type

@Component({
  selector: 'app-video-player',
  templateUrl: './video-player.component.html',
  styleUrls: ['./video-player.component.css'],
  standalone: true, // IMPORTANT: Mark component as standalone
  imports: [
    CommonModule, // Provides NgIf, NgFor, AsyncPipe, DatePipe, NgClass
    RouterLink, // Provides routerLink directive for placeholder recommended videos
    CommentSectionComponent, // Make CommentSectionComponent available
    NgIf, NgFor, AsyncPipe, DatePipe, NgClass // Explicitly import for clarity (optional, included in CommonModule)
  ]
})
export class VideoPlayerComponent implements OnInit, OnDestroy {
  @ViewChild('videoPlayer') videoPlayer!: ElementRef<HTMLVideoElement>; // Reference to the HTML video element

  videoId: Long | null = null; // ID of the currently playing video
  video: VideoResponse | null = null; // Video details object
  videoUrl: string | null = null; // URL for video streaming
  isLoadingVideo = true; // Loading state for video details
  errorLoadingVideo: string | null = null; // Error message if video fails to load

  isLiked = false; // Flag if current user liked the video
  likeCount: number = 0; // Total like count for the video
  isSubscribedToChannel = false; // Flag if current user subscribed to the channel
  channelSubscriberCount: number = 0; // Total subscriber count for the channel

  comments: CommentResponse[] = []; // List of comments for the video
  newCommentContent: string = ''; // Content for a new comment

  private likeStatusSubscription: Subscription | undefined; // Subscription for like status updates
  private commentRefreshSubscription: Subscription | undefined; // Subscription for comment refresh interval
  private channelSubscriptionStatusSubscription: Subscription | undefined; // Subscription for channel subscription status
  private channelSubscriberCountSubscription: Subscription | undefined; // Subscription for channel subscriber count

  // BehaviorSubjects to trigger reactive updates for comments, likes, and channel data
  private refreshComments$ = new BehaviorSubject<void>(undefined);
  private refreshLikes$ = new BehaviorSubject<void>(undefined);
  private refreshChannelData$ = new BehaviorSubject<void>(undefined);


  constructor(
    private route: ActivatedRoute, // ActivatedRoute to get route parameters
    private router: Router, // Router for navigation
    private videoService: VideoService, // VideoService for video-related API calls
    public authService: AuthService, // AuthService for authentication and current user status (made public for template access)
    private commentService: CommentService, // CommentService for comment-related API calls
    private channelService: ChannelService // ChannelService for channel-related API calls
  ) { }

  ngOnInit(): void {
    // Subscribe to route parameter changes to get the video ID
    this.route.paramMap.subscribe(params => {
      const id = params.get('videoId');
      if (id) {
        this.videoId = +id; // Convert string ID to number
        this.loadVideoDetails(this.videoId); // Load video details
        this.loadComments(this.videoId); // Load comments for the video

        // Reactive stream for video like status and count
        this.likeStatusSubscription = this.refreshLikes$.pipe(
          startWith(undefined), // Trigger initial load
          switchMap(() => this.authService.isAuthenticated$ ?
            this.authService.isAuthenticated$.pipe( // Use isAuthenticated$ observable
              switchMap(isAuth => isAuth ? this.videoService.hasUserLikedVideo(this.videoId!) : of(false) as Observable<boolean>)
            ) : of(false) as Observable<boolean> // Return Observable<boolean> type
          )
        ).subscribe((liked: boolean) => this.isLiked = liked); // Explicitly type 'liked'

        this.refreshLikes$.pipe(
          startWith(undefined), // Trigger initial load
          switchMap(() => this.videoService.getVideoLikeCount(this.videoId!))
        ).subscribe((count: number) => this.likeCount = count); // Explicitly type 'count'

        // Reactive stream for channel subscription status and subscriber count
        this.channelSubscriptionStatusSubscription = this.refreshChannelData$.pipe(
          startWith(undefined),
          switchMap(() => {
            if (this.video?.youtubeChannel?.id) { // Check if channel exists on video object
              return this.authService.isAuthenticated$.pipe( // Use isAuthenticated$ observable
                switchMap(isAuth => {
                  if (isAuth) {
                    return this.channelService.isSubscribed(this.video!.youtubeChannel!.id);
                  }
                  return of(false) as Observable<boolean>;
                })
              );
            }
            return of(false) as Observable<boolean>; // Return Observable<boolean> type if no channel or not authenticated
          })
        ).subscribe((subscribed: boolean) => this.isSubscribedToChannel = subscribed); // Explicitly type 'subscribed'

        this.channelSubscriberCountSubscription = this.refreshChannelData$.pipe(
          startWith(undefined),
          switchMap(() => {
            if (this.video?.youtubeChannel?.id) {
              return this.channelService.getSubscriberCount(this.video.youtubeChannel.id);
            }
            return of(0) as Observable<number>; // Return Observable<number> type
          })
        ).subscribe((count: number) => this.channelSubscriberCount = count); // Explicitly type 'count'

        // Set up an interval to refresh comments periodically (e.g., every 30 seconds)
        this.commentRefreshSubscription = interval(30000).subscribe(() => this.refreshComments$.next());
        this.refreshComments$.subscribe(() => this.loadComments(this.videoId!));


      } else {
        this.router.navigate(['/']); // Redirect to home if no videoId is provided in the URL
      }
    });
  }

  // Unsubscribe from all active subscriptions to prevent memory leaks
  ngOnDestroy(): void {
    this.likeStatusSubscription?.unsubscribe();
    this.commentRefreshSubscription?.unsubscribe();
    this.channelSubscriptionStatusSubscription?.unsubscribe();
    this.channelSubscriberCountSubscription?.unsubscribe();
  }

  // Loads detailed information about the video from the backend.
  loadVideoDetails(videoId: Long): void {
    this.isLoadingVideo = true;
    this.videoService.getVideoById(videoId).subscribe(
      (data) => {
        this.video = data;
        // Construct the video stream URL based on the video ID
        this.videoUrl = `http://localhost:8080/api/videos/${this.video.id}/stream`;
        this.isLoadingVideo = false;
        // Trigger initial refresh for channel data once video details are loaded
        this.refreshChannelData$.next();
      },
      (error) => {
        console.error('Error loading video:', error);
        this.errorLoadingVideo = 'Could not load video. It might not exist or there was a server error.';
        this.isLoadingVideo = false;
      }
    );
  }

  // Loads comments associated with the current video from the backend.
  loadComments(videoId: Long): void {
    this.commentService.getCommentsForVideo(videoId).subscribe(
      (comments) => {
        this.comments = comments;
      },
      (error) => {
        console.error('Error loading comments:', error);
        // Implement better error handling/feedback for the user
      }
    );
  }

  // Toggles the like status for the current video.
  onLikeToggle(): void {
    this.authService.isAuthenticated$.subscribe(isAuth => { // Use isAuthenticated$
      if (!isAuth) {
        // In a real application, consider a custom modal instead of alert()
        alert('Please log in to like videos.');
        this.router.navigate(['/login']);
        return;
      }
      if (this.videoId) {
        this.videoService.toggleVideoLike(this.videoId).subscribe(
          (liked) => {
            this.isLiked = liked;
            this.refreshLikes$.next(); // Trigger refresh for like count and status
          },
          (error) => {
            console.error('Error toggling like:', error);
            alert('Failed to toggle like. Please try again.');
          }
        );
      }
    });
  }

  // Toggles the subscription status for the video's channel.
  onSubscribeToggle(): void {
    this.authService.isAuthenticated$.subscribe(isAuth => { // Use isAuthenticated$
      if (!isAuth) {
        // In a real application, consider a custom modal instead of alert()
        alert('Please log in to subscribe to channels.');
        this.router.navigate(['/login']);
        return;
      }
      // Ensure video and its channel ID exist before proceeding
      if (this.video?.youtubeChannel?.id) {
        // Prevent a user from subscribing to their own channel
        if (this.authService.currentUserValue?.youtubeChannel?.id === this.video.youtubeChannel.id) {
          alert('You cannot subscribe to your own channel.');
          return;
        }

        if (this.isSubscribedToChannel) {
          // If already subscribed, unsubscribe
          this.channelService.unsubscribeFromChannel(this.video.youtubeChannel.id).subscribe(
            () => {
              this.isSubscribedToChannel = false;
              this.refreshChannelData$.next(); // Trigger refresh for subscriber count
            },
            (error) => {
              console.error('Error unsubscribing:', error);
              alert('Failed to unsubscribe. Please try again.');
            }
          );
        } else {
          // If not subscribed, subscribe
          this.channelService.subscribeToChannel(this.video.youtubeChannel.id).subscribe(
            () => {
              this.isSubscribedToChannel = true;
              this.refreshChannelData$.next(); // Trigger refresh for subscriber count
            },
            (error) => {
              console.error('Error subscribing:', error);
              alert('Failed to subscribe. Please try again.');
            }
          );
        }
      }
    });
  }

  // Adds a new comment to the video.
  addComment(): void {
    this.authService.isAuthenticated$.subscribe(isAuth => { // Use isAuthenticated$
      if (!isAuth) {
        // In a real application, consider a custom modal instead of alert()
        alert('Please log in to comment.');
        this.router.navigate(['/login']);
        return;
      }
      // Ensure videoId is available and comment content is not empty
      if (this.videoId && this.newCommentContent.trim()) {
        this.commentService.addComment(this.videoId, this.newCommentContent).subscribe(
          () => {
            this.newCommentContent = ''; // Clear the comment input field
            this.refreshComments$.next(); // Trigger refresh for the comments list
          },
          (error) => {
            console.error('Error adding comment:', error);
            alert('Failed to add comment. Please try again.');
          }
        );
      }
    });
  }
}
