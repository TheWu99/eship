"""Rating API endpoints."""

from typing import List
from fastapi import APIRouter, HTTPException

from ..models import Shipment, Rate
from ..services import RatingService

router = APIRouter()
rating_service = RatingService()


@router.post("/rates", response_model=List[Rate])
async def get_shipping_rates(shipment: Shipment):
    """
    Get shipping rates from all carriers.
    
    Retrieves real-time shipping rates from multiple carriers (UPS, FedEx, USPS, DHL)
    for the given shipment details. Returns rates sorted by price (cheapest first).
    
    - **from_address**: Sender's address
    - **to_address**: Recipient's address
    - **package**: Package dimensions and weight
    - **carrier**: Optional preferred carrier filter
    """
    try:
        rates = await rating_service.get_rates(shipment)
        return rates
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error retrieving rates: {str(e)}")
