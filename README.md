# eShip - Advanced Shipping Management Platform

A comprehensive enterprise shipping platform providing advanced shipping management features including AI-driven rate shopping, intelligent cartonization, insurance management, returns portal, pickup scheduling, and LTL/freight capabilities.

## 🛠 Technology Stack

### Development Environment
- **IDE**: Visual Studio Code (VS Code)
- **Version Control**: Git

### Backend
- **Framework**: Spring Boot 3.5.10 (Latest)
- **Language**: Java 17
- **Build Tool**: Maven
- **Security**: Spring Security with JWT Authentication
- **ORM**: Spring Data JPA with Hibernate

### Frontend
- **Language**: TypeScript
- **Runtime**: Node.js
- **Package Manager**: npm

### Database
- **RDBMS**: PostgreSQL 16
- **Connection**: JDBC Driver for PostgreSQL

### Key Features
- **Account Management**: User registration, authentication, and profile management
- **Account Authorization**: JWT-based authentication with role-based access control (RBAC)
- **Roles**: USER, ADMIN
- **Password Encryption**: BCrypt

## 📋 Prerequisites

Before you begin, ensure you have the following installed:
- **Java 17** or higher
- **Maven 3.6+**
- **Node.js 16+** and npm
- **PostgreSQL 16**
- **Visual Studio Code** (recommended)

## 🚀 Quick Start

### 1. Database Setup

Create a PostgreSQL database:
```sql
CREATE DATABASE eship;
```

### 2. Configure Application

Copy `.env.example` to `.env` and update the database credentials:
```bash
cp .env.example .env
```

Update `src/main/resources/application.properties` if needed:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/eship
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### 3. Build and Run Backend (Spring Boot)

```bash
# Install dependencies and build
mvn clean install

# Run the application
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 4. Install Frontend Dependencies

```bash
npm install
```

### 5. Build TypeScript

```bash
npm run build
```

## 🔐 Authentication API

### Register a New User

```bash
POST http://localhost:8080/api/auth/signup
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "roles": ["USER"]
}
```

### Login

```bash
POST http://localhost:8080/api/auth/signin
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

### Get Current User

```bash
GET http://localhost:8080/api/users/me
Authorization: Bearer <your-jwt-token>
```

### Get All Users (Admin Only)

```bash
GET http://localhost:8080/api/users
Authorization: Bearer <admin-jwt-token>
```

## Features

### 🤖 Smart Rate Shopping & Automation Rules
- **AI-Driven Logic**: Automatically select the cheapest, fastest, or most reliable carrier
- **Business Rules Engine**: Configurable rules for carrier selection based on cost, speed, reliability
- **Multi-Carrier Support**: USPS, UPS, FedEx, DHL, and more
- **Custom Optimization**: Define weighted criteria for balanced decision-making
- **Historical Data Integration**: Leverage past performance data for better predictions

### 📦 Cartonization & Packing Optimization
- **Intelligent Box Selection**: Algorithms to calculate the most efficient box size
- **Multi-Item Orders**: Optimize packing for orders with multiple items
- **Dimensional Weight Calculation**: Minimize dimensional weight charges across carriers
- **Bin Packing Algorithm**: First Fit Decreasing algorithm for efficient space utilization
- **Cost Optimization**: Reduce packaging costs while ensuring product safety

### 🛡️ Insurance & Claims Management
- **Integrated Shipping Insurance**: Purchase insurance directly for shipments
- **Automated Premium Calculation**: Dynamic pricing based on declared value
- **Claims Management**: Automated claim filing capabilities
- **Auto-Processing**: Smart rules for auto-approving small claims
- **Claims Tracking**: Full lifecycle tracking from filing to resolution

### 🔄 Returns Management Portal
- **Self-Service Portal**: Customer-facing portal for easy returns
- **QR Code Labels**: Generate scan-based return labels (no printer needed)
- **Traditional Labels**: PDF return labels for at-home printing
- **Return Tracking**: Real-time tracking of return shipments
- **Automated Processing**: Condition-based refund calculations

### 📅 Pickups & Manifesting
- **Carrier Pickup Scheduling**: Schedule pickups with multiple carriers via API
- **End-of-Day Manifests**: Generate daily manifests for carrier reconciliation
- **USPS SCAN Forms**: Automated SCAN form generation for bulk USPS shipments
- **Pickup Windows**: Validate and optimize pickup time windows
- **Confirmation Tracking**: Track pickup confirmations and completion

