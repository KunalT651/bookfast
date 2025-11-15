export interface AvailabilitySlot {
  id?: number;
  date: string; // YYYY-MM-DD
  startTime: string; // HH:MM
  endTime: string; // HH:MM
  status: 'available' | 'booked' | 'unavailable';
  reason?: string; // Optional reason for unavailability
}
