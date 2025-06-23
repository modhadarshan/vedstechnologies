
// src/app/models/comment.model.ts

import { Long } from "./types";

// Interface for the comment request body when adding a new comment or reply.
export interface CommentRequest {
  content: string;
  parentCommentId: Long | null; // Optional: ID of the parent comment if this is a reply
}

// Interface representing a comment response from the backend.
export interface CommentResponse {
  id: Long;
  content: string;
  createdAt: string; // ISO string date for creation timestamp
  userProfile?: { // User profile of the comment author, marked as optional
    id: Long;
    email: string;
    youtubeChannel?: { // Youtube channel of the comment author, marked as optional
      id: Long;
      name: string;
    };
  };
  parentCommentId: Long | null; // ID of the parent comment if this is a reply
  likeCount: number; // Number of likes for this comment
  userLiked: boolean; // Flag if the current user has liked this comment
  replies?: CommentResponse[]; // Nested replies to this comment
  replyCount: number; // Total count of replies (often for UI display without loading all replies)
}
