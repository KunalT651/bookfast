import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DemoFormService } from '../../services/demo-form.service';
import { DemoForm } from '../../models/demo-form.model';

@Component({
  selector: 'app-demo-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './demo-form.html',
  styleUrls: ['./demo-form.css']
})
export class DemoFormComponent implements OnInit {
  form: DemoForm = { field1: '', field2: '' };
  forms: DemoForm[] = [];

  constructor(private demoFormService: DemoFormService) {}

  ngOnInit() {
    this.loadForms();
  }

  submit() {
    this.demoFormService.save(this.form).subscribe(() => {
      this.form = { field1: '', field2: '' };
      this.loadForms();
    });
  }

  loadForms() {
    this.demoFormService.getAll().subscribe(data => this.forms = data);
  }
}