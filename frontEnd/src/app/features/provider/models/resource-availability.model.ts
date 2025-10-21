export interface ResourceAvailability {
  id?: number;
  resourceId: number;
  date: string; // 'YYYY-MM-DD'
  dayOfWeek: string; // e.g., 'MONDAY', 'TUESDAY', etc.
  startTime: string; // '09:00'
  endTime: string;   // '10:00'
}