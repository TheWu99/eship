# UPS API Integration Guide

This document provides instructions for setting up and using the UPS API integration in the eShip application.

## Overview

The eShip application now integrates with the following UPS APIs:
- **Rating API (v2409)**: Get shipping rates and compare services
- **Tracking API (v1)**: Track shipments in real-time
- **Shipping API (v2409)**: Create shipping labels (coming soon)

## Prerequisites

1. **UPS Developer Account**: Create a free account at https://developer.ups.com
2. **Create an Application**: Once logged in, create a new application to get your API credentials
3. **Subscribe to APIs**: Add the Rating, Tracking, and Shipping APIs to your application

## Getting Your Credentials

1. Go to https://developer.ups.com and sign in
2. Navigate to **Apps** > **Create an App**
3. Fill in the application details:
   - **App Name**: eShip Application
   - **Products**: Select Rating, Tracking, and Shipping
4. Once created, you'll receive:
   - **Client ID**: Your OAuth 2.0 client identifier
   - **Client Secret**: Your OAuth 2.0 client secret
5. You'll also need your **UPS Account Number** (shipper number) from your UPS shipping account

## Configuration

### Environment Variables (Recommended for Production)

Set the following environment variables:

```bash
export UPS_CLIENT_ID=your_client_id_here
export UPS_CLIENT_SECRET=your_client_secret_here
export UPS_ACCOUNT_NUMBER=your_account_number_here
```

On Windows:
```cmd
set UPS_CLIENT_ID=your_client_id_here
set UPS_CLIENT_SECRET=your_client_secret_here
set UPS_ACCOUNT_NUMBER=your_account_number_here
```

### Application Properties (Development)

Alternatively, update `src/main/resources/application.properties`:

```properties
# UPS API Configuration
ups.api.client-id=YOUR_CLIENT_ID_HERE
ups.api.client-secret=YOUR_CLIENT_SECRET_HERE
ups.api.account-number=YOUR_ACCOUNT_NUMBER_HERE

# For testing, use CIE (Customer Integration Environment):
ups.api.base-url=https://wwwcie.ups.com/api
ups.api.oauth-url=https://wwwcie.ups.com/security/v1/oauth/token

# For production, use:
# ups.api.base-url=https://onlinetools.ups.com/api
# ups.api.oauth-url=https://onlinetools.ups.com/security/v1/oauth/token
```

## Testing Environments

### CIE (Customer Integration Environment)
- **Base URL**: https://wwwcie.ups.com/api
- **Purpose**: Development and testing
- **Test Data**: Use UPS-provided test tracking numbers and addresses
- **Limitations**: No actual shipments are created

### Production
- **Base URL**: https://onlinetools.ups.com/api
- **Purpose**: Live production environment
- **Requirements**: Valid UPS account with billing set up
- **Creates**: Real shipments that will be billed

## API Features

### 1. Rating API

Get shipping rates from UPS:

```bash
POST /api/v1/rates
Content-Type: application/json

{
  "origin": {
    "street1": "123 Main St",
    "city": "Atlanta",
    "state": "GA",
    "zipCode": "30301",
    "country": "US"
  },
  "destination": {
    "street1": "456 Market St",
    "city": "San Francisco",
    "state": "CA",
    "zipCode": "94102",
    "country": "US"
  },
  "packages": [{
    "weight": 5.0,
    "length": 12,
    "width": 8,
    "height": 6
  }]
}
```

**Supported UPS Services:**
- `03` - Ground
- `01` - Next Day Air
- `02` - 2nd Day Air
- `12` - 3 Day Select
- `13` - Next Day Air Saver
- `14` - Next Day Air Early
- `59` - 2nd Day Air A.M.

### 2. Tracking API

Track a UPS shipment:

```bash
GET /api/v1/tracking/1Z999AA10123456784
```

**UPS Tracking Number Format:**
- Starts with "1Z"
- 18 characters total
- Test tracking number: `1Z12345E0205271688`

**Tracking Status Codes:**
- `MP` - Manifest Pickup
- `I` - In Transit
- `X` - Exception
- `D` - Delivered
- `P` - Pickup
- `RS` - Returned to Sender

### 3. Shipping API (Coming Soon)

Create shipping labels and shipments.

## Code Structure

### Services

- **UpsOAuthService**: Manages OAuth 2.0 authentication with token caching
- **UpsRatingService**: Handles rate shopping and rate calculations
- **UpsTrackingService**: Provides shipment tracking functionality
- **UpsShippingService**: Creates labels and shipments (coming soon)

