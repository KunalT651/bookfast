import { AvailabilitySlot, Review } from './resource.model'; // Adjust import if needed

export interface ServiceResource {
  id?: number;
  providerId: number;
  name: string;
  address: string;
  description: string;
  tags: string[];
  rating?: number;
  reviews?: Review[];
  availability: AvailabilitySlot[];
  imageUrl?: string;
}