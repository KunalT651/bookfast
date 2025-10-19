import { Review } from './resource.model';

export interface ServiceResource {
  id?: number;
  providerId: number;
  name: string;
  address: string;
  description: string;
  tags: string[];
  rating?: number;
  reviews?: Review[];
  // availability: AvailabilitySlot[]; // Removed, type not defined
  imageUrl?: string;
}