### 🚚 LTL & Freight Capabilities
- **LTL Shipping Support**: Full support for Less-Than-Truckload shipping
- **Pallet Rating**: Automatic freight class calculation based on density
- **BOL Generation**: Generate professional Bill of Lading documents
- **Freight Tracking**: Track LTL shipments with PRO numbers
- **Quote Comparison**: Get quotes from multiple LTL carriers
- **Accessorial Charges**: Support for liftgate, inside delivery, and more
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
npm install eship
```

## Quick Start

```typescript
import {
  RateShoppingService,
  CartonizationService,
  InsuranceService,
  ReturnsService,
  PickupsService,
  FreightService,
} from 'eship';

// Rate Shopping
const rateService = new RateShoppingService();
const rates = await rateService.fetchRates({
  from: originAddress,
  to: destinationAddress,
  packages: [package],
});

// Get best rate with business rules
const bestRate = await rateService.getBestRate(request, {
  criteria: 'cheapest',
  maxDays: 3,
  minReliability: 85,
});

// Cartonization
const cartonService = new CartonizationService();
const packingResult = cartonService.optimizePackaging(items);
console.log(`Packing efficiency: ${packingResult.efficiency}%`);

// Insurance
const insuranceService = new InsuranceService();
const insurance = await insuranceService.purchaseInsurance({
  shipmentId: 'SHP-123',
  declaredValue: 1000,
});

// Returns
const returnsService = new ReturnsService();
const returnLabel = await returnsService.generateScanBasedLabel({
  originalShipmentId: 'SHP-123',
  returnFrom: customerAddress,
  returnTo: warehouseAddress,
  reason: 'Changed mind',
});

// Pickups
const pickupsService = new PickupsService();
const pickup = await pickupsService.schedulePickup({
  carrier: 'UPS',
  address: pickupAddress,
  date: tomorrow,
  readyTime: '09:00',
  closeTime: '17:00',
  shipments: ['SHP-123', 'SHP-124'],
});

// Freight
const freightService = new FreightService();
const quotes = await freightService.getFreightQuote({
  origin: originAddress,
  destination: destinationAddress,
  pallets: [pallet],
  accessorials: ['liftgate', 'inside_delivery'],
});

const bol = await freightService.generateBOL({
  shipper: shipperAddress,
  consignee: consigneeAddress,
  pallets: [pallet],
  freightClass: '70',
});
```

## API Documentation

### Rate Shopping

#### RateShoppingService

```typescript
class RateShoppingService {
  // Fetch rates from multiple carriers
  async fetchRates(request: RateRequest): Promise<Rate[]>;
  
  // Get best rate based on business rules
  async getBestRate(request: RateRequest, rule: BusinessRule): Promise<Rate | null>;
  
  // AI-driven optimization with historical data
  async optimizeRateSelection(
    request: RateRequest,
    historicalData?: HistoricalData
  ): Promise<Rate | null>;
}
```

#### Business Rules

```typescript
interface BusinessRule {
  criteria: 'cheapest' | 'fastest' | 'most_reliable' | 'balanced' | 'custom';
  weightCost?: number;        // 0-1 (for custom criteria)
  weightSpeed?: number;       // 0-1 (for custom criteria)
  weightReliability?: number; // 0-1 (for custom criteria)
  excludeCarriers?: CarrierType[];
  preferredCarriers?: CarrierType[];
  maxCost?: number;
  maxDays?: number;
  minReliability?: number;
}
```

### Cartonization

#### CartonizationService

```typescript
class CartonizationService {
  // Optimize packaging for multi-item orders
  optimizePackaging(items: ShipmentItem[], carrier?: CarrierType): PackingResult;
  
  // Calculate dimensional weight
  calculateDimensionalWeight(dimensions: Dimensions, carrier: CarrierType): number;
  
  // Get billable weight (actual vs dimensional)
  getBillableWeight(pkg: Package, carrier: CarrierType): number;
  
  // Select optimal box size
  selectOptimalBox(items: ShipmentItem[]): Dimensions;
}
```

### Insurance & Claims

#### InsuranceService

```typescript
class InsuranceService {
  // Calculate insurance premium
  calculatePremium(declaredValue: number): number;
  
