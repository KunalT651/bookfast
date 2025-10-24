export interface Review {
  id?: number;
  customerName: string;
  customerId?: number;
  rating: number;
  comment: string;
  date: string;
  resourceId?: number;
  resourceName?: string;
}
