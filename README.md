YouTube Clone Backend

This repository contains the backend application for a YouTube-like video sharing platform, built using Spring Boot 3.3.1. It provides RESTful APIs for essential functionalities such as user authentication, video uploads, video playback, comments, likes, and channel subscriptions.

Features

User Management: Register, Login.

Channel Management: Create default channel on registration, update channel profile (name, description), subscribe/unsubscribe to channels, view subscriber counts.

Video Management: Upload videos (with titles, descriptions, categories, and optional thumbnails), view individual video details, stream video content, paginate through all videos, update video metadata, like/unlike videos, and get like counts.

Comment System: Add top-level comments and nested replies to videos, like/unlike comments, and view comment like counts.

File Storage: Local filesystem storage for video and thumbnail files.

In-Memory Database: Uses H2 database for quick setup and development.

CORS Configuration: Enabled for seamless integration with a frontend application (e.g., Angular).

Technology Stack

Backend: Spring Boot (Java 17)

Web Framework: Spring Web

Data Access: Spring Data JPA, Hibernate

Database: H2 Database (in-memory, for development)

Build Tool: Maven

Utility: Project Lombok (for boilerplate code reduction)

Validation: Jakarta Bean Validation

How to Run

Prerequisites:

Java Development Kit (JDK) 17 or higher

Maven 3.6+

Clone the Repository (if applicable):

# If this were a real repository
# git clone <your-repo-url>
# cd youtube-clone-backend


Build the Project:
Navigate to the root directory of the backend project (where pom.xml is located) and run:

mvn clean install


Run the Application:
After a successful build, you can start the Spring Boot application using:

mvn spring-boot:run


The application will start on http://localhost:8080.

Access Uploaded Files:
Videos and thumbnails will be stored in an uploads directory created in the project's root.

API Endpoints (Brief Overview)

POST /api/auth/register: Register a new user.

POST /api/auth/login: Log in a user.

PUT /api/users/profile: Update authenticated user's channel profile.

POST /api/videos: Upload a new video.

GET /api/videos/{videoId}: Get video details.

GET /api/videos/{videoId}/stream: Stream video file.

GET /api/videos: Get all videos (paginated).

PUT /api/videos/{videoId}: Update video details.

POST /api/videos/{videoId}/like: Toggle video like.

POST /api/comments/video/{videoId}: Add comment/reply to a video.

GET /api/comments/video/{videoId}: Get comments for a video.

POST /api/channels/{channelId}/subscribe: Subscribe to a channel.

Note on Authentication: For demonstration purposes, authentication is handled via X-User-Email and X-User-Password HTTP headers. This is highly insecure for production environments and should be replaced with robust token-based authentication (e.g., JWT) using Spring Security.
