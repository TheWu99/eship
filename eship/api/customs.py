"""Customs and international shipping API endpoints."""

from fastapi import APIRouter, HTTPException, Query

from ..models import Shipment
from ..services import CustomsService

router = APIRouter()
customs_service = CustomsService()


@router.post("/customs/form")
async def generate_customs_form(
    shipment: Shipment,
    form_type: str = Query("CN22", description="Form type: CN22 or CN23")
):
    """
    Generate customs declaration form.
    
    Creates the appropriate customs form for international shipments:
    - **CN22**: For items under $400 USD (simplified form)
    - **CN23**: For items over $400 USD (detailed declaration)
    
    The form includes:
    - Itemized contents with descriptions and values
    - Total weight and value
    - Sender and recipient information
    - Certification signature
    
    Returns base64-encoded PDF of the customs form.
    """
    if form_type not in ["CN22", "CN23"]:
        raise HTTPException(
            status_code=400,
            detail="Invalid form type. Must be 'CN22' or 'CN23'"
        )
    
    try:
        form_pdf = await customs_service.generate_customs_form(shipment, form_type)
        return {
            "form_type": form_type,
            "format": "PDF",
            "content": form_pdf
        }
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error generating customs form: {str(e)}")


@router.post("/customs/commercial-invoice")
async def generate_commercial_invoice(
    shipment: Shipment,
    invoice_number: str = Query(None, description="Optional invoice number")
):
    """
    Generate commercial invoice for international shipment.
    
    Creates a detailed commercial invoice required for customs clearance.
    The invoice includes:
    - Complete sender (exporter) and recipient (importer) details
    - Itemized list of contents with HS codes
    - Individual and total values
    - Origin country information
    - Certification and signature
    - EEI/PFC code if applicable
    
    This document is required for most international commercial shipments
    and helps facilitate customs clearance.
    
    Returns base64-encoded PDF of the commercial invoice.
    """
    try:
        invoice_pdf = await customs_service.generate_commercial_invoice(
            shipment, invoice_number
        )
        return {
            "document_type": "commercial_invoice",
            "format": "PDF",
            "invoice_number": invoice_number or shipment.customs.invoice_number if shipment.customs else None,
            "content": invoice_pdf
        }
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Error generating commercial invoice: {str(e)}"
        )
