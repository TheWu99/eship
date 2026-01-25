/**
 * LTL and Freight Capabilities Service
 */

import { Pallet, BillOfLading, Address, Weight, Dimensions } from '../../common/interfaces';
import { FreightClass, DimensionUnit, WeightUnit } from '../../common/types';
import { FREIGHT_CLASS_DENSITY_RANGES, PALLET_STANDARDS } from '../../common/constants';

export interface FreightQuoteRequest {
  origin: Address;
  destination: Address;
  pallets: Pallet[];
  accessorials?: string[]; // liftgate, inside delivery, etc.
  declaredValue?: number;
}

export interface FreightQuote {
  id: string;
  carrier: string;
  baseRate: number;
  fuelSurcharge: number;
  accessorialCharges: number;
  totalCost: number;
  transitDays: number;
  quoteExpiresAt: Date;
}

export interface BOLRequest {
  shipperRef?: string;
  shipper: Address;
  consignee: Address;
  billTo?: Address;
  pallets: Pallet[];
  freightClass: FreightClass;
  specialInstructions?: string;
}

export class FreightService {
  /**
   * Calculate freight class based on density
   */
  calculateFreightClass(pallet: Pallet): FreightClass {
    const volume = this.calculateVolume(pallet.dimensions);
    const density = pallet.weight.value / volume;

    // Find matching freight class based on density
    for (const [freightClass, range] of Object.entries(FREIGHT_CLASS_DENSITY_RANGES)) {
      if (density >= range.min && density < range.max) {
        return freightClass as FreightClass;
      }
    }

    return '500'; // Default to highest class if not found
  }

  /**
   * Get freight quote from LTL carriers
   */
  async getFreightQuote(request: FreightQuoteRequest): Promise<FreightQuote[]> {
    const quotes: FreightQuote[] = [];

    // In production, this would call actual LTL carrier APIs
    // For now, simulate quotes from multiple carriers
    const carriers = ['Old Dominion', 'XPO Logistics', 'YRC Freight', 'Estes Express'];

    for (const carrier of carriers) {
      const baseRate = this.calculateBaseRate(request);
      const fuelSurcharge = baseRate * 0.15; // 15% fuel surcharge
      const accessorialCharges = this.calculateAccessorialCharges(request.accessorials || []);

      quotes.push({
        id: this.generateId('FQ'),
        carrier,
        baseRate,
        fuelSurcharge,
        accessorialCharges,
        totalCost: baseRate + fuelSurcharge + accessorialCharges,
        transitDays: this.estimateTransitDays(request.origin, request.destination),
        quoteExpiresAt: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000), // 7 days
      });
    }