### DTOs

- **UpsRateRequest/Response**: Rating API data structures
- **UpsTrackingResponse**: Tracking API data structures
- **UpsShipmentRequest/Response**: Shipping API data structures (coming soon)

### Configuration

- **UpsApiConfig**: Configuration properties and RestTemplate setup
- **application.properties**: API credentials and endpoints

## Testing

### Using Postman

1. Import the UPS Postman collection: https://www.postman.com/ups-api/workspace/ups-apis/overview
2. Set your environment variables (client ID, client secret)
3. Test the APIs before integrating

### Test Data (CIE Environment)

**Test Addresses:**
```
Origin:
1401 E Main St
Richmond, VA 23219
US

Destination:
20 S Santa Cruz Ave
Los Gatos, CA 95030
US
```

**Test Tracking Numbers:**
- `1Z12345E0205271688` - Delivered
- `1Z12345E6605272234` - In Transit
- `1Z12345E0305271640` - Exception

### Testing with cURL

**Get OAuth Token:**
```bash
curl -X POST https://wwwcie.ups.com/security/v1/oauth/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -H "x-merchant-id: YOUR_CLIENT_ID" \
  -u "YOUR_CLIENT_ID:YOUR_CLIENT_SECRET" \
  -d "grant_type=client_credentials"
```

**Get Tracking:**
```bash
curl -X GET "https://wwwcie.ups.com/api/track/v1/details/1Z12345E0205271688?locale=en_US" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "transId: $(uuidgen)" \
  -H "transactionSrc: testing"
```

## Error Handling

The integration includes comprehensive error handling:

- **401 Unauthorized**: Invalid or expired OAuth token (automatically refreshes)
- **400 Bad Request**: Invalid request data
- **404 Not Found**: Tracking number not found
- **403 Forbidden**: Account not authorized for API
- **429 Rate Limit Exceeded**: Too many requests

All errors are logged and fallback to mock data when API is unavailable.

## Rate Limits

UPS API has rate limits:
- **Rating API**: 50 requests per minute
- **Tracking API**: 250 requests per minute
- **Shipping API**: 50 requests per minute

The application caches OAuth tokens to minimize authentication requests.

## Security Best Practices

1. **Never commit credentials** to version control
2. **Use environment variables** for sensitive data
3. **Rotate credentials** periodically
4. **Use HTTPS** for all API calls (handled automatically)
5. **Monitor API usage** through UPS Developer Portal

## Troubleshooting

### "Unauthorized" Errors
- Verify your client ID and secret are correct
- Check if your credentials have access to the API
- Ensure you're using the correct environment (CIE vs Production)

### "Invalid Address" Errors
- Verify address format matches UPS requirements
- Use standard USPS address formats
- Include required fields: street, city, state, postal code, country

### "Service Not Available" Errors
- Check UPS API status: https://developer.ups.com/
- Verify your network can reach UPS servers
- Check firewall settings

### Application Falls Back to Mock Data
- Verify UPS configuration in application.properties
- Check application logs for detailed error messages
- Ensure UPS services are properly autowired

## Resources

- **UPS Developer Portal**: https://developer.ups.com
- **API Documentation**: https://developer.ups.com/api/reference
- **OAuth Guide**: https://developer.ups.com/oauth-developer-guide
- **Postman Collection**: https://www.postman.com/ups-api/workspace/ups-apis
- **GitHub Samples**: https://github.com/UPS-API
- **Support**: https://developer.ups.com/us/en/support/contact-us

## Migration to Production

When moving to production:

1. Update `application.properties` to use production URLs:
   ```properties
   ups.api.base-url=https://onlinetools.ups.com/api
   ups.api.oauth-url=https://onlinetools.ups.com/security/v1/oauth/token
   ```

2. Ensure you have a valid UPS shipping account with billing set up

3. Test thoroughly in CIE before switching to production

4. Monitor API usage and costs through UPS portal

5. Set up proper logging and alerting for production issues

## Next Steps

1. **Get UPS credentials** from developer portal
2. **Configure application** with your credentials
3. **Test with CIE** environment
4. **Integrate FedEx API** (similar pattern)
5. **Integrate USPS API** (similar pattern)
6. **Implement Shipping API** for label generation
7. **Add Address Validation API** for address verification

## Support

For issues with:
- **UPS API**: Contact UPS Developer Support
- **eShip Application**: Create an issue in the project repository
- **Integration Questions**: Check the API documentation and code comments
