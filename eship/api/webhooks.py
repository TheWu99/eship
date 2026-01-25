"""Webhook management API endpoints."""

from typing import List, Optional
from fastapi import APIRouter, HTTPException, Body

from ..models import WebhookSubscription, TrackingState
from ..services import WebhookService

router = APIRouter()
webhook_service = WebhookService()


@router.post("/webhooks", response_model=WebhookSubscription, status_code=201)
async def create_webhook_subscription(
    url: str = Body(..., description="Webhook endpoint URL"),
    events: Optional[List[TrackingState]] = Body(None, description="Events to subscribe to"),
    tracking_numbers: Optional[List[str]] = Body(None, description="Filter by tracking numbers")
):
    """
    Create a new webhook subscription.
    
    Subscribe to real-time tracking updates. Your endpoint will receive POST requests
    whenever a subscribed event occurs for the specified tracking numbers (or all shipments
    if no filter is provided).
    
    Events are sent as JSON payloads to your webhook URL. Your endpoint should:
    - Respond with 2xx status code to acknowledge receipt
    - Process webhooks asynchronously to avoid timeouts
    - Implement idempotency (same event may be sent multiple times)
    
    - **url**: Your webhook endpoint URL (must be HTTPS in production)
    - **events**: List of tracking states to subscribe to (default: all events)
    - **tracking_numbers**: Optional list of specific tracking numbers to monitor
    """
    try:
        subscription = await webhook_service.create_subscription(
            url=url,
            events=events,
            tracking_numbers=tracking_numbers
        )
        return subscription
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error creating subscription: {str(e)}")


@router.get("/webhooks", response_model=List[WebhookSubscription])
async def list_webhook_subscriptions(active_only: bool = True):
    """
    List all webhook subscriptions.
    
    - **active_only**: Filter to only active subscriptions (default: true)
    """
    try:
        subscriptions = await webhook_service.list_subscriptions(active_only=active_only)
        return subscriptions
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error listing subscriptions: {str(e)}")


@router.get("/webhooks/{subscription_id}", response_model=WebhookSubscription)
async def get_webhook_subscription(subscription_id: str):
    """Get a specific webhook subscription by ID."""
    subscription = await webhook_service.get_subscription(subscription_id)
    if not subscription:
        raise HTTPException(status_code=404, detail="Subscription not found")
    return subscription


@router.patch("/webhooks/{subscription_id}", response_model=WebhookSubscription)
async def update_webhook_subscription(
    subscription_id: str,
    url: Optional[str] = Body(None),
    events: Optional[List[TrackingState]] = Body(None),
    active: Optional[bool] = Body(None)
):
    """
    Update a webhook subscription.
    
    - **url**: New webhook URL
    - **events**: New list of events to subscribe to
    - **active**: Enable or disable the subscription
    """
    try:
        subscription = await webhook_service.update_subscription(
            subscription_id=subscription_id,
            url=url,
            events=events,
            active=active
        )
        
        if not subscription:
            raise HTTPException(status_code=404, detail="Subscription not found")
        
        return subscription
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error updating subscription: {str(e)}")


@router.delete("/webhooks/{subscription_id}", status_code=204)
async def delete_webhook_subscription(subscription_id: str):
    """Delete a webhook subscription."""
    success = await webhook_service.delete_subscription(subscription_id)
    if not success:
        raise HTTPException(status_code=404, detail="Subscription not found")
    return None
