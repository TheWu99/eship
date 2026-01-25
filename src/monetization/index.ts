/**
 * Monetization & Markup Engine Module
 * Provides ability to set custom markups on carrier rates for reselling shipping services
 */

import { CarrierRate, MarkupConfig } from '../types';

export class MonetizationEngine {
  private markupConfigs: Map<string, MarkupConfig>;

  constructor() {
    this.markupConfigs = new Map();
  }

  /**
   * Add or update a markup configuration
   */
  setMarkupConfig(config: MarkupConfig): void {
    this.markupConfigs.set(config.id, config);
  }

  /**
   * Get a markup configuration by ID
   */
  getMarkupConfig(id: string): MarkupConfig | undefined {
    return this.markupConfigs.get(id);
  }

  /**
   * Get all active markup configurations
   */
  getActiveMarkupConfigs(): MarkupConfig[] {
    return Array.from(this.markupConfigs.values()).filter(config => config.isActive);
  }

  /**
   * Remove a markup configuration
   */
  removeMarkupConfig(id: string): boolean {
    return this.markupConfigs.delete(id);
  }

  /**
   * Apply markup to a carrier rate based on configured rules
   */
  applyMarkup(rate: CarrierRate): CarrierRate {
    const applicableMarkups = this.findApplicableMarkups(rate.carrier, rate.service);
    
    let finalRate = rate.baseRate;
    
    // Apply all applicable markups
    for (const markup of applicableMarkups) {
      if (markup.markupType === 'percentage') {
        finalRate += (finalRate * markup.markupValue / 100);
      } else if (markup.markupType === 'flat') {
        finalRate += markup.markupValue;
      }
    }

    return {
      ...rate,
      baseRate: finalRate
    };
  }

  /**
   * Apply markup to multiple carrier rates
   */
  applyMarkupToRates(rates: CarrierRate[]): CarrierRate[] {
    return rates.map(rate => this.applyMarkup(rate));
  }

  /**
   * Calculate profit margin for a rate
   */
  calculateProfit(originalRate: number, markedUpRate: number): number {
    return markedUpRate - originalRate;
  }

  /**
   * Calculate profit percentage
   */
  calculateProfitPercentage(originalRate: number, markedUpRate: number): number {
    if (originalRate === 0) return 0;
    return ((markedUpRate - originalRate) / originalRate) * 100;
  }

  /**
   * Find applicable markups for a carrier and service
   */
  private findApplicableMarkups(carrier: string, service: string): MarkupConfig[] {
    const activeConfigs = this.getActiveMarkupConfigs();
    
    // Priority: specific carrier + service > specific carrier > global
    const markups: MarkupConfig[] = [];
    
    // Add carrier-specific service markups
    const carrierServiceMarkup = activeConfigs.find(
      config => config.carrier === carrier && config.service === service
    );
    if (carrierServiceMarkup) {
      markups.push(carrierServiceMarkup);
    }
    
    // Add carrier-level markups (if no specific service markup)
    if (markups.length === 0) {
      const carrierMarkup = activeConfigs.find(
        config => config.carrier === carrier && !config.service
      );
      if (carrierMarkup) {
        markups.push(carrierMarkup);
      }
    }
    
    // Add global markups (if no carrier-specific markup)
    if (markups.length === 0) {
      const globalMarkup = activeConfigs.find(
        config => !config.carrier && !config.service
      );
      if (globalMarkup) {
        markups.push(globalMarkup);
      }
    }
    
    return markups;
  }

  /**
   * Create a default markup configuration
   */
  createDefaultMarkup(carrier?: string, service?: string, percentage: number = 15): MarkupConfig {
    const config: MarkupConfig = {
      id: `markup_${Date.now()}`,
      markupType: 'percentage',
      markupValue: percentage,
      isActive: true
    };
    
    if (carrier !== undefined) {
      config.carrier = carrier;
    }
    
    if (service !== undefined) {
      config.service = service;
    }
    
    this.setMarkupConfig(config);
    return config;
  }

  /**
   * Disable a markup configuration
   */
  disableMarkup(id: string): void {
    const config = this.markupConfigs.get(id);
    if (config) {
      config.isActive = false;
      this.markupConfigs.set(id, config);
    }
  }

  /**
   * Enable a markup configuration
   */
  enableMarkup(id: string): void {
    const config = this.markupConfigs.get(id);
    if (config) {
      config.isActive = true;
      this.markupConfigs.set(id, config);
    }
  }

  /**
   * Get all markup configurations (active and inactive)
   */
  getAllMarkupConfigs(): MarkupConfig[] {
    return Array.from(this.markupConfigs.values());
  }
}
