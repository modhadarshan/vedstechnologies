import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-register',
  imports: [FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {

  email = '';
  password = '';
  message = '';

  constructor(private auth: AuthService, private router: Router) { }

  register() {
    this.auth.register({ email: this.email, password: this.password }).subscribe({
      next: (res) => {
        this.message = res;
        this.router.navigate(['/login']);
      },
      error: (err) => this.message = 'Error: ' + err.error
    });
  }

}
