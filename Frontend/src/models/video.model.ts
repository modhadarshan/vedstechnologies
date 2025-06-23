import { Long } from "./types";

export interface VideoResponse {
  id: Long;
  title: string;
  description: string;
  category: string;
  createdAt: string; // ISO string date for creation timestamp
  viewCount: number;
  thumbnailData?: Uint8Array; // Optional binary data for the video thumbnail
  youtubeChannel?: { // Youtube channel details, marked as optional
    id: Long;
    name: string;
  };
}
export interface VideoUploadRequest {
  title: string;
  description: string;
  category: string;
}
