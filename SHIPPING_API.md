# Shipping API Endpoints

All shipping-related endpoints are now available under the Java/Spring Boot service.

## Base URL
```
http://localhost:8080/api/v1
```

## Endpoints

### 1. Rate Shopping
**POST** `/rates`

Get shipping rates from all carriers.

**Request Body:**
```json
{
  "fromAddress": {
    "name": "Sender Name",
    "street1": "123 Main St",
    "city": "New York",
    "state": "NY",
    "postalCode": "10001",
    "country": "US"
  },
  "toAddress": {
    "name": "Recipient Name",
    "street1": "456 Oak Ave",
    "city": "Los Angeles",
    "state": "CA",
    "postalCode": "90001",
    "country": "US"
  },
  "packageInfo": {
    "weight": 5.0,
    "length": 12.0,
    "width": 8.0,
    "height": 6.0
  }
}
```

**Response:** Array of rate objects sorted by price

---

### 2. Label Generation
**POST** `/labels`

Generate a shipping label.

**Request Body:**
```json
{
  "fromAddress": { ... },
  "toAddress": { ... },
  "packageInfo": { ... },
  "carrier": "UPS",
  "labelFormat": "PDF"
}
```

**Response:** Label object with tracking number and base64 encoded content

---

### 3. Shipment Tracking
**GET** `/tracking/{trackingNumber}?carrier=UPS`

Get tracking information for a shipment.

**Parameters:**
- `trackingNumber` (path): The tracking number
- `carrier` (query, optional): Carrier filter (auto-detected if not provided)

**Response:** Tracking information with events timeline

---

### 4. Address Validation
**POST** `/address/validate`

Validate and standardize an address.

**Request Body:**
```json
{
  "name": "John Doe",
  "street1": "123 main st",
  "city": "new york",
  "state": "ny",
  "postalCode": "10001",
  "country": "US"
}
```

**Response:** Validated address with standardized fields

---

### 5. Address Classification
**POST** `/address/classify`

Classify an address as residential or commercial.

**Request Body:** Same as address validation

**Response:**
```json
{
  "address_type": "RESIDENTIAL",
  "confidence": "medium"
}
```

---

### 6. Customs Form Generation
**POST** `/customs/form`

Generate customs form for international shipments.

**Request Body:**
```json
{
  "fromAddress": { "country": "US", ... },
  "toAddress": { "country": "CA", ... },
  "packageInfo": { ... },
  "customs": {
    "contentsType": "merchandise",
    "customsSigner": "John Doe",
    "items": [
      {
        "description": "Electronics",
        "quantity": 2,
        "value": 50.00,
        "weight": 1.5,
        "originCountry": "US"
      }
    ]
  }
}
```

**Response:** Base64 encoded customs form

---

### 7. Customs Check
**POST** `/customs/check`

Check if shipment requires customs documentation.

**Request Body:** Shipment object

**Response:**
```json
{
  "customs_required": true
}
```

---

### 8. Health Check
**GET** `/health`

Service health check endpoint.

**Response:**
```json
{
  "status": "healthy",
  "service": "eship-shipping"
}
```

## Authentication

Most endpoints require JWT authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## Supported Carriers

- UPS
- FedEx
- USPS
- DHL
- Other

## Supported Label Formats

- ZPL (for thermal printers)
- PDF (standard printers)
- PNG (image format)

## Notes

- All rates are returned in USD
- Tracking states are standardized across all carriers
- Address validation includes postal code standardization
- International shipments require customs documentation
