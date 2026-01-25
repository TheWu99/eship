/**
 * Application constants
 */

export const CARRIER_RELIABILITY_SCORES = {
  USPS: 85,
  UPS: 92,
  FedEx: 90,
  DHL: 88,
  LTL: 80,
  Freight: 75,
};

export const STANDARD_BOX_SIZES = [
  { name: 'Small', length: 8, width: 6, height: 4 },
  { name: 'Medium', length: 12, width: 10, height: 6 },
  { name: 'Large', length: 16, width: 12, height: 8 },
  { name: 'XLarge', length: 20, width: 16, height: 12 },
  { name: 'XXLarge', length: 24, width: 18, height: 18 },
];

export const DIMENSIONAL_WEIGHT_DIVISORS = {
  USPS: 166,
  UPS: 139,
  FedEx: 139,
  DHL: 139,
};

export const FREIGHT_CLASS_DENSITY_RANGES = {
  '50': { min: 50, max: Infinity },
  '55': { min: 35, max: 50 },
  '60': { min: 30, max: 35 },
  '65': { min: 22.5, max: 30 },
  '70': { min: 15, max: 22.5 },
  '77.5': { min: 13.5, max: 15 },
  '85': { min: 12, max: 13.5 },
  '92.5': { min: 10.5, max: 12 },
  '100': { min: 9, max: 10.5 },
  '110': { min: 8, max: 9 },
  '125': { min: 7, max: 8 },
  '150': { min: 6, max: 7 },
  '175': { min: 5, max: 6 },
  '200': { min: 4, max: 5 },
  '250': { min: 3, max: 4 },
  '300': { min: 2, max: 3 },
  '400': { min: 1, max: 2 },
  '500': { min: 0, max: 1 },
};

export const INSURANCE_RATES = {
  base: 0.01, // 1% of declared value
  minimum: 2.50,
  maximum: 50000,
};

export const PALLET_STANDARDS = {
  standard: { length: 48, width: 40, height: 6 },
  euro: { length: 47.2, width: 31.5, height: 6 },
};
