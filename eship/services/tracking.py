"""Tracking service for unified tracking across carriers."""

from datetime import datetime, timedelta
from typing import List, Optional
import random

from ..models import ShipmentTracking, TrackingEvent, TrackingState, CarrierType


class TrackingService:
    """Service for tracking shipments across all carriers."""

    async def get_tracking(
        self, tracking_number: str, carrier: Optional[CarrierType] = None
    ) -> Optional[ShipmentTracking]:
        """
        Get tracking information for a shipment.

        Args:
            tracking_number: Tracking number to look up
            carrier: Optional carrier filter

        Returns:
            Tracking information if found
        """
        # In a real implementation, this would query carrier APIs
        # For now, we'll generate mock tracking data

        if not carrier:
            # Try to detect carrier from tracking number format
            carrier = self._detect_carrier(tracking_number)

        if not carrier:
            return None

        events = await self._generate_mock_events(carrier, tracking_number)

        if not events:
            return None

        current_status = events[-1].status if events else TrackingState.PRE_TRANSIT

        # Estimate delivery
        estimated_delivery = None
        actual_delivery = None

        if current_status == TrackingState.DELIVERED:
            actual_delivery = events[-1].timestamp
        elif current_status in [TrackingState.PRE_TRANSIT, TrackingState.IN_TRANSIT]:
            # Estimate 3-5 days from last event
            last_event_time = events[-1].timestamp if events else datetime.now()
            estimated_delivery = last_event_time + timedelta(days=random.randint(3, 5))

        return ShipmentTracking(
            tracking_number=tracking_number,
            carrier=carrier,
            current_status=current_status,
            events=events,
            estimated_delivery=estimated_delivery,
            actual_delivery=actual_delivery,
        )

    def _detect_carrier(self, tracking_number: str) -> Optional[CarrierType]:
        """Detect carrier from tracking number format."""

        # UPS: 18 characters starting with 1Z
        if tracking_number.startswith("1Z") and len(tracking_number) == 18:
            return CarrierType.UPS

        # FedEx: 12 or 15 digits
        if tracking_number.isdigit() and len(tracking_number) in [12, 15]:
            return CarrierType.FEDEX

        # USPS: 20-22 characters
        if len(tracking_number) in [20, 22] and tracking_number[:2].isdigit():
            return CarrierType.USPS

        # DHL: 10 digits
        if tracking_number.isdigit() and len(tracking_number) == 10:
            return CarrierType.DHL

        return None

    async def _generate_mock_events(
        self, carrier: CarrierType, tracking_number: str
    ) -> List[TrackingEvent]:
        """Generate mock tracking events for demonstration."""

        now = datetime.now()
        events = []

        # Generate a realistic progression of events
        event_templates = [
            {
                "offset_days": -5,
                "status": TrackingState.PRE_TRANSIT,
                "message": "Shipping label created",
                "location": None,
            },
            {
                "offset_days": -4,
                "status": TrackingState.IN_TRANSIT,
                "message": "Package picked up by carrier",
                "location": "Origin Facility",
            },
            {
                "offset_days": -3,
                "status": TrackingState.IN_TRANSIT,
                "message": "In transit to destination",
                "location": "Regional Hub",
            },
            {
                "offset_days": -2,
                "status": TrackingState.IN_TRANSIT,
                "message": "Arrived at destination facility",
                "location": "Destination Facility",
            },
            {
                "offset_days": -1,
                "status": TrackingState.OUT_FOR_DELIVERY,
                "message": "Out for delivery",
                "location": "Local Delivery Center",
            },
            {
                "offset_days": 0,
                "status": TrackingState.DELIVERED,
                "message": "Delivered",
                "location": "Front Door",
            },
        ]

        # Randomly decide how far along the shipment is
        num_events = random.randint(2, len(event_templates))

        for template in event_templates[:num_events]:
            timestamp = now + timedelta(days=template["offset_days"])

            # Add carrier-specific status codes
            carrier_status = self._get_carrier_status(carrier, template["status"])

            event = TrackingEvent(
                timestamp=timestamp,
                status=template["status"],
                message=template["message"],
                location=template["location"],
                carrier_status=carrier_status,
            )
            events.append(event)

        return events

    def _get_carrier_status(self, carrier: CarrierType, status: TrackingState) -> str:
        """Get carrier-specific status code."""

        status_mapping = {
            CarrierType.UPS: {
                TrackingState.PRE_TRANSIT: "M",
                TrackingState.IN_TRANSIT: "I",
                TrackingState.OUT_FOR_DELIVERY: "O",
                TrackingState.DELIVERED: "D",
            },
            CarrierType.FEDEX: {
                TrackingState.PRE_TRANSIT: "PU",
                TrackingState.IN_TRANSIT: "IT",
                TrackingState.OUT_FOR_DELIVERY: "OD",
                TrackingState.DELIVERED: "DL",
            },
            CarrierType.USPS: {
                TrackingState.PRE_TRANSIT: "Pre-Shipment",
                TrackingState.IN_TRANSIT: "In Transit",
                TrackingState.OUT_FOR_DELIVERY: "Out for Delivery",
                TrackingState.DELIVERED: "Delivered",
            },
            CarrierType.DHL: {
                TrackingState.PRE_TRANSIT: "transit",
                TrackingState.IN_TRANSIT: "transit",
                TrackingState.OUT_FOR_DELIVERY: "delivery",
                TrackingState.DELIVERED: "delivered",
            },
        }

        return status_mapping.get(carrier, {}).get(status, "UNKNOWN")
