"""Tests for address validation service."""

import pytest
from eship.models import Address, AddressType
from eship.services import AddressValidationService


@pytest.fixture
def address_service():
    return AddressValidationService()


@pytest.mark.asyncio
async def test_validate_valid_us_address(address_service):
    """Test validation of a valid US address."""
    address = Address(
        name="John Doe",
        street1="123 Main St",
        city="New York",
        state="NY",
        postal_code="10001",
        country="US"
    )
    
    validated, is_valid = await address_service.validate_address(address)
    
    assert is_valid
    assert validated.is_validated
    assert validated.state == "NY"
    assert validated.country == "US"
    assert validated.postal_code == "10001"


@pytest.mark.asyncio
async def test_validate_zip_plus_4(address_service):
    """Test validation of ZIP+4 format."""
    address = Address(
        name="Jane Smith",
        street1="456 Oak Ave",
        city="Los Angeles",
        state="ca",
        postal_code="900011234",
        country="us"
    )
    
    validated, is_valid = await address_service.validate_address(address)
    
    assert is_valid
    assert validated.postal_code == "90001-1234"
    assert validated.state == "CA"
    assert validated.country == "US"


@pytest.mark.asyncio
async def test_detect_commercial_address(address_service):
    """Test detection of commercial address."""
    address = Address(
        name="Acme Corp",
        street1="100 Business Plaza Suite 200",
        city="Chicago",
        state="IL",
        postal_code="60601",
        country="US"
    )
    
    address_type = await address_service.detect_address_type(address)
    
    assert address_type == AddressType.COMMERCIAL


@pytest.mark.asyncio
async def test_detect_residential_address(address_service):
    """Test detection of residential address."""
    address = Address(
        name="John Doe",
        street1="123 Maple Lane",
        city="Portland",
        state="OR",
        postal_code="97201",
        country="US"
    )
    
    address_type = await address_service.detect_address_type(address)
    
    assert address_type == AddressType.RESIDENTIAL
