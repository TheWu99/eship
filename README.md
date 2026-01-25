# eShip - Advanced Shipping Management Platform

A comprehensive TypeScript/Node.js platform providing advanced shipping management features including AI-driven rate shopping, intelligent cartonization, insurance management, returns portal, pickup scheduling, and LTL/freight capabilities.

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

## License

ISC

## Support

For issues and questions, please open an issue on GitHub.