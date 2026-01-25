"""Tests for tracking service."""

import pytest
from eship.models import CarrierType, TrackingState
from eship.services import TrackingService


@pytest.fixture
def tracking_service():
    return TrackingService()


@pytest.mark.asyncio
async def test_detect_ups_tracking_number(tracking_service):
    """Test UPS tracking number detection."""
    tracking_number = "1Z999AA10123456784"
    carrier = tracking_service._detect_carrier(tracking_number)
    assert carrier == CarrierType.UPS


@pytest.mark.asyncio
async def test_detect_fedex_tracking_number(tracking_service):
    """Test FedEx tracking number detection."""
    tracking_number = "123456789012"
    carrier = tracking_service._detect_carrier(tracking_number)
    assert carrier == CarrierType.FEDEX


@pytest.mark.asyncio
async def test_get_tracking_with_carrier(tracking_service):
    """Test getting tracking information."""
    tracking = await tracking_service.get_tracking("1Z999AA10123456784", CarrierType.UPS)

    assert tracking is not None
    assert tracking.tracking_number == "1Z999AA10123456784"
    assert tracking.carrier == CarrierType.UPS
    assert tracking.current_status in TrackingState
    assert len(tracking.events) > 0


@pytest.mark.asyncio
async def test_tracking_events_have_required_fields(tracking_service):
    """Test that tracking events have all required fields."""
    tracking = await tracking_service.get_tracking("1Z999AA10123456784", CarrierType.UPS)

    for event in tracking.events:
        assert event.timestamp is not None
        assert event.status in TrackingState
        assert event.message is not None
        assert event.carrier_status is not None
