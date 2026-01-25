"""Label generation API endpoints."""

from fastapi import APIRouter, HTTPException
import uuid

from ..models import Shipment, Label, LabelFormat, CarrierType
from ..services import LabelGenerationService

router = APIRouter()
label_service = LabelGenerationService()


@router.post("/labels", response_model=Label)
async def generate_shipping_label(shipment: Shipment):
    """
    Generate a shipping label.
    
    Creates a carrier-compliant shipping label in the specified format (ZPL, PDF, or PNG).
    The label includes:
    - Tracking number and barcode/QR code
    - Sender and recipient addresses
    - Package information
    - Carrier branding
    
    - **shipment**: Complete shipment details
    - **label_format**: Desired format (ZPL for thermal printers, PDF for standard printers, PNG for images)
    """
    try:
        # Generate tracking number (in production, this would come from carrier API)
        tracking_number = _generate_tracking_number(shipment.carrier or CarrierType.UPS)
        
        # Use shipment's preferred carrier or default to UPS
        carrier = shipment.carrier or CarrierType.UPS
        
        label = await label_service.generate_label(
            shipment=shipment,
            tracking_number=tracking_number,
            carrier=carrier,
            format=shipment.label_format
        )
        
        return label
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error generating label: {str(e)}")


def _generate_tracking_number(carrier: CarrierType) -> str:
    """Generate a mock tracking number based on carrier format."""
    unique_id = str(uuid.uuid4().hex)[:16].upper()
    
    if carrier == CarrierType.UPS:
        return f"1Z{unique_id}"
    elif carrier == CarrierType.FEDEX:
        return unique_id[:12]
    elif carrier == CarrierType.USPS:
        return f"92{unique_id[:18]}"
    elif carrier == CarrierType.DHL:
        return unique_id[:10]
    else:
        return unique_id[:15]
