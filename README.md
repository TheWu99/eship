# eShip Platform

A comprehensive e-commerce shipping platform providing advanced developer features for managing shipping operations across multiple carriers.

## Features

### 🧪 Sandbox Environment
A dedicated testing mode to simulate label purchases and tracking events without incurring real-world costs.

- Create mock shipping labels
- Simulate tracking events and deliveries
- Auto-generate tracking numbers
- Test workflows without actual carrier costs

### 💰 Monetization & Markup Engine
Ability to set custom markups on carrier rates for reselling shipping services.

- Configure percentage or flat-rate markups
- Carrier-specific and service-specific markup rules
- Global fallback markup configurations
- Profit calculation and reporting

### 💳 Consolidated Billing
One master account to pay for all postage across different carriers with audit-ready reconciliation.

- Single account for all carrier charges
- Transaction history and tracking
- Reconciliation reports by date range
- CSV export for accounting
- Spending analytics by carrier

### 🏭 Multi-Warehouse Routing
Logic to route shipments from the fulfillment center closest to the customer to reduce transit time and cost.

- Distance-based warehouse selection
- Multiple warehouse option comparison
- Estimated transit time calculation
- Warehouse management (active/inactive)

### 📄 Digital Proof of Delivery (POD)
Automatic retrieval of signature images and delivery confirmation documents.

- Retrieve signature images from carriers
- Store confirmation documents
- Batch POD retrieval
- POD availability checking

## Installation

```bash
npm install
```

## Build

```bash
npm run build
```

## Usage

### Basic Setup

```typescript
import { EShipPlatform } from './src';

// Initialize the platform
const eship = new EShipPlatform({
  sandboxEnabled: true,
  accountName: 'My Shipping Account'
});

// Check platform status
const status = eship.getStatus();
console.log(status);
```

### Sandbox Environment

```typescript
// Create a mock label in sandbox mode
const label = await eship.sandbox.createMockLabel({
  fromAddress: { /* ... */ },
  toAddress: { /* ... */ },
  weight: 2.5,
  dimensions: { length: 10, width: 8, height: 6 },
  carrier: 'USPS',
  service: 'Priority'
});

console.log('Tracking Number:', label.trackingNumber);

// Simulate tracking events
await eship.sandbox.simulateTrackingEvent(
  label.trackingNumber,
  'IN_TRANSIT',
  'Memphis Hub'
);

// Simulate full delivery process
await eship.sandbox.simulateDelivery(label.trackingNumber);

// Get all tracking events
const events = eship.sandbox.getTrackingEvents(label.trackingNumber);
```

### Monetization & Markup

```typescript
// Set a 15% markup for all USPS services
const markup = eship.monetization.createDefaultMarkup('USPS', undefined, 15);

// Set a flat $2.50 markup for FedEx Ground
eship.monetization.setMarkupConfig({
  id: 'fedex_ground_markup',
  carrier: 'FedEx',
  service: 'Ground',
  markupType: 'flat',
  markupValue: 2.50,
  isActive: true
});

// Apply markup to rates
const originalRate = { carrier: 'USPS', service: 'Priority', baseRate: 10.00, estimatedDays: 2, currency: 'USD' };
const markedUpRate = eship.monetization.applyMarkup(originalRate);

console.log('Original:', originalRate.baseRate);
console.log('Marked Up:', markedUpRate.baseRate);
```

### Consolidated Billing

```typescript
// Add funds to master account
eship.billing.addFunds(1000.00);

// Process a shipping charge
const transaction = eship.billing.processCharge(
  'USPS',
  11.50,
  label.id,
  'Priority Mail shipment'
);

// Get current balance
const balance = eship.billing.getBalance();

// Generate reconciliation report
const report = eship.billing.generateReconciliationReport(
  new Date('2024-01-01'),
  new Date('2024-01-31')
);

// Export to CSV
const csv = eship.billing.exportReportToCSV(report);
```

### Multi-Warehouse Routing

```typescript
// Add warehouses
eship.routing.addWarehouse({
  id: 'wh_east',
  name: 'East Coast Distribution',
  address: { street: '123 Main St', city: 'New York', state: 'NY', zipCode: '10001', country: 'US' },
  location: { latitude: 40.7128, longitude: -74.0060 },
  isActive: true
});

eship.routing.addWarehouse({
  id: 'wh_west',
  name: 'West Coast Distribution',
  address: { street: '456 Oak Ave', city: 'Los Angeles', state: 'CA', zipCode: '90001', country: 'US' },
  location: { latitude: 34.0522, longitude: -118.2437 },
  isActive: true
});

// Find optimal warehouse for a destination
const destination = {
  street: '789 Pine St',
  city: 'Chicago',
  state: 'IL',
  zipCode: '60601',
  country: 'US'
};

const routing = eship.routing.findOptimalWarehouse(destination);
console.log('Best warehouse:', routing?.warehouse.name);
console.log('Distance:', routing?.distance, 'miles');
console.log('Estimated transit:', routing?.estimatedTransitTime, 'days');

// Get top 3 warehouse options
const options = eship.routing.findWarehouseOptions(destination, 3);
```

### Digital Proof of Delivery

```typescript
// Retrieve POD for a delivery
const pod = await eship.pod.retrievePOD({
  trackingNumber: 'USPS1234567890',
  carrier: 'USPS'
});

if (pod) {
  console.log('Delivered at:', pod.deliveredAt);
  console.log('Recipient:', pod.recipientName);
  console.log('Signature:', pod.signatureImageUrl);
}

// Batch retrieve PODs
const pods = await eship.pod.batchRetrievePOD([
  { trackingNumber: 'USPS123', carrier: 'USPS' },
  { trackingNumber: 'FEDEX456', carrier: 'FedEx' },
  { trackingNumber: 'UPS789', carrier: 'UPS' }
]);

// Generate POD report
const podReport = eship.pod.generatePODReport([
  'USPS123',
  'FEDEX456',
  'UPS789'
]);

console.log(`${podReport.available}/${podReport.totalRequested} PODs available`);
```

## Architecture

The platform is built with TypeScript and follows a modular architecture:

```
src/
├── index.ts              # Main platform orchestrator
├── types/                # TypeScript type definitions
├── sandbox/              # Sandbox environment module
├── monetization/         # Markup and pricing engine
├── billing/              # Consolidated billing system
├── routing/              # Multi-warehouse routing logic
└── pod/                  # Proof of delivery service
```

## Development

### Project Structure

- `src/` - TypeScript source files
- `dist/` - Compiled JavaScript output
- `node_modules/` - Dependencies

### Scripts

- `npm run build` - Compile TypeScript to JavaScript
- `npm run dev` - Run in development mode with ts-node
- `npm start` - Run compiled application

## License

ISC