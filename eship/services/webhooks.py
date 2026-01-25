"""Webhook service for carrier event notifications."""

from typing import List, Optional, Dict
from datetime import datetime
import uuid
import asyncio

from ..models import WebhookSubscription, WebhookEvent, TrackingEvent, TrackingState, CarrierType


class WebhookService:
    """Service for managing webhook subscriptions and sending notifications."""

    def __init__(self):
        # In-memory storage (in production, use a database)
        self._subscriptions: Dict[str, WebhookSubscription] = {}

    async def create_subscription(
        self,
        url: str,
        events: Optional[List[TrackingState]] = None,
        tracking_numbers: Optional[List[str]] = None,
    ) -> WebhookSubscription:
        """
        Create a new webhook subscription.

        Args:
            url: Webhook endpoint URL
            events: List of events to subscribe to (default: all events)
            tracking_numbers: Optional filter for specific tracking numbers

        Returns:
            Created subscription
        """
        subscription = WebhookSubscription(
            id=str(uuid.uuid4()),
            url=url,
            events=events or list(TrackingState),
            tracking_numbers=tracking_numbers,
            active=True,
        )

        self._subscriptions[subscription.id] = subscription
        return subscription

    async def get_subscription(self, subscription_id: str) -> Optional[WebhookSubscription]:
        """Get a subscription by ID."""
        return self._subscriptions.get(subscription_id)

    async def list_subscriptions(self, active_only: bool = True) -> List[WebhookSubscription]:
        """List all subscriptions."""
        subs = list(self._subscriptions.values())
        if active_only:
            subs = [s for s in subs if s.active]
        return subs

    async def update_subscription(
        self,
        subscription_id: str,
        url: Optional[str] = None,
        events: Optional[List[TrackingState]] = None,
        active: Optional[bool] = None,
    ) -> Optional[WebhookSubscription]:
        """Update an existing subscription."""
        subscription = self._subscriptions.get(subscription_id)
        if not subscription:
            return None

        if url is not None:
            subscription.url = url
        if events is not None:
            subscription.events = events
        if active is not None:
            subscription.active = active

        return subscription

    async def delete_subscription(self, subscription_id: str) -> bool:
        """Delete a subscription."""
        if subscription_id in self._subscriptions:
            del self._subscriptions[subscription_id]
            return True
        return False

    async def send_webhook_event(
        self,
        tracking_number: str,
        carrier: CarrierType,
        status: TrackingState,
        event_details: TrackingEvent,
    ) -> None:
        """
        Send webhook notifications for a tracking event.

        Args:
            tracking_number: Tracking number
            carrier: Carrier
            status: New tracking status
            event_details: Detailed event information
        """
        # Find matching subscriptions
        matching_subs = await self._find_matching_subscriptions(tracking_number, status)

        if not matching_subs:
            return

        # Create webhook event
        webhook_event = WebhookEvent(
            event_id=str(uuid.uuid4()),
            event_type=f"tracking.{status.value}",
            timestamp=datetime.now(),
            tracking_number=tracking_number,
            carrier=carrier,
            status=status,
            event_details=event_details,
        )

        # Send to all matching subscriptions
        tasks = [self._send_to_endpoint(sub.url, webhook_event) for sub in matching_subs]

        await asyncio.gather(*tasks, return_exceptions=True)

    async def _find_matching_subscriptions(
        self, tracking_number: str, status: TrackingState
    ) -> List[WebhookSubscription]:
        """Find subscriptions that match the event."""
        matching = []

        for sub in self._subscriptions.values():
            if not sub.active:
                continue

            # Check if event type matches
            if status not in sub.events:
                continue

            # Check if tracking number matches (if filtered)
            if sub.tracking_numbers and tracking_number not in sub.tracking_numbers:
                continue

            matching.append(sub)

        return matching

    async def _send_to_endpoint(self, url: str, event: WebhookEvent) -> None:
        """
        Send webhook event to an endpoint.

        In a real implementation, this would use httpx or requests
        to POST the event to the webhook URL.
        """
        # Simulate sending webhook
        # In production:
        # async with httpx.AsyncClient() as client:
        #     await client.post(url, json=event.model_dump())
        pass
