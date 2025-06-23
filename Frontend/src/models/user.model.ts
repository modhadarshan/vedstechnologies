import { Long } from "./types";

export interface UserResponse {
  id: Long;
  email: string;
  createdAt: string; // ISO string date for creation timestamp
  youtubeChannel?: { // Youtube channel details, marked as optional
    id: Long;
    name: string;
    description: string;
  };
}
