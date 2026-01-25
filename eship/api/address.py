"""Address validation API endpoints."""

from fastapi import APIRouter, HTTPException

from ..models import Address
from ..services import AddressValidationService

router = APIRouter()
address_service = AddressValidationService()


@router.post("/address/validate")
async def validate_address(address: Address):
    """
    Validate and standardize an address.

    Verifies the address format, standardizes fields (postal codes, state codes),
    and checks for common errors. This helps prevent delivery failures and
    unexpected surcharges.

    Returns:
    - **address**: Standardized address
    - **is_valid**: Whether the address passed validation
    - **address_type**: Detected type (residential or commercial)

    Prevents common issues:
    - Invalid postal codes
    - Missing required fields
    - Incorrect state/province codes
    - Format inconsistencies
    """
    try:
        validated_address, is_valid = await address_service.validate_address(address)

        # Also detect address type
        address_type = await address_service.detect_address_type(validated_address)
        validated_address.address_type = address_type

        return {"address": validated_address, "is_valid": is_valid, "address_type": address_type}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error validating address: {str(e)}")


@router.post("/address/classify", response_model=dict)
async def classify_address(address: Address):
    """
    Classify an address as residential or commercial.

    Determines whether an address is residential or commercial to help
    calculate accurate shipping rates and prevent unexpected residential
    delivery surcharges.

    Returns:
    - **address_type**: "residential" or "commercial"
    - **confidence**: Classification confidence (currently always "medium" for heuristic-based)
    """
    try:
        address_type = await address_service.detect_address_type(address)
        return {
            "address_type": address_type,
            "confidence": "medium",  # In production, this would be based on data sources
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error classifying address: {str(e)}")
