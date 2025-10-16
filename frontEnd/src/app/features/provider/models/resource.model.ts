export interface Resource {
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
  price?: number;
  experienceYears?: number;
  phone?: string;
  email?: string;
  specialization: string;
  status: string;
}

export interface AvailabilitySlot {
  id?: number;
  date: string;
  startTime: string;
  endTime: string;
  status: 'available' | 'booked' | 'unavailable';
}

export interface Review {
  id?: number;
  customerId: number;
  customerName: string;
  rating: number;
  comment: string;
  date: string;
}