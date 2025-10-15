export interface ResourceAvailability {
  id?: number;
  date: string; // ISO date string
  startTime: string; // "HH:mm:ss"
  endTime: string;   // "HH:mm:ss"
  status: string;    // e.g., 'available', 'booked'
  resourceId: number;
}