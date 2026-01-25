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