  // Purchase insurance for shipment
  async purchaseInsurance(options: InsuranceOptions): Promise<Insurance>;
  
  // Validate insurance eligibility
  validateInsuranceEligibility(shipment: Shipment): ValidationResult;
}
```

#### ClaimsService

```typescript
class ClaimsService {
  // File insurance claim
  async fileClaim(request: ClaimRequest): Promise<Claim>;
  
  // Auto-process claim based on rules
  async autoProcessClaim(claim: Claim, insurance: Insurance): Promise<Claim>;
  
  // Update claim status
  async updateClaimStatus(claimId: string, status: ClaimStatus): Promise<Claim>;
  
  // Generate claims report
  generateClaimReport(claims: Claim[]): ClaimReport;
}
```

### Returns Management

#### ReturnsService

```typescript
class ReturnsService {
  // Create self-service portal session
  async createReturnPortalSession(
    customerId: string,
    orderNumber: string
  ): Promise<ReturnPortalSession>;
  
  // Generate return label with QR code
  async generateReturnLabel(request: ReturnRequest): Promise<ReturnLabel>;
  
  // Generate scan-based label (no printer needed)
  async generateScanBasedLabel(request: ReturnRequest): Promise<ReturnLabel>;
  
  // Validate return eligibility
  validateReturnEligibility(shipment: Shipment, days: number): ValidationResult;
  
  // Process return with automated refund
  async processReturn(
    returnId: string,
    received: boolean,
    condition: 'new' | 'used' | 'damaged'
  ): Promise<ReturnResult>;
}
```

### Pickups & Manifesting

#### PickupsService

```typescript
class PickupsService {
  // Schedule carrier pickup
  async schedulePickup(request: PickupRequest): Promise<Pickup>;
  
  // Cancel scheduled pickup
  async cancelPickup(pickupId: string): Promise<void>;
  
  // Get available pickup windows
  getAvailablePickupWindows(carrier: CarrierType, date: Date): TimeWindow[];
}
```

#### ManifestingService

```typescript
class ManifestingService {
  // Generate end-of-day manifest
  async generateManifest(request: ManifestRequest): Promise<Manifest>;
  
  // Generate USPS SCAN form
  async generateUSPSScanForm(shipments: string[]): Promise<ScanForm>;
  
  // Close out day (manifest + SCAN form)
  async closeOutDay(carrier: CarrierType, shipments: Shipment[]): Promise<CloseoutResult>;
}
```

### LTL & Freight

#### FreightService

```typescript
class FreightService {
  // Calculate freight class based on density
  calculateFreightClass(pallet: Pallet): FreightClass;
  
  // Get freight quotes from LTL carriers
  async getFreightQuote(request: FreightQuoteRequest): Promise<FreightQuote[]>;
  
  // Generate Bill of Lading
  async generateBOL(request: BOLRequest): Promise<BillOfLading>;
  
  // Rate pallet shipment
  async ratePalletShipment(
    origin: Address,
    destination: Address,
    pallet: Pallet
  ): Promise<PalletRate>;
  
  // Track freight shipment
  async trackFreightShipment(proNumber: string): Promise<TrackingInfo>;
  
  // Validate pallet dimensions and weight
  validatePallet(pallet: Pallet): ValidationResult;
  
  // Create standard pallet
  createStandardPallet(type?: 'standard' | 'euro'): Pallet;
}
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

### Build

```bash
npm run build
```

### Test

```bash
npm test
```

### Lint

```bash
npm run lint
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

The eShip platform is organized into specialized modules:

```
src/
├── common/              # Shared types, interfaces, constants
│   ├── types/
│   ├── interfaces/
│   └── constants/
├── rate-shopping/       # Smart rate shopping & automation
│   └── services/
├── cartonization/       # Packing optimization
│   └── services/
├── insurance/           # Insurance & claims management
│   └── services/
├── returns/             # Returns management portal
│   └── services/
├── pickups/             # Pickups & manifesting
│   └── services/
└── freight/             # LTL & freight capabilities
    └── services/
```

## Contributing

Contributions are welcome! Please read our contributing guidelines before submitting PRs.
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

## Support

For issues and questions, please open an issue on GitHub.
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
