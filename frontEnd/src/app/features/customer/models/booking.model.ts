export interface Booking {
	id: number;
	resourceId: number;
	slotId: number;
	customerName: string;
	customerEmail: string;
	customerPhone: string;
	status: 'pending' | 'confirmed' | 'cancelled';
	paymentStatus: 'unpaid' | 'paid';
	amount?: number;
}
