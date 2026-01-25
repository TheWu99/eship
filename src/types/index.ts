/**
 * Core type definitions for the eship platform
 */

export interface Address {
  street: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
}

export interface Location {
  latitude: number;
  longitude: number;
}

export interface Warehouse {
  id: string;
  name: string;
  address: Address;
  location: Location;
  isActive: boolean;
}

export interface CarrierRate {
  carrier: string;
  service: string;
  baseRate: number;
  estimatedDays: number;
  currency: string;
}

export interface Label {
  id: string;
  trackingNumber: string;
  carrier: string;
  service: string;
  rate: number;
  labelUrl?: string;
  createdAt: Date;
  isSandbox: boolean;
}

export interface TrackingEvent {
  id: string;
  trackingNumber: string;
  status: string;
  location: string;
  timestamp: Date;
  description: string;
  isSandbox: boolean;
}

export interface MarkupConfig {
  id: string;
  carrier?: string;
  service?: string;
  markupType: 'percentage' | 'flat';
  markupValue: number;
  isActive: boolean;
}

export interface BillingTransaction {
  id: string;
  accountId: string;
  carrier: string;
  amount: number;
  currency: string;
  labelId: string;
  timestamp: Date;
  description: string;
}

export interface ProofOfDelivery {
  id: string;
  trackingNumber: string;
  signatureImageUrl?: string;
  deliveredAt: Date;
  recipientName: string;
  confirmationDocument?: string;
}

export interface ShipmentRequest {
  fromAddress: Address;
  toAddress: Address;
  weight: number;
  dimensions: {
    length: number;
    width: number;
    height: number;
  };
  carrier?: string;
  service?: string;
}

export interface RoutingResult {
  warehouseId: string;
  warehouse: Warehouse;
  distance: number;
  estimatedTransitTime: number;
}

export interface SandboxConfig {
  enabled: boolean;
  autoGenerateTracking: boolean;
  simulateDeliveryDelay: number; // in milliseconds
}
