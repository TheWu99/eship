/**
 * Common types for the eShip application
 */

export type CarrierType = 'USPS' | 'UPS' | 'FedEx' | 'DHL' | 'LTL' | 'Freight';

export type ServiceLevel = 'ground' | 'express' | 'overnight' | 'two-day' | 'standard' | 'freight';

export type PackageType = 'envelope' | 'box' | 'tube' | 'pak' | 'pallet' | 'custom';

export type WeightUnit = 'lb' | 'kg' | 'oz' | 'g';

export type DimensionUnit = 'in' | 'cm' | 'ft' | 'm';

export type Currency = 'USD' | 'EUR' | 'GBP' | 'CAD';

export type ClaimStatus = 'pending' | 'in_review' | 'approved' | 'denied' | 'paid';

export type ShipmentStatus = 'created' | 'picked_up' | 'in_transit' | 'out_for_delivery' | 'delivered' | 'exception' | 'returned';

export type FreightClass = '50' | '55' | '60' | '65' | '70' | '77.5' | '85' | '92.5' | '100' | '110' | '125' | '150' | '175' | '200' | '250' | '300' | '400' | '500';
