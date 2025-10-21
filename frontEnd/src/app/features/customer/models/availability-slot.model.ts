export interface AvailabilitySlot {
  id?: number;
  resourceId?: number;
  dayOfWeek: string; // e.g., 'MONDAY', 'TUESDAY', etc.
  startTime: string; // '09:00'
  endTime: string;   // '10:00'
  date?: string; // ISO date string
}
