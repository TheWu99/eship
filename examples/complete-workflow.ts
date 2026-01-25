/**
 * Comprehensive Integration Example
 * Demonstrates how all eShip features work together in a complete workflow
 */

import {
  RateShoppingService,
  CartonizationService,
  InsuranceService,
  ClaimsService,
  ReturnsService,
  PickupsService,
  ManifestingService,
  Address,
  ShipmentItem,
  Shipment,
} from '../src';

async function completeShippingWorkflow() {
  console.log('=== Complete eShip Workflow Demo ===\n');

  // Setup addresses
  const warehouseAddress: Address = {
    name: 'eCommerce Warehouse',
    company: 'ShipFast Inc',
    street1: '100 Logistics Lane',
    city: 'Los Angeles',
    state: 'CA',
    postalCode: '90001',
    country: 'US',
    phone: '555-1234',
    email: 'warehouse@shipfast.com',
  };

  const customerAddress: Address = {
    name: 'Jane Doe',
    street1: '789 Residential Ave',
    street2: 'Unit 5C',
    city: 'Boston',
    state: 'MA',
    postalCode: '02101',
    country: 'US',
    phone: '555-5678',
    email: 'jane@example.com',
  };

  // Order items
  const orderItems: ShipmentItem[] = [
    {
      sku: 'LAPTOP-001',
      name: 'Premium Laptop',
      quantity: 1,
      weight: { value: 5, unit: 'lb' },
      dimensions: { length: 15, width: 10, height: 2, unit: 'in' },
      value: 1299.99,
      category: 'electronics',
    },
    {
      sku: 'MOUSE-002',
      name: 'Wireless Mouse',
      quantity: 2,
      weight: { value: 0.3, unit: 'lb' },
      dimensions: { length: 5, width: 3, height: 2, unit: 'in' },
      value: 29.99,
      category: 'electronics',
    },
    {
      sku: 'CABLE-003',
      name: 'USB-C Cable',
      quantity: 1,
      weight: { value: 0.2, unit: 'lb' },
      dimensions: { length: 6, width: 4, height: 1, unit: 'in' },
      value: 19.99,
      category: 'accessories',
    },
  ];

  console.log('Order Details:');
  console.log(`  Customer: ${customerAddress.name}`);
  console.log(`  Items: ${orderItems.length}`);
  console.log(
    `  Total Value: $${orderItems.reduce((sum, item) => sum + item.value * item.quantity, 0).toFixed(2)}\n`
  );

  // STEP 1: Cartonization - Optimize packaging
  console.log('STEP 1: Optimizing Package Configuration...');
  const cartonService = new CartonizationService();
  const packingResult = cartonService.optimizePackaging(orderItems);

  console.log(`✓ Optimized to ${packingResult.packages.length} package(s)`);
  console.log(`  Packing efficiency: ${packingResult.efficiency.toFixed(1)}%`);
  console.log(`  Total weight: ${packingResult.totalWeight.toFixed(2)} lb\n`);

  // STEP 2: Rate Shopping - Find best shipping rate
  console.log('STEP 2: Finding Best Shipping Rate...');
  const rateService = new RateShoppingService();
  const bestRate = await rateService.getBestRate(
    {
      from: warehouseAddress,
      to: customerAddress,
      packages: packingResult.packages,
    },
    {
      criteria: 'balanced', // Balance cost, speed, and reliability
      maxDays: 5,
      minReliability: 85,
    }
  );

  if (bestRate) {
    console.log(`✓ Selected: ${bestRate.carrier} ${bestRate.service}`);
    console.log(`  Cost: $${bestRate.cost.toFixed(2)}`);
    console.log(`  Delivery: ${bestRate.estimatedDays} days`);
    console.log(`  Reliability: ${bestRate.reliability}%\n`);
  }

  // STEP 3: Insurance - Purchase shipping insurance
  console.log('STEP 3: Purchasing Shipping Insurance...');
  const insuranceService = new InsuranceService();
  const totalValue = orderItems.reduce(
    (sum, item) => sum + item.value * item.quantity,
    0
  );
  const insurance = await insuranceService.purchaseInsurance({
    shipmentId: 'SHP-2024-001',
    declaredValue: totalValue,
  });

  console.log(`✓ Insurance purchased`);
  console.log(`  Policy: ${insurance.policyNumber}`);
  console.log(`  Coverage: $${insurance.coverage.toFixed(2)}`);
  console.log(`  Premium: $${insurance.premium.toFixed(2)}\n`);

  // STEP 4: Create shipment
  const shipment: Shipment = {
    id: 'SHP-2024-001',
    from: warehouseAddress,
    to: customerAddress,
    packages: packingResult.packages,
    rate: bestRate || undefined,
    status: 'created',
    trackingNumber: '1Z999AA10123456784',
    labelUrl: 'https://api.eship.com/labels/SHP-2024-001.pdf',
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  console.log('STEP 4: Shipment Created');
  console.log(`✓ Shipment ID: ${shipment.id}`);
  console.log(`  Tracking: ${shipment.trackingNumber}\n`);

  // STEP 5: Schedule pickup
  console.log('STEP 5: Scheduling Carrier Pickup...');
  const pickupService = new PickupsService();
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);

  const pickup = await pickupService.schedulePickup({
    carrier: bestRate?.carrier || 'UPS',
    address: warehouseAddress,
    date: tomorrow,
    readyTime: '09:00',
    closeTime: '17:00',
    shipments: [shipment.id],
    packageCount: packingResult.packages.length,
    totalWeight: packingResult.totalWeight,
  });

  console.log(`✓ Pickup scheduled`);
  console.log(`  Confirmation: ${pickup.confirmationNumber}`);
  console.log(`  Date: ${pickup.date.toLocaleDateString()}`);
  console.log(`  Window: ${pickup.readyTime} - ${pickup.closeTime}\n`);

  // STEP 6: Generate manifest
  console.log('STEP 6: Generating End-of-Day Manifest...');
  const manifestService = new ManifestingService();
  const closeout = await manifestService.closeOutDay(
    bestRate?.carrier || 'UPS',
    [shipment]
  );

  console.log(`✓ Manifest generated`);
  console.log(`  Manifest ID: ${closeout.manifest.id}`);
  console.log(`  Document: ${closeout.manifest.documentUrl}`);
  if (closeout.scanForm) {
    console.log(`  SCAN Form: ${closeout.scanForm.formUrl}`);
  }
  console.log();

  // STEP 7: Handle potential claim
  console.log('STEP 7: Demonstrating Claims Process...');
  const claimsService = new ClaimsService();
  const claim = await claimsService.fileClaim({
    insuranceId: insurance.id,
    shipmentId: shipment.id,
    damageDescription: 'Package damaged in transit',
    claimAmount: 50,
    supportingDocuments: ['https://example.com/photo1.jpg'],
  });

  console.log(`✓ Claim filed`);
  console.log(`  Claim ID: ${claim.id}`);
  console.log(`  Status: ${claim.status}`);

  // Auto-process small claim
  const processedClaim = await claimsService.autoProcessClaim(claim, insurance);
  console.log(`✓ Claim auto-processed`);
  console.log(`  New Status: ${processedClaim.status}`);
  console.log(`  Resolution: ${processedClaim.resolution}\n`);

  // STEP 8: Handle return
  console.log('STEP 8: Processing Customer Return...');
  const returnsService = new ReturnsService();

  // Create return portal session
  const returnSession = await returnsService.createReturnPortalSession(
    'CUST-12345',
    'ORD-2024-001'
  );
  console.log(`✓ Return portal session created`);
  console.log(`  Session ID: ${returnSession.sessionId}`);

  // Generate scan-based return label
  const returnLabel = await returnsService.generateScanBasedLabel({
    originalShipmentId: shipment.id,
    returnFrom: customerAddress,
    returnTo: warehouseAddress,
    reason: 'Changed mind',
  });

  console.log(`✓ Return label generated`);
  console.log(`  Return ID: ${returnLabel.returnId}`);
  console.log(`  Tracking: ${returnLabel.trackingNumber}`);
  console.log(`  QR Code: ${returnLabel.qrCodeUrl}`);
  console.log(`  Scan Code: ${returnLabel.scanCode}\n`);

  // STEP 9: Summary
  console.log('=== Workflow Complete ===\n');
  console.log('Summary:');
  console.log(`✓ Optimized packaging (${packingResult.efficiency.toFixed(1)}% efficient)`);
  console.log(`✓ Found best rate: $${bestRate?.cost.toFixed(2)}`);
  console.log(`✓ Purchased insurance: $${insurance.coverage.toFixed(2)} coverage`);
  console.log(`✓ Scheduled pickup for ${tomorrow.toLocaleDateString()}`);
  console.log(`✓ Generated manifest and SCAN form`);
  console.log(`✓ Claim auto-processed`);
  console.log(`✓ Return label ready for customer`);
  console.log();
  console.log('Total shipping cost breakdown:');
  console.log(`  Shipping: $${bestRate?.cost.toFixed(2) || '0.00'}`);
  console.log(`  Insurance: $${insurance.premium.toFixed(2)}`);
  console.log(`  Packaging: $${packingResult.totalCost.toFixed(2)}`);
  console.log(
    `  TOTAL: $${((bestRate?.cost || 0) + insurance.premium + packingResult.totalCost).toFixed(2)}`
  );
}

// Run the complete workflow
completeShippingWorkflow().catch(console.error);
