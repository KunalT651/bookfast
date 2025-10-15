import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ServiceCategoryService } from '../../services/service-category.service';
@Component({
  selector: 'app-service-categories',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './service-categories.component.html',
  styleUrls: ['./service-categories.component.css']
})
export class ServiceCategoriesComponent implements OnInit {
  categories: any[] = [];
  categoryForm: FormGroup;
  editingCategory: any = null;
  error = '';
  success = '';

  constructor(
    private fb: FormBuilder,
    private categoryService: ServiceCategoryService
  ) {
    this.categoryForm = this.fb.group({
      name: ['', Validators.required],
      description: ['']
    });
  }

  ngOnInit() {
    this.loadCategories();
  }

  loadCategories() {
    this.categoryService.getAll().subscribe({
      next: (data) => this.categories = data,
      error: () => this.categories = []
    });
  }

  submit() {
    if (this.categoryForm.invalid) return;
    if (this.editingCategory) {
      this.categoryService.update(this.editingCategory.id, this.categoryForm.value).subscribe({
        next: () => {
          this.success = 'Category updated!';
          this.error = '';
          this.editingCategory = null;
          this.categoryForm.reset();
          this.loadCategories();
        },
        error: () => {
          this.error = 'Update failed';
          this.success = '';
        }
      });
    } else {
      this.categoryService.create(this.categoryForm.value).subscribe({
        next: () => {
          this.success = 'Category added!';
          this.error = '';
          this.categoryForm.reset();
          this.loadCategories();
        },
        error: () => {
          this.error = 'Add failed';
          this.success = '';
        }
      });
    }
  }

  edit(category: any) {
    this.editingCategory = category;
    this.categoryForm.patchValue(category);
  }

  delete(id: number) {
    this.categoryService.delete(id).subscribe({
      next: () => {
        this.success = 'Category deleted!';
        this.error = '';
        this.loadCategories();
      },
      error: () => {
        this.error = 'Delete failed';
        this.success = '';
      }
    });
  }

  cancelEdit() {
    this.editingCategory = null;
    this.categoryForm.reset();
  }
}