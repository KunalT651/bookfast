import { ResourceAvailability } from './resource-availability.model';

export interface ServiceResource {
  id?: number;
  name: string;
  contactNumber: string;
  specialization: string;
  description: string;
  status: string;
  providerId: number;
  availabilities?: ResourceAvailability[];
}