"""Multi-carrier rating engine service."""

from typing import List
from ..models import Shipment, Rate, CarrierType, Package


class RatingService:
    """Service for retrieving shipping rates from multiple carriers."""

    async def get_rates(self, shipment: Shipment) -> List[Rate]:
        """
        Get shipping rates from all available carriers.

        Args:
            shipment: Shipment details

        Returns:
            List of available rates from all carriers
        """
        rates = []

        # In a real implementation, this would make API calls to each carrier
        # For now, we'll return mock rates

        carriers = [CarrierType.UPS, CarrierType.FEDEX, CarrierType.USPS, CarrierType.DHL]

        for carrier in carriers:
            carrier_rates = await self._get_carrier_rates(carrier, shipment)
            rates.extend(carrier_rates)

        # Sort by rate (cheapest first)
        rates.sort(key=lambda r: r.rate)

        return rates

    async def _get_carrier_rates(self, carrier: CarrierType, shipment: Shipment) -> List[Rate]:
        """Get rates from a specific carrier."""

        # Mock rate calculation based on weight and distance
        base_rate = self._calculate_base_rate(shipment.package)

        rates = []

        if carrier == CarrierType.UPS:
            rates = [
                Rate(
                    carrier=carrier,
                    service="Ground",
                    rate=base_rate * 1.0,
                    delivery_days=5,
                    carrier_rate_id=f"UPS_GND_{id(shipment)}",
                ),
                Rate(
                    carrier=carrier,
                    service="2nd Day Air",
                    rate=base_rate * 1.8,
                    delivery_days=2,
                    carrier_rate_id=f"UPS_2DA_{id(shipment)}",
                ),
                Rate(
                    carrier=carrier,
                    service="Next Day Air",
                    rate=base_rate * 3.0,
                    delivery_days=1,
                    carrier_rate_id=f"UPS_NDA_{id(shipment)}",
                ),
            ]
        elif carrier == CarrierType.FEDEX:
            rates = [
                Rate(
                    carrier=carrier,
                    service="Ground",
                    rate=base_rate * 0.95,
                    delivery_days=5,
                    carrier_rate_id=f"FDX_GND_{id(shipment)}",
                ),
                Rate(
                    carrier=carrier,
                    service="Express Saver",
                    rate=base_rate * 1.7,
                    delivery_days=3,
                    carrier_rate_id=f"FDX_ES_{id(shipment)}",
                ),
                Rate(
                    carrier=carrier,
                    service="Priority Overnight",
                    rate=base_rate * 2.8,
                    delivery_days=1,
                    carrier_rate_id=f"FDX_PO_{id(shipment)}",
                ),
            ]
        elif carrier == CarrierType.USPS:
            rates = [
                Rate(
                    carrier=carrier,
                    service="First-Class",
                    rate=base_rate * 0.5,
                    delivery_days=3,
                    carrier_rate_id=f"USPS_FC_{id(shipment)}",
                ),
                Rate(
                    carrier=carrier,
                    service="Priority Mail",
                    rate=base_rate * 0.8,
                    delivery_days=2,
                    carrier_rate_id=f"USPS_PM_{id(shipment)}",
                ),
                Rate(
                    carrier=carrier,
                    service="Priority Mail Express",
                    rate=base_rate * 2.2,
                    delivery_days=1,
                    carrier_rate_id=f"USPS_PME_{id(shipment)}",
                ),
            ]
        elif carrier == CarrierType.DHL:
            rates = [
                Rate(
                    carrier=carrier,
                    service="Ground",
                    rate=base_rate * 1.1,
                    delivery_days=5,
                    carrier_rate_id=f"DHL_GND_{id(shipment)}",
                ),
                Rate(
                    carrier=carrier,
                    service="Express",
                    rate=base_rate * 2.5,
                    delivery_days=2,
                    carrier_rate_id=f"DHL_EXP_{id(shipment)}",
                ),
            ]

        return rates

    def _calculate_base_rate(self, package: Package) -> float:
        """Calculate base rate from package dimensions and weight."""
        # Simple calculation: weight-based pricing with dimensional weight consideration
        dim_weight = (package.length * package.width * package.height) / 166
        billable_weight = max(package.weight, dim_weight)

        # Base rate: $5 + $0.50 per pound
        return 5.0 + (billable_weight * 0.50)
