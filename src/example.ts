/**
 * Example demonstration of all eShip platform features
 */

import { EShipPlatform } from './index';

async function demonstratePlatform() {
  console.log('=== eShip Platform Demo ===\n');

  // Initialize platform
  const eship = new EShipPlatform({
    sandboxEnabled: true,
    accountName: 'Demo Shipping Account'
  });

  console.log('1. Platform Status:');
  console.log(eship.getStatus());
  console.log('');

  // === SANDBOX ENVIRONMENT ===
  console.log('2. Sandbox Environment Demo:');
  
  const shipmentRequest = {
    fromAddress: {
      street: '123 Warehouse St',
      city: 'New York',
      state: 'NY',
      zipCode: '10001',
      country: 'US'
    },
    toAddress: {
      street: '456 Customer Ave',
      city: 'Los Angeles',
      state: 'CA',
      zipCode: '90001',
      country: 'US'
    },
    weight: 2.5,
    dimensions: { length: 12, width: 10, height: 8 },
    carrier: 'USPS',
    service: 'Priority'
  };

  const label = await eship.sandbox.createMockLabel(shipmentRequest);
  console.log('Created mock label:', {
    trackingNumber: label.trackingNumber,
    carrier: label.carrier,
    rate: label.rate,
    isSandbox: label.isSandbox
  });

  // Simulate some tracking events
  await eship.sandbox.simulateTrackingEvent(label.trackingNumber, 'PICKED_UP', 'New York, NY');
  await eship.sandbox.simulateTrackingEvent(label.trackingNumber, 'IN_TRANSIT', 'Memphis, TN');
  
  const events = eship.sandbox.getTrackingEvents(label.trackingNumber);
  console.log(`Tracking events (${events.length}):`, events.map(e => ({
    status: e.status,
    location: e.location,
    timestamp: e.timestamp.toISOString()
  })));
  console.log('');

  // === MONETIZATION ===
  console.log('3. Monetization & Markup Demo:');
  
  // Create markup configurations
  const uspsMarkup = eship.monetization.createDefaultMarkup('USPS', undefined, 15);
  console.log('Created USPS 15% markup:', uspsMarkup.id);

  eship.monetization.setMarkupConfig({
    id: 'fedex_ground_flat',
    carrier: 'FedEx',
    service: 'Ground',
    markupType: 'flat',
    markupValue: 2.50,
    isActive: true
  });

  // Apply markup to rates
  const originalRate = {
    carrier: 'USPS',
    service: 'Priority',
    baseRate: 10.00,
    estimatedDays: 2,
    currency: 'USD'
  };

  const markedUpRate = eship.monetization.applyMarkup(originalRate);
  console.log('Rate markup:');
  console.log('  Original: $' + originalRate.baseRate.toFixed(2));
  console.log('  Marked up: $' + markedUpRate.baseRate.toFixed(2));
  console.log('  Profit: $' + eship.monetization.calculateProfit(originalRate.baseRate, markedUpRate.baseRate).toFixed(2));
  console.log('');

  // === CONSOLIDATED BILLING ===
  console.log('4. Consolidated Billing Demo:');
  
  // Add funds
  eship.billing.addFunds(1000.00);
  console.log('Added $1000 to master account');
  console.log('Current balance: $' + eship.billing.getBalance().toFixed(2));

  // Process some charges
  const txn1 = eship.billing.processCharge('USPS', 11.50, label.id, 'Priority Mail');
  const txn2 = eship.billing.processCharge('FedEx', 22.75, 'label_2', 'FedEx Ground');
  const txn3 = eship.billing.processCharge('UPS', 18.25, 'label_3', 'UPS Ground');

  console.log('Processed 3 transactions');
  console.log('New balance: $' + eship.billing.getBalance().toFixed(2));
  console.log('Total spent: $' + eship.billing.getTotalSpent().toFixed(2));

  // Generate reconciliation report
  const report = eship.billing.generateReconciliationReport(
    new Date(Date.now() - 86400000), // Yesterday
    new Date() // Today
  );

  console.log('\nReconciliation Report:');
  console.log('  Total transactions:', report.summary.totalTransactions);
  console.log('  Total amount: $' + report.summary.totalAmount.toFixed(2));
  console.log('  By carrier:');
  report.summary.byCarrier.forEach((amount, carrier) => {
    console.log(`    ${carrier}: $${amount.toFixed(2)}`);
  });
  console.log('');

  // === MULTI-WAREHOUSE ROUTING ===
  console.log('5. Multi-Warehouse Routing Demo:');
  
  // Add warehouses
  eship.routing.addWarehouse({
    id: 'wh_east',
    name: 'East Coast DC',
    address: {
      street: '100 Port St',
      city: 'New York',
      state: 'NY',
      zipCode: '10001',
      country: 'US'
    },
    location: { latitude: 40.7128, longitude: -74.0060 },
    isActive: true
  });

  eship.routing.addWarehouse({
    id: 'wh_central',
    name: 'Central DC',
    address: {
      street: '200 Commerce Dr',
      city: 'Chicago',
      state: 'IL',
      zipCode: '60601',
      country: 'US'
    },
    location: { latitude: 41.8781, longitude: -87.6298 },
    isActive: true
  });

  eship.routing.addWarehouse({
    id: 'wh_west',
    name: 'West Coast DC',
    address: {
      street: '300 Harbor Blvd',
      city: 'Los Angeles',
      state: 'CA',
      zipCode: '90001',
      country: 'US'
    },
    location: { latitude: 34.0522, longitude: -118.2437 },
    isActive: true
  });

  const destination = {
    street: '789 Customer Ln',
    city: 'Denver',
    state: 'CO',
    zipCode: '80201',
    country: 'US'
  };

  const routing = eship.routing.findOptimalWarehouse(destination);
  if (routing) {
    console.log('Optimal warehouse for Denver, CO:');
    console.log('  Warehouse:', routing.warehouse.name);
    console.log('  Distance:', routing.distance, 'miles');
    console.log('  Estimated transit:', routing.estimatedTransitTime, 'days');
  }

  const options = eship.routing.findWarehouseOptions(destination, 3);
  console.log('\nTop 3 warehouse options:');
  options.forEach((opt, idx) => {
    console.log(`  ${idx + 1}. ${opt.warehouse.name} - ${opt.distance} miles (${opt.estimatedTransitTime} days)`);
  });
  console.log('');

  // === PROOF OF DELIVERY ===
  console.log('6. Digital Proof of Delivery Demo:');
  
  const pod = await eship.pod.retrievePOD({
    trackingNumber: label.trackingNumber,
    carrier: 'USPS'
  });

  if (pod) {
    console.log('Retrieved POD:');
    console.log('  Tracking:', pod.trackingNumber);
    console.log('  Delivered at:', pod.deliveredAt.toISOString());
    console.log('  Recipient:', pod.recipientName);
    console.log('  Signature URL:', pod.signatureImageUrl);
    console.log('  Document URL:', pod.confirmationDocument);
  }

  // Batch retrieve
  const batchPODs = await eship.pod.batchRetrievePOD([
    { trackingNumber: 'USPS123456', carrier: 'USPS' },
    { trackingNumber: 'FEDEX789012', carrier: 'FedEx' }
  ]);

  console.log('\nBatch POD retrieval:', batchPODs.size, 'PODs retrieved');

  const podReport = eship.pod.generatePODReport([
    label.trackingNumber,
    'USPS123456',
    'FEDEX789012'
  ]);

  console.log('POD Report:');
  console.log('  Available:', podReport.available + '/' + podReport.totalRequested);
  console.log('');

  // === FINAL STATUS ===
  console.log('7. Final Platform Status:');
  console.log(eship.getStatus());
  console.log('\n=== Demo Complete ===');
}

// Run the demo
if (require.main === module) {
  demonstratePlatform().catch(console.error);
}

export { demonstratePlatform };
