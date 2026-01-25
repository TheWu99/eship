#!/usr/bin/env python
"""Manual test script to verify the API functionality."""

import asyncio
import json
from eship.models import (
    Address,
    Package,
    Shipment,
    LabelFormat,
    CarrierType,
    CustomsForm,
    CustomsItem,
)
from eship.services import (
    AddressValidationService,
    RatingService,
    LabelGenerationService,
    TrackingService,
    WebhookService,
    CustomsService,
)


async def test_address_validation():
    """Test address validation."""
    print("\n=== Testing Address Validation ===")
    service = AddressValidationService()

    address = Address(
        name="John Doe",
        street1="123 Main St",
        city="New York",
        state="ny",
        postal_code="10001",
        country="us",
    )

    validated, is_valid = await service.validate_address(address)
    print(f"Original: {address.city}, {address.state} {address.postal_code}")
    print(f"Validated: {validated.city}, {validated.state} {validated.postal_code}")
    print(f"Valid: {is_valid}")

    address_type = await service.detect_address_type(validated)
    print(f"Address Type: {address_type}")


async def test_rating():
    """Test rating service."""
    print("\n=== Testing Rating Service ===")
    service = RatingService()

    shipment = Shipment(
        from_address=Address(
            name="Sender",
            street1="100 Main St",
            city="New York",
            state="NY",
            postal_code="10001",
            country="US",
        ),
        to_address=Address(
            name="Recipient",
            street1="200 Oak Ave",
            city="Los Angeles",
            state="CA",
            postal_code="90001",
            country="US",
        ),
        package=Package(weight=5.0, length=12.0, width=8.0, height=6.0),
    )

    rates = await service.get_rates(shipment)
    print(f"Found {len(rates)} rates:")
    for rate in rates[:5]:  # Show top 5
        print(f"  {rate.carrier.value} {rate.service}: ${rate.rate:.2f} ({rate.delivery_days} days)")


async def test_label_generation():
    """Test label generation."""
    print("\n=== Testing Label Generation ===")
    service = LabelGenerationService()

    shipment = Shipment(
        from_address=Address(
            name="ACME Corp",
            street1="100 Main St",
            city="New York",
            state="NY",
            postal_code="10001",
            country="US",
        ),
        to_address=Address(
            name="John Doe",
            street1="200 Oak Ave",
            city="Los Angeles",
            state="CA",
            postal_code="90001",
            country="US",
        ),
        package=Package(weight=5.0, length=12.0, width=8.0, height=6.0),
    )

    for format in [LabelFormat.ZPL, LabelFormat.PDF, LabelFormat.PNG]:
        label = await service.generate_label(
            shipment=shipment,
            tracking_number="1Z999AA10123456784",
            carrier=CarrierType.UPS,
            format=format,
        )
        print(f"Generated {format.value} label: {len(label.content)} bytes (base64)")


async def test_tracking():
    """Test tracking service."""
    print("\n=== Testing Tracking Service ===")
    service = TrackingService()

    tracking = await service.get_tracking("1Z999AA10123456784", CarrierType.UPS)
    if tracking:
        print(f"Tracking: {tracking.tracking_number}")
        print(f"Carrier: {tracking.carrier.value}")
        print(f"Status: {tracking.current_status.value}")
        print(f"Events: {len(tracking.events)}")
        for event in tracking.events[-3:]:  # Last 3 events
            print(f"  - {event.timestamp.strftime('%Y-%m-%d %H:%M')}: {event.message}")


async def test_webhooks():
    """Test webhook service."""
    print("\n=== Testing Webhook Service ===")
    service = WebhookService()

    subscription = await service.create_subscription(
        url="https://example.com/webhook", tracking_numbers=["1Z999AA10123456784"]
    )
    print(f"Created subscription: {subscription.id}")
    print(f"URL: {subscription.url}")
    print(f"Events: {len(subscription.events)}")

    subs = await service.list_subscriptions()
    print(f"Total subscriptions: {len(subs)}")


async def test_customs():
    """Test customs service."""
    print("\n=== Testing Customs Service ===")
    service = CustomsService()

    shipment = Shipment(
        from_address=Address(
            name="ACME Corp",
            street1="100 Main St",
            city="New York",
            state="NY",
            postal_code="10001",
            country="US",
        ),
        to_address=Address(
            name="Jane Smith",
            street1="123 High Street",
            city="London",
            state="LDN",
            postal_code="SW1A 1AA",
            country="GB",
        ),
        package=Package(weight=2.0, length=10.0, width=8.0, height=4.0, value=150.0),
        customs=CustomsForm(
            contents_type="merchandise",
            customs_signer="John Manager",
            items=[
                CustomsItem(
                    description="Electronics",
                    quantity=2,
                    value=75.0,
                    weight=1.0,
                    hs_code="8517.12.00",
                    origin_country="US",
                )
            ],
        ),
    )

    cn22 = await service.generate_customs_form(shipment, "CN22")
    print(f"Generated CN22 form: {len(cn22)} bytes (base64)")

    invoice = await service.generate_commercial_invoice(shipment)
    print(f"Generated commercial invoice: {len(invoice)} bytes (base64)")


async def main():
    """Run all tests."""
    print("Starting eShip Platform Tests...")

    await test_address_validation()
    await test_rating()
    await test_label_generation()
    await test_tracking()
    await test_webhooks()
    await test_customs()

    print("\n✅ All tests completed successfully!")


if __name__ == "__main__":
    asyncio.run(main())
