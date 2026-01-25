/**
 * Example: LTL Freight Shipment
 * Demonstrates how to rate, quote, and generate BOL for LTL shipments
 */

import { FreightService, Address, Pallet } from '../src';

// Shipper and consignee addresses
const shipperAddress: Address = {
  name: 'ACME Manufacturing',
  company: 'ACME Corp',
  street1: '1000 Industrial Park Way',
  city: 'Detroit',
  state: 'MI',
  postalCode: '48201',
  country: 'US',
  phone: '555-1000',
  email: 'shipping@acme.com',
};

const consigneeAddress: Address = {
  name: 'ABC Distribution',
  company: 'ABC Distribution Center',
  street1: '500 Commerce Drive',
  city: 'Atlanta',
  state: 'GA',
  postalCode: '30301',
  country: 'US',
  phone: '555-2000',
  email: 'receiving@abc.com',
};

async function demonstrateFreight() {
  const freightService = new FreightService();

  console.log('=== LTL Freight Capabilities Demo ===\n');

  // 1. Create a standard pallet
  console.log('1. Creating standard pallet...');
  const pallet: Pallet = freightService.createStandardPallet('standard');
  pallet.weight = { value: 850, unit: 'lb' };
  pallet.hazmat = false;
  pallet.stackable = true;

  console.log(`Pallet created:`);
  console.log(
    `  Dimensions: ${pallet.dimensions.length}" x ${pallet.dimensions.width}" x ${pallet.dimensions.height}"`
  );
  console.log(`  Weight: ${pallet.weight.value} lb`);
  console.log(`  Type: ${pallet.type}`);

  // 2. Calculate freight class
  console.log('\n2. Calculating freight class based on density...');
  const freightClass = freightService.calculateFreightClass(pallet);
  console.log(`  Freight Class: ${freightClass}`);

  // 3. Validate pallet
  console.log('\n3. Validating pallet specifications...');
  const validation = freightService.validatePallet(pallet);
  console.log(`  Valid: ${validation.valid}`);
  if (!validation.valid) {
    console.log('  Errors:');
    validation.errors.forEach((error) => console.log(`    - ${error}`));
  }

  // 4. Get freight quotes
  console.log('\n4. Getting LTL freight quotes...');
  const quotes = await freightService.getFreightQuote({
    origin: shipperAddress,
    destination: consigneeAddress,
    pallets: [pallet],
    accessorials: ['liftgate', 'inside_delivery'],
    declaredValue: 5000,
  });

  console.log(`\nReceived ${quotes.length} quotes:\n`);
  quotes.forEach((quote, index) => {
    console.log(`Quote ${index + 1}: ${quote.carrier}`);
    console.log(`  Base Rate: $${quote.baseRate.toFixed(2)}`);
    console.log(`  Fuel Surcharge: $${quote.fuelSurcharge.toFixed(2)}`);
    console.log(`  Accessorials: $${quote.accessorialCharges.toFixed(2)}`);
    console.log(`  Total Cost: $${quote.totalCost.toFixed(2)}`);
    console.log(`  Transit Days: ${quote.transitDays}`);
    console.log(
      `  Quote Expires: ${quote.quoteExpiresAt.toLocaleDateString()}\n`
    );
  });

  // 5. Generate Bill of Lading
  console.log('5. Generating Bill of Lading (BOL)...');
  const bol = await freightService.generateBOL({
    shipperRef: 'PO-12345',
    shipper: shipperAddress,
    consignee: consigneeAddress,
    pallets: [pallet],
    freightClass,
    specialInstructions: 'Deliver between 8AM-5PM. Call before delivery.',
  });

  console.log(`\nBOL Generated:`);
  console.log(`  BOL ID: ${bol.id}`);
  console.log(`  PRO Number: ${bol.proNumber}`);
  console.log(`  Shipper Reference: ${bol.shipperRef}`);
  console.log(`  Total Weight: ${bol.totalWeight.value} ${bol.totalWeight.unit}`);
  console.log(`  Freight Class: ${bol.freightClass}`);
  console.log(`  Number of Pallets: ${bol.pallets.length}`);
  console.log(`  Special Instructions: ${bol.specialInstructions}`);
  console.log(`  Document URL: ${bol.documentUrl}`);
  console.log(`  Generated: ${bol.generatedAt.toLocaleString()}`);

  // 6. Track freight shipment
  console.log('\n6. Tracking freight shipment...');
  if (bol.proNumber) {
    const tracking = await freightService.trackFreightShipment(bol.proNumber);
    console.log(`\nTracking Information (PRO: ${tracking.proNumber}):`);
    console.log(`  Status: ${tracking.status}`);
    console.log(`  Current Location: ${tracking.currentLocation}`);
    console.log(
      `  Estimated Delivery: ${tracking.estimatedDelivery?.toLocaleDateString()}`
    );
    console.log(`\n  Recent Events:`);
    tracking.events.forEach((event) => {
      console.log(
        `    ${event.date.toLocaleString()} - ${event.location}: ${event.description}`
      );
    });
  }

  // 7. Rate a single pallet
  console.log('\n7. Quick pallet rating...');
  const palletRate = await freightService.ratePalletShipment(
    shipperAddress,
    consigneeAddress,
    pallet
  );
  console.log(`  Freight Class: ${palletRate.freightClass}`);
  console.log(`  Estimated Cost: $${palletRate.estimatedCost.toFixed(2)}`);
  console.log(`  Transit Days: ${palletRate.transitDays}`);
}

// Run the demo
demonstrateFreight().catch(console.error);
