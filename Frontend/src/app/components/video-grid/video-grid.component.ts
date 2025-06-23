import { Component, Input } from '@angular/core';
import { CommonModule, NgFor } from '@angular/common'; // Import necessary CommonModule parts
import { VideoCardComponent } from '../../components/video-card/video-card.component'; // Import child component
import { VideoResponse } from '../../../models/video.model';

@Component({
  selector: 'app-video-grid',
  templateUrl: './video-grid.component.html',
  styleUrls: ['./video-grid.component.css'],
  standalone: true, // IMPORTANT: Mark component as standalone
  imports: [
    CommonModule, // Provides *ngFor
    VideoCardComponent, // Make VideoCardComponent available in this component's template
    NgFor // Explicitly import for clarity (optional, included in CommonModule)
  ]
})
export class VideoGridComponent {
  @Input() videos: VideoResponse[] = []; // Input property to receive an array of video data
}
