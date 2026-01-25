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
