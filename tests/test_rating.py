"""Tests for rating service."""

import pytest
from eship.models import Shipment, Address, Package, CarrierType
from eship.services import RatingService


@pytest.fixture
def rating_service():
    return RatingService()


@pytest.fixture
def sample_shipment():
    return Shipment(
        from_address=Address(
            name="Sender Name",
            street1="100 Main St",
            city="New York",
            state="NY",
            postal_code="10001",
            country="US"
        ),
        to_address=Address(
            name="Recipient Name",
            street1="200 Oak Ave",
            city="Los Angeles",
            state="CA",
            postal_code="90001",
            country="US"
        ),
        package=Package(
            weight=5.0,
            length=12.0,
            width=8.0,
            height=6.0
        )
    )


@pytest.mark.asyncio
async def test_get_rates_returns_multiple_carriers(rating_service, sample_shipment):
    """Test that rates are returned from multiple carriers."""
    rates = await rating_service.get_rates(sample_shipment)
    
    assert len(rates) > 0
    
    carriers = {rate.carrier for rate in rates}
    assert CarrierType.UPS in carriers
    assert CarrierType.FEDEX in carriers
    assert CarrierType.USPS in carriers
    assert CarrierType.DHL in carriers


@pytest.mark.asyncio
async def test_rates_sorted_by_price(rating_service, sample_shipment):
    """Test that rates are sorted by price (cheapest first)."""
    rates = await rating_service.get_rates(sample_shipment)
    
    prices = [rate.rate for rate in rates]
    assert prices == sorted(prices)


@pytest.mark.asyncio
async def test_rates_include_service_details(rating_service, sample_shipment):
    """Test that rates include necessary service details."""
    rates = await rating_service.get_rates(sample_shipment)
    
    for rate in rates:
        assert rate.carrier is not None
        assert rate.service is not None
        assert rate.rate > 0
        assert rate.currency == "USD"
        assert rate.carrier_rate_id is not None
