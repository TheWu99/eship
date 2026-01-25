/**
 * Rate shopping service with AI-driven logic
 */

import { Rate, Address, Package } from '../../common/interfaces';
import { CarrierType, Currency } from '../../common/types';
import { CARRIER_RELIABILITY_SCORES } from '../../common/constants';
import { BusinessRule, RateSelectionEngine } from './RateSelectionEngine';

export interface RateRequest {
  from: Address;
  to: Address;
  packages: Package[];
  includeCarriers?: CarrierType[];
  excludeCarriers?: CarrierType[];
}

export class RateShoppingService {
  /**
   * Fetch rates from multiple carriers
   */
  async fetchRates(request: RateRequest): Promise<Rate[]> {
    // In a real implementation, this would call actual carrier APIs
    // For now, we'll simulate rates
    const rates: Rate[] = [];

    const carriers: CarrierType[] = [
      'USPS',
      'UPS',
      'FedEx',
      'DHL',
    ] as CarrierType[];
    const filteredCarriers = carriers.filter((carrier) => {
      if (
        request.includeCarriers &&
        !request.includeCarriers.includes(carrier)
      ) {
        return false;
      }
      if (
        request.excludeCarriers &&
        request.excludeCarriers.includes(carrier)
      ) {
        return false;
      }
      return true;
    });

    for (const carrier of filteredCarriers) {
      rates.push(...this.simulateCarrierRates(carrier, request));
    }

    return rates;
  }

  /**
   * Get the best rate based on business rules
   */
  async getBestRate(
    request: RateRequest,
    rule: BusinessRule
  ): Promise<Rate | null> {
    const rates = await this.fetchRates(request);
    return RateSelectionEngine.selectBestRate(rates, rule);
  }

  /**
   * AI-driven rate optimization
   * Analyzes historical data, shipping patterns, and carrier performance
   */
  async optimizeRateSelection(
    request: RateRequest,
    historicalData?: {
      carrierPerformance: Record<CarrierType, number>;
      avgDeliveryTimes: Record<CarrierType, number>;
    }
  ): Promise<Rate | null> {
    const rates = await this.fetchRates(request);

    // Apply AI-driven optimization
    if (historicalData) {
      rates.forEach((rate) => {
        // Adjust reliability based on historical performance
        if (historicalData.carrierPerformance[rate.carrier]) {
          rate.reliability = historicalData.carrierPerformance[rate.carrier];
        }
        // Adjust estimated days based on actual delivery times
        if (historicalData.avgDeliveryTimes[rate.carrier]) {
          rate.estimatedDays = Math.ceil(
            historicalData.avgDeliveryTimes[rate.carrier]
          );
        }
      });
    }

    // Use balanced approach with AI adjustments
    const rule: BusinessRule = {
      criteria: 'balanced',
    };

    return RateSelectionEngine.selectBestRate(rates, rule);
  }

  /**
   * Simulate carrier rates (in production, this would call actual APIs)
   */
  private simulateCarrierRates(
    carrier: CarrierType,
    request: RateRequest
  ): Rate[] {
    const totalWeight = request.packages.reduce(
      (sum, pkg) => sum + pkg.weight.value,
      0
    );
    const baseRate = totalWeight * 0.5; // Simple calculation

    return [
      {
        carrier,
        service: 'ground',
        cost: baseRate + 5,
        currency: 'USD' as Currency,
        estimatedDays: 5,
        reliability: CARRIER_RELIABILITY_SCORES[carrier] || 80,
        trackingAvailable: true,
        insuranceAvailable: true,
      },
      {
        carrier,
        service: 'express',
        cost: baseRate * 1.5 + 10,
        currency: 'USD' as Currency,
        estimatedDays: 2,
        reliability: CARRIER_RELIABILITY_SCORES[carrier] || 80,
        trackingAvailable: true,
        insuranceAvailable: true,
      },
      {
        carrier,
        service: 'overnight',
        cost: baseRate * 2.5 + 20,
        currency: 'USD' as Currency,
        estimatedDays: 1,
        reliability: (CARRIER_RELIABILITY_SCORES[carrier] || 80) + 5,
        trackingAvailable: true,
        insuranceAvailable: true,
      },
    ];
  }
}
