/**
 * eShip - Advanced Shipping Management Platform
 * 
 * Main entry point for the eShip application
 */

// Export common types and interfaces
export * from './common/types';
export * from './common/interfaces';
export * from './common/constants';

// Export Rate Shopping services
export { RateShoppingService } from './rate-shopping/services/RateShoppingService';
export { RateSelectionEngine, BusinessRule, SelectionCriteria } from './rate-shopping/services/RateSelectionEngine';

// Export Cartonization services
export { CartonizationService, PackingResult } from './cartonization/services/CartonizationService';

// Export Insurance services
export { InsuranceService, ClaimsService, InsuranceOptions, ClaimRequest } from './insurance/services/InsuranceService';

// Export Returns services
export { ReturnsService, ReturnRequest, ReturnLabel, ReturnPortalSession } from './returns/services/ReturnsService';

// Export Pickups services
export { PickupsService, ManifestingService, PickupRequest, ManifestRequest } from './pickups/services/PickupsService';

// Export Freight services
export { FreightService, FreightQuoteRequest, FreightQuote, BOLRequest } from './freight/services/FreightService';
 * eShip Platform - Main Entry Point
 * E-commerce shipping platform with advanced features
 */

import { SandboxEnvironment } from './sandbox';
import { MonetizationEngine } from './monetization';
import { ConsolidatedBilling } from './billing';
import { MultiWarehouseRouter } from './routing';
import { ProofOfDeliveryService } from './pod';

/**
 * Main eShip Platform class that orchestrates all modules
 */
export class EShipPlatform {
  public sandbox: SandboxEnvironment;
  public monetization: MonetizationEngine;
  public billing: ConsolidatedBilling;
  public routing: MultiWarehouseRouter;
  public pod: ProofOfDeliveryService;

  constructor(options: {
    sandboxEnabled?: boolean;
    accountName?: string;
  } = {}) {
    // Initialize all modules
    this.sandbox = new SandboxEnvironment({
      enabled: options.sandboxEnabled !== undefined ? options.sandboxEnabled : true,
      autoGenerateTracking: true,
      simulateDeliveryDelay: 5000
    });

    this.monetization = new MonetizationEngine();
    this.billing = new ConsolidatedBilling(options.accountName);
    this.routing = new MultiWarehouseRouter();
    this.pod = new ProofOfDeliveryService();
  }

  /**
   * Get platform status and health check
   */
  getStatus(): {
    sandbox: { enabled: boolean };
    billing: { balance: number };
    routing: { warehouses: number };
    pod: { records: number };
  } {
    return {
      sandbox: {
        enabled: this.sandbox.isEnabled()
      },
      billing: {
        balance: this.billing.getBalance()
      },
      routing: {
        warehouses: this.routing.getActiveWarehouses().length
      },
      pod: {
        records: this.pod.getAllPODRecords().length
      }
    };
  }

  /**
   * Reset all platform data (useful for testing)
   */
  reset(): void {
    this.sandbox.clearAll();
    this.pod.clearAll();
    // Note: Billing and routing data is not cleared to preserve account state
  }
}

// Export all modules for direct access if needed
export { SandboxEnvironment } from './sandbox';
export { MonetizationEngine } from './monetization';
export { ConsolidatedBilling } from './billing';
export { MultiWarehouseRouter } from './routing';
export { ProofOfDeliveryService } from './pod';
export * from './types';

// Create and export a default instance
export const platform = new EShipPlatform();

// For CommonJS compatibility
export default EShipPlatform;
