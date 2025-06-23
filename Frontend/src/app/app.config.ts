import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from '../app/app.routes'; // Import your routes array
import { CommonModule } from '@angular/common'; // Provides common directives like NgIf, NgFor, NgClass and pipes like AsyncPipe, DatePipe
import { FormsModule, ReactiveFormsModule } from '@angular/forms'; // Provides NgModel, FormGroup, etc.

// Import services which are often provided at the root level (though providedIn: 'root' handles this automatically for new services)
import { AuthService } from '../services/auth.service';
import { ThemeService } from '../services/theme.service';
import { VideoService } from '../services/video.service';
import { CommentService } from '../services/comment.service';
import { ChannelService } from '../services/channel.service';

// If you have an AuthInterceptor, make sure to import and provide it
// import { AuthInterceptor } from './interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    // Provide the router with your defined application routes
    provideRouter(routes),
    // Provide HttpClient for making HTTP requests and potentially register interceptors
    provideHttpClient(), // If using interceptors: provideHttpClient(withInterceptors([AuthInterceptor])),
    // Import common Angular modules that contain directives and pipes used widely
    // These are provided to the entire application via importProvidersFrom
    // This allows directives/pipes from these modules to be used by any standalone component without individual imports.
    importProvidersFrom(CommonModule, FormsModule, ReactiveFormsModule),
    // Explicitly list services if they don't have `providedIn: 'root'` or if you need to override their default providing mechanism
    AuthService,
    ThemeService,
    VideoService,
    CommentService,
    ChannelService,
  ]
};
