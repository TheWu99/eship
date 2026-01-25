"""Tracking API endpoints."""

from typing import Optional
from fastapi import APIRouter, HTTPException, Query

from ..models import ShipmentTracking, CarrierType
from ..services import TrackingService

router = APIRouter()
tracking_service = TrackingService()


@router.get("/tracking/{tracking_number}", response_model=ShipmentTracking)
async def get_tracking_info(
    tracking_number: str,
    carrier: Optional[CarrierType] = Query(None, description="Optional carrier filter")
):
    """
    Get tracking information for a shipment.
    
    Retrieves unified tracking information from the carrier's API and standardizes
    the tracking events into common states (pre_transit, in_transit, delivered, etc.).
    
    The system automatically detects the carrier from the tracking number format if
    not explicitly provided.
    
    - **tracking_number**: The shipment's tracking number
    - **carrier**: Optional carrier to filter by (auto-detected if not provided)
    """
    try:
        tracking = await tracking_service.get_tracking(tracking_number, carrier)
        
        if not tracking:
            raise HTTPException(
                status_code=404,
                detail=f"Tracking information not found for {tracking_number}"
            )
        
        return tracking
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error retrieving tracking: {str(e)}")
