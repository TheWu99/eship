"""Core domain models for the shipping platform."""

from enum import Enum
from datetime import datetime
from typing import Optional, List
from pydantic import BaseModel, Field


class CarrierType(str, Enum):
    """Supported shipping carriers."""

    UPS = "UPS"
    FEDEX = "FedEx"
    USPS = "USPS"
    DHL = "DHL"
    OTHER = "Other"


class AddressType(str, Enum):
    """Address classification types."""

    RESIDENTIAL = "residential"
    COMMERCIAL = "commercial"


class TrackingState(str, Enum):
    """Standardized tracking states across all carriers."""

    PRE_TRANSIT = "pre_transit"
    IN_TRANSIT = "in_transit"
    OUT_FOR_DELIVERY = "out_for_delivery"
    DELIVERED = "delivered"
    RETURNED = "returned"
    FAILED = "failed"
    CANCELLED = "cancelled"
    EXCEPTION = "exception"


class LabelFormat(str, Enum):
    """Supported label formats."""

    ZPL = "ZPL"
    PDF = "PDF"
    PNG = "PNG"


class Address(BaseModel):
    """Shipping address model."""

    name: str = Field(..., description="Recipient or business name")
    street1: str = Field(..., description="Primary street address")
    street2: Optional[str] = Field(None, description="Apartment, suite, etc.")
    city: str = Field(..., description="City name")
    state: str = Field(..., description="State/province code")
    postal_code: str = Field(..., description="Postal/ZIP code")
    country: str = Field(default="US", description="ISO 2-letter country code")
    phone: Optional[str] = Field(None, description="Contact phone number")
    email: Optional[str] = Field(None, description="Contact email")
    address_type: Optional[AddressType] = Field(None, description="Residential or commercial")
    is_validated: bool = Field(default=False, description="Whether address has been validated")


class Package(BaseModel):
    """Package dimensions and weight."""

    weight: float = Field(..., description="Weight in pounds", gt=0)
    length: float = Field(..., description="Length in inches", gt=0)
    width: float = Field(..., description="Width in inches", gt=0)
    height: float = Field(..., description="Height in inches", gt=0)
    value: Optional[float] = Field(None, description="Declared value in USD")
    description: Optional[str] = Field(None, description="Contents description")


class Rate(BaseModel):
    """Shipping rate from a carrier."""

    carrier: CarrierType
    service: str = Field(..., description="Service level (e.g., Ground, Express)")
    rate: float = Field(..., description="Shipping cost in USD")
    currency: str = Field(default="USD", description="Currency code")
    delivery_days: Optional[int] = Field(None, description="Estimated delivery days")
    carrier_rate_id: Optional[str] = Field(None, description="Carrier's rate identifier")


class TrackingEvent(BaseModel):
    """Individual tracking event."""

    timestamp: datetime
    status: TrackingState
    message: str
    location: Optional[str] = Field(None, description="Event location")
    carrier_status: Optional[str] = Field(None, description="Original carrier status code")


class ShipmentTracking(BaseModel):
    """Complete tracking information for a shipment."""

    tracking_number: str
    carrier: CarrierType
    current_status: TrackingState
    events: List[TrackingEvent] = Field(default_factory=list)
    estimated_delivery: Optional[datetime] = None
    actual_delivery: Optional[datetime] = None


class Label(BaseModel):
    """Shipping label."""

    tracking_number: str
    carrier: CarrierType
    format: LabelFormat
    content: str = Field(..., description="Base64 encoded label content")
    created_at: datetime = Field(default_factory=datetime.now)


class CustomsItem(BaseModel):
    """Item for customs declaration."""

    description: str
    quantity: int = Field(..., gt=0)
    value: float = Field(..., description="Value per unit in USD", gt=0)
    weight: float = Field(..., description="Weight per unit in pounds", gt=0)
    hs_code: Optional[str] = Field(None, description="Harmonized System code")
    origin_country: str = Field(default="US", description="Country of origin")


class CustomsForm(BaseModel):
    """Customs declaration form."""

    contents_type: str = Field(default="merchandise", description="Type of goods")
    contents_explanation: Optional[str] = None
    customs_certify: bool = Field(default=True)
    customs_signer: str
    items: List[CustomsItem]
    eel_pfc: Optional[str] = Field(None, description="EEI/PFC code")
    invoice_number: Optional[str] = None


class Shipment(BaseModel):
    """Complete shipment request."""

    id: Optional[str] = Field(None, description="Shipment identifier")
    from_address: Address
    to_address: Address
    package: Package
    carrier: Optional[CarrierType] = Field(None, description="Preferred carrier")
    service: Optional[str] = Field(None, description="Service level")
    label_format: LabelFormat = Field(default=LabelFormat.PDF)
    customs: Optional[CustomsForm] = Field(None, description="Customs form for international")
    reference: Optional[str] = Field(None, description="Customer reference number")
    created_at: datetime = Field(default_factory=datetime.now)


class WebhookSubscription(BaseModel):
    """Webhook subscription for tracking updates."""

    id: Optional[str] = None
    url: str = Field(..., description="Webhook endpoint URL")
    events: List[TrackingState] = Field(
        default_factory=lambda: list(TrackingState), description="Events to subscribe to"
    )
    tracking_numbers: Optional[List[str]] = Field(None, description="Specific tracking numbers")
    active: bool = Field(default=True)
    created_at: datetime = Field(default_factory=datetime.now)


class WebhookEvent(BaseModel):
    """Webhook event payload."""

    event_id: str
    event_type: str
    timestamp: datetime
    tracking_number: str
    carrier: CarrierType
    status: TrackingState
    event_details: TrackingEvent
