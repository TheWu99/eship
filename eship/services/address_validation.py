"""Address validation and standardization service."""

from typing import Tuple, Optional
from ..models import Address, AddressType


class AddressValidationService:
    """Service for validating and standardizing addresses."""
    
    async def validate_address(self, address: Address) -> Tuple[Address, bool]:
        """
        Validate and standardize an address.
        
        Args:
            address: Address to validate
            
        Returns:
            Tuple of (standardized address, is_valid)
        """
        # In a real implementation, this would integrate with USPS, SmartyStreets, etc.
        # For now, we'll do basic validation and standardization
        
        validated = address.model_copy(deep=True)
        is_valid = True
        
        # Basic validation checks
        if not validated.street1 or not validated.city:
            is_valid = False
        
        if not validated.postal_code:
            is_valid = False
        
        # Standardize country code
        validated.country = validated.country.upper()
        
        # Standardize state to uppercase
        validated.state = validated.state.upper()
        
        # Clean postal code
        if validated.country == "US":
            # US ZIP code standardization
            postal = validated.postal_code.replace(" ", "").replace("-", "")
            if len(postal) == 5 or len(postal) == 9:
                if len(postal) == 9:
                    validated.postal_code = f"{postal[:5]}-{postal[5:]}"
                else:
                    validated.postal_code = postal
            else:
                is_valid = False
        
        validated.is_validated = is_valid
        
        return validated, is_valid
    
    async def detect_address_type(self, address: Address) -> AddressType:
        """
        Detect if address is residential or commercial.
        
        Args:
            address: Address to classify
            
        Returns:
            Address type classification
        """
        # In a real implementation, this would use carrier APIs or third-party services
        # For now, use simple heuristics
        
        address_text = f"{address.street1} {address.street2 or ''}".lower()
        
        # Business indicators
        business_keywords = [
            'suite', 'ste', 'floor', 'bldg', 'building', 'office',
            'plaza', 'corp', 'llc', 'inc', 'ltd', 'company'
        ]
        
        for keyword in business_keywords:
            if keyword in address_text:
                return AddressType.COMMERCIAL
        
        # Default to residential
        return AddressType.RESIDENTIAL
