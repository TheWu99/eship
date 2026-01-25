# eShip - Multi-Carrier Shipping Platform

A comprehensive web application that provides a unified API to access services from multiple logistics providers (UPS, FedEx, DHL). This platform enables seamless integration with multiple carriers for shipping operations.

## Features

- **Multi-Carrier Support**: Unified interface for UPS, FedEx, and DHL
- **Rate Calculation**: Get real-time shipping rates from multiple carriers
- **Shipping Labels**: Generate shipping labels for all supported carriers
- **Package Tracking**: Track packages across different carriers
- **Address Validation**: Validate shipping addresses
- **Pickup Scheduling**: Schedule pickups with carriers
- **Customs Documentation**: Generate international customs documentation
- **Shipment Types**: Support for small packages, LTL, and air freight

## Technology Stack

- **Backend**: Node.js with Express
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla)
- **APIs**: RESTful API architecture
- **Architecture**: Carrier abstraction layer for easy extensibility

## Installation

1. Clone the repository:
```bash
git clone https://github.com/TheWu99/eship.git
cd eship
```

2. Install dependencies:
```bash
npm install
```

3. Configure environment variables:
```bash
cp .env.example .env
```

Edit `.env` and add your carrier API credentials:
```
PORT=3000
UPS_API_KEY=your_ups_api_key
UPS_API_SECRET=your_ups_api_secret
UPS_ACCOUNT_NUMBER=your_ups_account_number
FEDEX_API_KEY=your_fedex_api_key
FEDEX_API_SECRET=your_fedex_api_secret
FEDEX_ACCOUNT_NUMBER=your_fedex_account_number
DHL_API_KEY=your_dhl_api_key
DHL_API_SECRET=your_dhl_api_secret
DHL_ACCOUNT_NUMBER=your_dhl_account_number
```

4. Start the application:
```bash
npm start
```

The application will be available at `http://localhost:3000`

## API Documentation

### Get Available Carriers
```
GET /api/shipping/carriers
```

### Calculate Shipping Rates
```
POST /api/shipping/rates
Content-Type: application/json

{
  "shipment": {
    "origin": {
      "city": "New York",
      "state": "NY",
      "postalCode": "10001",
      "country": "US"
    },
    "destination": {
      "city": "Los Angeles",
      "state": "CA",
      "postalCode": "90001",
      "country": "US"
    },
    "weight": 5,
    "shipmentType": "small_package"
  },
  "carrier": "ups" // optional, omit to get rates from all carriers
}
```

### Create Shipping Label
```
POST /api/shipping/labels
Content-Type: application/json

{
  "carrier": "ups",
  "shipment": {
    "origin": { ... },
    "destination": { ... },
    "weight": 5,
    "service": "Ground"
  }
}
```

### Track Package
```
GET /api/shipping/tracking/:carrier/:trackingNumber
```

### Validate Address
```
POST /api/shipping/validate-address
Content-Type: application/json

{
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "postalCode": "10001",
    "country": "US"
  },
  "carrier": "ups"
}
```

### Schedule Pickup
```
POST /api/shipping/pickup
Content-Type: application/json

{
  "carrier": "ups",
  "pickupDetails": {
    "date": "2026-01-30",
    "timeWindow": "9:00 AM - 5:00 PM",
    "address": { ... }
  }
}
```

### Create Customs Documentation
```
POST /api/shipping/customs
Content-Type: application/json

{
  "carrier": "ups",
  "customsData": {
    "type": "Commercial Invoice",
    "items": [
      {
        "description": "Electronics",
        "quantity": 2,
        "value": 150.00,
        "hsCode": "8517.12"
      }
    ]
  }
}
```

## Project Structure

```
eship/
├── src/
│   ├── carriers/          # Carrier implementations
│   │   ├── BaseCarrier.js
│   │   ├── UPSCarrier.js
│   │   ├── FedExCarrier.js
│   │   ├── DHLCarrier.js
│   │   └── CarrierFactory.js
│   ├── config/            # Configuration files
│   │   └── carriers.js
│   ├── routes/            # API routes
│   │   └── shipping.js
│   ├── services/          # Business logic
│   │   └── ShippingService.js
│   └── server.js          # Main server file
├── public/                # Frontend files
│   ├── index.html
│   ├── css/
│   │   └── style.css
│   └── js/
│       └── app.js
├── .env.example           # Environment variables template
├── .gitignore
├── package.json
└── README.md
```

## Architecture

The application uses a carrier abstraction layer pattern:

1. **BaseCarrier**: Abstract class defining the interface for all carriers
2. **Carrier Implementations**: Specific implementations for UPS, FedEx, and DHL
3. **CarrierFactory**: Manages carrier instances and routing
4. **ShippingService**: Business logic layer
5. **API Routes**: RESTful endpoints for client interaction

## Adding New Carriers

To add a new carrier:

1. Create a new carrier class extending `BaseCarrier`:
```javascript
class NewCarrier extends BaseCarrier {
  // Implement required methods
}
```

2. Add configuration in `src/config/carriers.js`

3. Register in `CarrierFactory.js`

## Notes

This is a demonstration application. The carrier integrations use simulated responses. For production use:

1. Replace simulated responses with actual API calls
2. Implement proper authentication and security
3. Add comprehensive error handling
4. Implement rate limiting and caching
5. Add database for storing shipment history
6. Implement webhook handlers for tracking updates

## Security Considerations

This is a demonstration application. For production deployment:

1. **Rate Limiting**: Implement rate limiting middleware (e.g., `express-rate-limit`) to protect against abuse and DDoS attacks
2. **Authentication**: Add proper authentication and authorization for API endpoints
3. **Input Validation**: Implement comprehensive input validation and sanitization
4. **HTTPS**: Use HTTPS/TLS for all communications
5. **API Keys**: Securely store and manage carrier API credentials using environment variables and secrets management
6. **Error Handling**: Avoid exposing sensitive information in error messages
7. **Logging**: Implement proper logging and monitoring for security events

## License

ISC
