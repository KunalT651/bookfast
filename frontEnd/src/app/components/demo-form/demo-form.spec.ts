import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DemoForm } from './demo-form';

describe('DemoForm', () => {
  let component: DemoForm;
  let fixture: ComponentFixture<DemoForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DemoForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DemoForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
