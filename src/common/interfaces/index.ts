/**
 * Common interfaces for the eShip application
 */

import { CarrierType, ServiceLevel, PackageType, WeightUnit, DimensionUnit, Currency, ClaimStatus, ShipmentStatus, FreightClass } from '../types';

export interface Address {
  name: string;
  company?: string;
  street1: string;
  street2?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  phone?: string;
  email?: string;
}

export interface Dimensions {
  length: number;
  width: number;
  height: number;
  unit: DimensionUnit;
}

export interface Weight {
  value: number;
  unit: WeightUnit;
}

export interface Package {
  id?: string;
  type: PackageType;
  dimensions: Dimensions;
  weight: Weight;
  items?: ShipmentItem[];
  declaredValue?: number;
}

export interface ShipmentItem {
  sku: string;
  name: string;
  quantity: number;
  weight: Weight;
  dimensions?: Dimensions;
  value: number;
  category?: string;
}

export interface Rate {
  carrier: CarrierType;
  service: ServiceLevel;
  cost: number;
  currency: Currency;
  estimatedDays: number;
  reliability?: number; // 0-100 score
  trackingAvailable: boolean;
  insuranceAvailable: boolean;
  metadata?: Record<string, any>;
}

export interface Shipment {
  id: string;
  from: Address;
  to: Address;
  packages: Package[];
  rate?: Rate;
  status: ShipmentStatus;
  trackingNumber?: string;
  labelUrl?: string;
  createdAt: Date;
  updatedAt: Date;
}

export interface Insurance {
  id: string;
  shipmentId: string;
  carrier: CarrierType;
  coverage: number;
  premium: number;
  currency: Currency;
  policyNumber?: string;
  issuedAt: Date;
}

export interface Claim {
  id: string;
  insuranceId: string;
  shipmentId: string;
  status: ClaimStatus;
  amount: number;
  description: string;
  filedAt: Date;
  resolvedAt?: Date;
  resolution?: string;
}

export interface Pallet {
  id?: string;
  type: 'standard' | 'euro' | 'custom';
  dimensions: Dimensions;
  weight: Weight;
  freightClass?: FreightClass;
  stackable: boolean;
  hazmat: boolean;
}

export interface BillOfLading {
  id: string;
  shipmentId: string;
  shipperRef?: string;
  proNumber?: string;
  shipper: Address;
  consignee: Address;
  pallets: Pallet[];
  totalWeight: Weight;
  freightClass: FreightClass;
  specialInstructions?: string;
  generatedAt: Date;
  documentUrl?: string;
}

export interface Manifest {
  id: string;
  carrier: CarrierType;
  date: Date;
  shipments: string[]; // shipment IDs
  documentUrl?: string;
  scanFormUrl?: string;
  createdAt: Date;
}

export interface Pickup {
  id: string;
  carrier: CarrierType;
  address: Address;
  date: Date;
  readyTime: string;
  closeTime: string;
  shipments: string[]; // shipment IDs
  confirmationNumber?: string;
  specialInstructions?: string;
  status: 'scheduled' | 'confirmed' | 'completed' | 'cancelled';
}
