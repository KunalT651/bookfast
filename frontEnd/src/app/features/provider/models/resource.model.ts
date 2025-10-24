import { AvailabilitySlot } from './availability-slot.model';

export interface Resource {
  id?: number;
  providerId: number;
  name: string;
  description: string;
  specialization: string;
  status: string;
  price?: number;
  experienceYears?: number;
  phone?: string;
  email?: string;
  imageUrl?: string;
  tags?: string[];
  rating?: number;
  reviews?: Review[];
  availabilitySlots?: AvailabilitySlot[];
}

export interface Review {
  customerName: string;
  rating: number;
  comment: string;
  date: string;
}