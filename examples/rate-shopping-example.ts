/**
 * Example: Smart Rate Shopping
 * Demonstrates how to use the rate shopping service with business rules
 */

import {
  RateShoppingService,
  RateSelectionEngine,
  Address,
  Package,
  BusinessRule,
} from '../src';

// Sample addresses
const originAddress: Address = {
  name: 'ACME Corp',
  company: 'ACME Corporation',
  street1: '123 Warehouse Blvd',
  city: 'Los Angeles',
  state: 'CA',
  postalCode: '90001',
  country: 'US',
  phone: '555-0100',
  email: 'shipping@acme.com',
};

const destinationAddress: Address = {
  name: 'John Smith',
  street1: '456 Main Street',
  street2: 'Apt 2B',
  city: 'New York',
  state: 'NY',
  postalCode: '10001',
  country: 'US',
  phone: '555-0200',
  email: 'john@example.com',
};

// Sample package
const samplePackage: Package = {
  type: 'box',
  dimensions: {
    length: 12,
    width: 10,
    height: 8,
    unit: 'in',
  },
  weight: {
    value: 5,
    unit: 'lb',
  },
  declaredValue: 150,
};

async function demonstrateRateShopping() {
  const rateService = new RateShoppingService();

  console.log('=== Smart Rate Shopping Demo ===\n');

  // 1. Fetch all available rates
  console.log('1. Fetching rates from all carriers...');
  const allRates = await rateService.fetchRates({
    from: originAddress,
    to: destinationAddress,
    packages: [samplePackage],
  });

  console.log(`Found ${allRates.length} rates:\n`);
  allRates.forEach((rate) => {
    console.log(
      `  ${rate.carrier} ${rate.service}: $${rate.cost.toFixed(2)} (${rate.estimatedDays} days, ${rate.reliability}% reliable)`
    );
  });

  // 2. Find cheapest option
  console.log('\n2. Finding cheapest option...');
  const cheapestRule: BusinessRule = {
    criteria: 'cheapest',
  };
  const cheapest = await rateService.getBestRate(
    {
      from: originAddress,
      to: destinationAddress,
      packages: [samplePackage],
    },
    cheapestRule
  );
  console.log(
    `Cheapest: ${cheapest?.carrier} ${cheapest?.service} - $${cheapest?.cost.toFixed(2)}`
  );

  // 3. Find fastest option
  console.log('\n3. Finding fastest option...');
  const fastestRule: BusinessRule = {
    criteria: 'fastest',
  };
  const fastest = await rateService.getBestRate(
    {
      from: originAddress,
      to: destinationAddress,
      packages: [samplePackage],
    },
    fastestRule
  );
  console.log(
    `Fastest: ${fastest?.carrier} ${fastest?.service} - ${fastest?.estimatedDays} days`
  );

  // 4. Balanced approach
  console.log('\n4. Finding balanced option (cost + speed + reliability)...');
  const balancedRule: BusinessRule = {
    criteria: 'balanced',
  };
  const balanced = await rateService.getBestRate(
    {
      from: originAddress,
      to: destinationAddress,
      packages: [samplePackage],
    },
    balancedRule
  );
  console.log(
    `Balanced: ${balanced?.carrier} ${balanced?.service} - $${balanced?.cost.toFixed(2)} (${balanced?.estimatedDays} days)`
  );

  // 5. Custom rules with constraints
  console.log('\n5. Custom rule: Must arrive in 3 days, under $30...');
  const customRule: BusinessRule = {
    criteria: 'cheapest',
    maxDays: 3,
    maxCost: 30,
    minReliability: 85,
  };
  const custom = await rateService.getBestRate(
    {
      from: originAddress,
      to: destinationAddress,
      packages: [samplePackage],
    },
    customRule
  );
  if (custom) {
    console.log(
      `Best match: ${custom.carrier} ${custom.service} - $${custom.cost.toFixed(2)} (${custom.estimatedDays} days, ${custom.reliability}% reliable)`
    );
  } else {
    console.log('No rates match the constraints');
  }

  // 6. AI-driven optimization with historical data
  console.log('\n6. AI-driven optimization with historical data...');
  const optimized = await rateService.optimizeRateSelection(
    {
      from: originAddress,
      to: destinationAddress,
      packages: [samplePackage],
    },
    {
      carrierPerformance: {
        USPS: 88,
        UPS: 94,
        FedEx: 91,
        DHL: 87,
        LTL: 80,
        Freight: 75,
      },
      avgDeliveryTimes: {
        USPS: 4.2,
        UPS: 1.8,
        FedEx: 1.5,
        DHL: 2.1,
        LTL: 5,
        Freight: 7,
      },
    }
  );
  console.log(
    `AI Optimized: ${optimized?.carrier} ${optimized?.service} - $${optimized?.cost.toFixed(2)} (${optimized?.estimatedDays} days)`
  );
}

// Run the demo
demonstrateRateShopping().catch(console.error);
