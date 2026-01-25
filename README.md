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
# eShip - Enterprise Shipping Platform

A comprehensive multi-carrier shipping solution providing unified APIs for shipping operations.

## Features

### 🚚 Core Shipping Capabilities

- **Multi-Carrier Rating Engine**: Real-time retrieval and comparison of shipping rates across multiple carriers (UPS, FedEx, USPS, DHL, etc.) through a single endpoint.

- **Dynamic Label Generation**: Automated creation of carrier-compliant shipping labels in multiple formats:
  - ZPL (Zebra Programming Language) for thermal printers
  - PDF for standard printers
  - PNG for image-based applications

- **Address Validation & Standardization**: Verification of global street-level addresses to prevent delivery failures and "residential vs. commercial" surcharge errors.

- **Unified Tracking API**: A single tracking interface that standardizes status events across hundreds of carriers into a common set of states:
  - `pre_transit`: Label created, not yet in carrier's possession
  - `in_transit`: Package moving through carrier network
  - `out_for_delivery`: Package out for final delivery
  - `delivered`: Package successfully delivered
  - `returned`: Package returned to sender
  - `failed`: Delivery failed
  - `cancelled`: Shipment cancelled
  - `exception`: Exception occurred during transit

- **Carrier Webhooks**: Subscription-based push notifications for real-time shipment status updates and exception alerts.

- **International Documentation**: Automatic generation of customs forms, commercial invoices, and Electronic Export Information (EEI):
  - CN22 customs forms (for items under $400)
  - CN23 customs forms (for items over $400)
  - Commercial invoices with itemized contents
  - HS code support

## Quick Start

### Installation

```bash
# Clone the repository
git clone https://github.com/TheWu99/eship.git
cd eship

# Install dependencies
pip install -r requirements.txt
```

### Running the Server

```bash
# Start the development server
uvicorn eship.main:app --reload --host 0.0.0.0 --port 8000
```

The API will be available at `http://localhost:8000` with interactive documentation at `http://localhost:8000/`.

## API Documentation

### Get Shipping Rates

```bash
POST /api/v1/rates
```

Compare rates across all carriers:

```json
{
  "from_address": {
    "name": "Sender Name",
    "street1": "123 Main St",
    "city": "New York",
    "state": "NY",
    "postal_code": "10001",
    "country": "US"
  },
  "to_address": {
    "name": "Recipient Name",
    "street1": "456 Oak Ave",
    "city": "Los Angeles",
    "state": "CA",
    "postal_code": "90001",
    "country": "US"
  },
  "package": {
    "weight": 5.0,
    "length": 12.0,
    "width": 8.0,
    "height": 6.0
  }
}
```

### Generate Shipping Label

```bash
POST /api/v1/labels
```

Create a shipping label in your preferred format:

```json
{
  "from_address": { ... },
  "to_address": { ... },
  "package": { ... },
  "carrier": "UPS",
  "label_format": "PDF"
}
```

### Track Shipment

```bash
GET /api/v1/tracking/{tracking_number}?carrier=UPS
```

### Validate Address

```bash
POST /api/v1/address/validate
```

Verify and standardize addresses:

```json
{
  "name": "John Doe",
  "street1": "123 Main St",
  "city": "New York",
  "state": "NY",
  "postal_code": "10001",
  "country": "US"
}
```

### Create Webhook Subscription

```bash
POST /api/v1/webhooks
```

Subscribe to tracking events:

```json
{
  "url": "https://your-app.com/webhooks/shipping",
  "events": ["in_transit", "delivered"],
  "tracking_numbers": ["1Z999AA10123456784"]
}
```

### Generate Customs Form

```bash
POST /api/v1/customs/form?form_type=CN22
```

### Generate Commercial Invoice

```bash
POST /api/v1/customs/commercial-invoice
```

## Development

### Running Tests

```bash
# Install dev dependencies
pip install -r requirements-dev.txt

# Run tests
pytest

# Run with coverage
pytest --cov=eship --cov-report=html
```

### Code Formatting

```bash
# Format code
black eship tests

# Lint code
ruff check eship tests
```

### Type Checking

```bash
mypy eship
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
```
eship/
├── api/           # FastAPI route handlers
│   ├── rates.py
│   ├── labels.py
│   ├── tracking.py
│   ├── webhooks.py
│   ├── address.py
│   └── customs.py
├── models/        # Pydantic data models
├── services/      # Business logic
│   ├── rating.py
│   ├── label_generation.py
│   ├── tracking.py
│   ├── webhooks.py
│   ├── address_validation.py
│   └── customs.py
└── main.py        # Application entry point
```

## Technology Stack

- **Framework**: FastAPI for high-performance async APIs
- **Validation**: Pydantic for data validation and serialization
- **Label Generation**: ReportLab (PDF), Pillow (PNG), QR codes
- **Testing**: pytest with async support
- **Code Quality**: Black, Ruff, MyPy

## License

MIT License
# E-Ship Application

A modern e-commerce shipping application built with Spring Boot 3.5.10.

## Technology Stack

- **Spring Boot**: 3.5.10 (Latest version)
- **Java**: 17
- **Build Tool**: Maven
- **Web Framework**: Spring Web MVC

## Prerequisites

- Java 17 or higher
- Maven 3.6+

## Getting Started

### Build the Application

```bash
mvn clean package
```

### Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Run Tests

```bash
mvn test
```

## API Endpoints

- `GET /api/hello` - Welcome message
- `GET /api/version` - Spring Boot version information

## Project Structure

```
eship/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/thewu/eship/
│   │   │       ├── EshipApplication.java
│   │   │       └── controller/
│   │   │           └── HomeController.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/thewu/eship/
│               └── EshipApplicationTests.java
└── pom.xml
```