    return quotes.sort((a, b) => a.totalCost - b.totalCost);
  }

  /**
   * Generate Bill of Lading (BOL)
   */
  async generateBOL(request: BOLRequest): Promise<BillOfLading> {
    const totalWeight = this.calculateTotalWeight(request.pallets);

    const bol: BillOfLading = {
      id: this.generateId('BOL'),
      shipmentId: this.generateId('FRT'),
      shipperRef: request.shipperRef,
      proNumber: this.generatePRONumber(),
      shipper: request.shipper,
      consignee: request.consignee,
      pallets: request.pallets,
      totalWeight,
      freightClass: request.freightClass,
      specialInstructions: request.specialInstructions,
      generatedAt: new Date(),
      documentUrl: this.generateBOLUrl(),
    };

    // In production, this would:
    // 1. Generate PDF document
    // 2. Store in database
    // 3. Send to carrier via EDI or API
    // 4. Email to shipper and consignee

    return bol;
  }

  /**
   * Rate pallet shipment
   */
  async ratePalletShipment(
    origin: Address,
    destination: Address,
    pallet: Pallet
  ): Promise<{
    freightClass: FreightClass;
    estimatedCost: number;
    transitDays: number;
  }> {
    const freightClass = this.calculateFreightClass(pallet);
    const baseRate = this.calculatePalletBaseRate(pallet, freightClass);
    const distance = this.estimateDistance(origin, destination);
    const estimatedCost = baseRate * (distance / 100); // Simple distance-based pricing

    return {
      freightClass,
      estimatedCost,
      transitDays: this.estimateTransitDays(origin, destination),
    };
  }

  /**
   * Track freight shipment
   */
  async trackFreightShipment(proNumber: string): Promise<{
    proNumber: string;
    status: string;
    currentLocation?: string;
    estimatedDelivery?: Date;
    events: Array<{
      date: Date;
      location: string;
      description: string;
    }>;
  }> {
    // In production, would call carrier tracking API
    return {
      proNumber,
      status: 'in_transit',
      currentLocation: 'Distribution Center - Chicago, IL',
      estimatedDelivery: new Date(Date.now() + 3 * 24 * 60 * 60 * 1000),
      events: [
        {
          date: new Date(Date.now() - 24 * 60 * 60 * 1000),
          location: 'Origin Terminal',
          description: 'Shipment picked up',
        },
        {
          date: new Date(Date.now() - 12 * 60 * 60 * 1000),
          location: 'Distribution Center - Chicago, IL',
          description: 'Arrived at distribution center',
        },
      ],
    };
  }

  /**
   * Validate pallet dimensions and weight
   */
  validatePallet(pallet: Pallet): {
    valid: boolean;
    errors: string[];
  } {
    const errors: string[] = [];

    // Check maximum dimensions
    if (pallet.dimensions.length > 96 || pallet.dimensions.width > 96) {
      errors.push('Pallet dimensions exceed maximum of 96 inches');
    }

    // Check maximum height
    if (pallet.dimensions.height > 96) {
      errors.push('Pallet height exceeds maximum of 96 inches');
    }

    // Check maximum weight (varies by carrier, using 2000 lbs as standard)
    if (pallet.weight.value > 2000) {
      errors.push('Pallet weight exceeds maximum of 2000 lbs');
    }

    return {
      valid: errors.length === 0,
      errors,
    };
  }

  /**
   * Create standard pallet
   */
  createStandardPallet(type: 'standard' | 'euro' = 'standard'): Pallet {
    const standards = PALLET_STANDARDS[type];

    return {
      type,
      dimensions: {
        length: standards.length,
        width: standards.width,
        height: standards.height,
        unit: 'in' as DimensionUnit,
      },
      weight: {
        value: 0,
        unit: 'lb' as WeightUnit,
      },
      stackable: true,
      hazmat: false,
    };
  }

  private calculateVolume(dimensions: Dimensions): number {
    return dimensions.length * dimensions.width * dimensions.height;
  }

  private calculateTotalWeight(pallets: Pallet[]): Weight {
    const total = pallets.reduce((sum, p) => sum + p.weight.value, 0);
    return {
      value: total,
      unit: 'lb' as WeightUnit,
    };
  }

  private calculateBaseRate(request: FreightQuoteRequest): number {
    const totalWeight = request.pallets.reduce((sum, p) => sum + p.weight.value, 0);
    const distance = this.estimateDistance(request.origin, request.destination);
    
    // Simple rate calculation (in production, would use carrier-specific algorithms)
    return (totalWeight / 100) * (distance / 10);
  }

  private calculatePalletBaseRate(pallet: Pallet, freightClass: FreightClass): number {
    // Base rate multipliers by freight class
    const classMultipliers: Record<string, number> = {
      '50': 0.5,
      '55': 0.6,
      '60': 0.7,
      '65': 0.8,
      '70': 0.9,
      '77.5': 1.0,
      '85': 1.1,
      '92.5': 1.2,
      '100': 1.3,
      '110': 1.4,
      '125': 1.5,
      '150': 1.7,
      '175': 2.0,
      '200': 2.5,
      '250': 3.0,
      '300': 3.5,
      '400': 4.0,
      '500': 5.0,
    };

    const multiplier = classMultipliers[freightClass] || 1.0;
    return pallet.weight.value * 0.10 * multiplier;
  }

  private calculateAccessorialCharges(accessorials: string[]): number {
    const rates: Record<string, number> = {
      liftgate: 75,
      inside_delivery: 100,
      residential: 85,
      limited_access: 50,
      appointment: 40,
    };

    return accessorials.reduce((sum, acc) => sum + (rates[acc] || 0), 0);
  }

  private estimateDistance(origin: Address, destination: Address): number {
    // In production, would use actual geocoding and distance calculation
    // For now, return a random distance between 100-1000 miles
    return 100 + Math.random() * 900;
  }

  private estimateTransitDays(origin: Address, destination: Address): number {
    const distance = this.estimateDistance(origin, destination);
    // Rough estimate: 500 miles per day
    return Math.ceil(distance / 500);
  }

  private generateId(prefix: string): string {
    return `${prefix}-${Date.now()}-${Math.random().toString(36).slice(2, 11)}`;
  }

  private generatePRONumber(): string {
    // PRO number format: 9 digits
    return Math.random().toString().slice(2, 11);
  }

  private generateBOLUrl(): string {
    const id = Math.random().toString(36).slice(2, 14);
    return `https://api.eship.com/bol/${id}.pdf`;
  }
}
