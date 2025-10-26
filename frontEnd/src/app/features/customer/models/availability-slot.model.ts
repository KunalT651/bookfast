export interface AvailabilitySlot {
  id?: number;
  resourceId?: number;
  date: string; // ISO date string (e.g., '2025-10-26')
  startTime: string; // '09:00'
  endTime: string;   // '10:00'
  status?: string; // 'available', 'booked', 'unavailable'
}
