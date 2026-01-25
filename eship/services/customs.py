"""International customs documentation service."""

from typing import Optional
from datetime import datetime
import base64
from io import BytesIO

from reportlab.pdfgen import canvas
from reportlab.lib.pagesizes import letter
from reportlab.lib.units import inch

from ..models import Shipment


class CustomsService:
    """Service for generating international shipping documentation."""

    async def generate_customs_form(self, shipment: Shipment, form_type: str = "CN22") -> str:
        """
        Generate customs form (CN22/CN23).

        Args:
            shipment: Shipment with customs information
            form_type: Type of customs form (CN22 or CN23)

        Returns:
            Base64 encoded PDF of customs form
        """
        if not shipment.customs:
            raise ValueError("Customs information required for international shipments")

        if form_type == "CN22":
            return await self._generate_cn22(shipment)
        elif form_type == "CN23":
            return await self._generate_cn23(shipment)
        else:
            raise ValueError(f"Unsupported customs form type: {form_type}")

    async def generate_commercial_invoice(
        self, shipment: Shipment, invoice_number: Optional[str] = None
    ) -> str:
        """
        Generate commercial invoice for international shipment.

        Args:
            shipment: Shipment details
            invoice_number: Optional invoice number

        Returns:
            Base64 encoded PDF of commercial invoice
        """
        if not shipment.customs:
            raise ValueError("Customs information required for commercial invoice")

        buffer = BytesIO()
        c = canvas.Canvas(buffer, pagesize=letter)
        width, height = letter

        # Header
        c.setFont("Helvetica-Bold", 16)
        c.drawString(1 * inch, height - 1 * inch, "COMMERCIAL INVOICE")

        # Invoice details
        c.setFont("Helvetica", 10)
        y = height - 1.5 * inch

        if invoice_number or shipment.customs.invoice_number:
            c.drawString(
                1 * inch, y, f"Invoice Number: {invoice_number or shipment.customs.invoice_number}"
            )
            y -= 0.25 * inch

        c.drawString(1 * inch, y, f"Date: {datetime.now().strftime('%Y-%m-%d')}")
        y -= 0.5 * inch

        # Exporter information
        c.setFont("Helvetica-Bold", 12)
        c.drawString(1 * inch, y, "Exporter (Shipper):")
        c.setFont("Helvetica", 10)
        y -= 0.25 * inch
        c.drawString(1 * inch, y, shipment.from_address.name)
        y -= 0.2 * inch
        c.drawString(1 * inch, y, shipment.from_address.street1)
        y -= 0.2 * inch
        c.drawString(
            1 * inch,
            y,
            f"{shipment.from_address.city}, {shipment.from_address.state} {shipment.from_address.postal_code}",
        )
        y -= 0.2 * inch
        c.drawString(1 * inch, y, shipment.from_address.country)
        y -= 0.5 * inch

        # Importer information
        c.setFont("Helvetica-Bold", 12)
        c.drawString(1 * inch, y, "Importer (Consignee):")
        c.setFont("Helvetica", 10)
        y -= 0.25 * inch
        c.drawString(1 * inch, y, shipment.to_address.name)
        y -= 0.2 * inch
        c.drawString(1 * inch, y, shipment.to_address.street1)
        y -= 0.2 * inch
        c.drawString(
            1 * inch,
            y,
            f"{shipment.to_address.city}, {shipment.to_address.state} {shipment.to_address.postal_code}",
        )
        y -= 0.2 * inch
        c.drawString(1 * inch, y, shipment.to_address.country)
        y -= 0.5 * inch

        # Items table header
        c.setFont("Helvetica-Bold", 10)
        c.drawString(1 * inch, y, "Item Description")
        c.drawString(4 * inch, y, "HS Code")
        c.drawString(5 * inch, y, "Qty")
        c.drawString(5.75 * inch, y, "Value")
        c.drawString(6.5 * inch, y, "Total")
        c.line(1 * inch, y - 0.1 * inch, 7.5 * inch, y - 0.1 * inch)
        y -= 0.3 * inch

        # Items
        c.setFont("Helvetica", 9)
        total_value = 0.0

        for item in shipment.customs.items:
            item_total = item.value * item.quantity
            total_value += item_total

            c.drawString(1 * inch, y, item.description[:30])
            c.drawString(4 * inch, y, item.hs_code or "N/A")
            c.drawString(5 * inch, y, str(item.quantity))
            c.drawString(5.75 * inch, y, f"${item.value:.2f}")
            c.drawString(6.5 * inch, y, f"${item_total:.2f}")
            y -= 0.25 * inch

        # Total
        y -= 0.2 * inch
        c.line(1 * inch, y, 7.5 * inch, y)
        y -= 0.3 * inch
        c.setFont("Helvetica-Bold", 10)
        c.drawString(5.75 * inch, y, "TOTAL:")
        c.drawString(6.5 * inch, y, f"${total_value:.2f} USD")

        # Certifications
        y -= 0.5 * inch
        c.setFont("Helvetica", 9)
        c.drawString(1 * inch, y, "I certify that all information is true and correct.")
        y -= 0.3 * inch
        c.drawString(1 * inch, y, f"Signature: {shipment.customs.customs_signer}")
        y -= 0.2 * inch
        c.drawString(1 * inch, y, f"Date: {datetime.now().strftime('%Y-%m-%d')}")

        # EEI/PFC if provided
        if shipment.customs.eel_pfc:
            y -= 0.4 * inch
            c.drawString(1 * inch, y, f"EEI/PFC: {shipment.customs.eel_pfc}")

        c.showPage()
        c.save()

        buffer.seek(0)
        return base64.b64encode(buffer.read()).decode()

    async def _generate_cn22(self, shipment: Shipment) -> str:
        """Generate CN22 customs form (for items under $400)."""

        buffer = BytesIO()
        c = canvas.Canvas(buffer, pagesize=(4 * inch, 3 * inch))

        c.setFont("Helvetica-Bold", 12)
        c.drawString(0.25 * inch, 2.75 * inch, "CN 22 - Customs Declaration")

        c.setFont("Helvetica", 9)
        y = 2.4 * inch

        # Contents type
        c.drawString(0.25 * inch, y, f"Contents: {shipment.customs.contents_type}")
        y -= 0.25 * inch

        # Items
        total_value = 0.0
        for item in shipment.customs.items[:3]:  # CN22 has limited space
            item_total = item.value * item.quantity
            total_value += item_total
            c.drawString(0.25 * inch, y, f"{item.quantity}x {item.description[:25]}")
            y -= 0.2 * inch

        # Total weight and value
        y -= 0.1 * inch
        total_weight = sum(item.weight * item.quantity for item in shipment.customs.items)
        c.drawString(0.25 * inch, y, f"Total Weight: {total_weight:.2f} lbs")
        y -= 0.2 * inch
        c.drawString(0.25 * inch, y, f"Total Value: ${total_value:.2f} USD")

        # Origin
        y -= 0.3 * inch
        c.drawString(0.25 * inch, y, f"Origin: {shipment.from_address.country}")

        c.showPage()
        c.save()

        buffer.seek(0)
        return base64.b64encode(buffer.read()).decode()

    async def _generate_cn23(self, shipment: Shipment) -> str:
        """Generate CN23 customs form (for items over $400)."""

        buffer = BytesIO()
        c = canvas.Canvas(buffer, pagesize=letter)
        width, height = letter

        c.setFont("Helvetica-Bold", 14)
        c.drawString(1 * inch, height - 1 * inch, "CN 23 - Customs Declaration")

        y = height - 1.5 * inch
        c.setFont("Helvetica", 10)

        # Sender
        c.setFont("Helvetica-Bold", 10)
        c.drawString(1 * inch, y, "Sender:")
        c.setFont("Helvetica", 9)
        y -= 0.2 * inch
        c.drawString(1 * inch, y, shipment.from_address.name)
        y -= 0.15 * inch
        c.drawString(1 * inch, y, shipment.from_address.street1)
        y -= 0.15 * inch
        c.drawString(
            1 * inch,
            y,
            f"{shipment.from_address.city}, {shipment.from_address.state} {shipment.from_address.postal_code}",
        )
        y -= 0.15 * inch
        c.drawString(1 * inch, y, shipment.from_address.country)
        y -= 0.4 * inch

        # Addressee
        c.setFont("Helvetica-Bold", 10)
        c.drawString(1 * inch, y, "Addressee:")
        c.setFont("Helvetica", 9)
        y -= 0.2 * inch
        c.drawString(1 * inch, y, shipment.to_address.name)
        y -= 0.15 * inch
        c.drawString(1 * inch, y, shipment.to_address.street1)
        y -= 0.15 * inch
        c.drawString(
            1 * inch,
            y,
            f"{shipment.to_address.city}, {shipment.to_address.state} {shipment.to_address.postal_code}",
        )
        y -= 0.15 * inch
        c.drawString(1 * inch, y, shipment.to_address.country)
        y -= 0.4 * inch

        # Contents
        c.setFont("Helvetica-Bold", 10)
        c.drawString(1 * inch, y, "Detailed Description of Contents:")
        y -= 0.25 * inch

        c.setFont("Helvetica", 8)
        c.drawString(1 * inch, y, "Description")
        c.drawString(4 * inch, y, "Qty")
        c.drawString(4.5 * inch, y, "Weight")
        c.drawString(5.5 * inch, y, "Value")
        c.line(1 * inch, y - 0.05 * inch, 7 * inch, y - 0.05 * inch)
        y -= 0.2 * inch

        total_value = 0.0
        total_weight = 0.0

        for item in shipment.customs.items:
            item_total = item.value * item.quantity
            item_weight = item.weight * item.quantity
            total_value += item_total
            total_weight += item_weight

            c.drawString(1 * inch, y, item.description[:35])
            c.drawString(4 * inch, y, str(item.quantity))
            c.drawString(4.5 * inch, y, f"{item_weight:.2f}")
            c.drawString(5.5 * inch, y, f"${item_total:.2f}")
            y -= 0.2 * inch

        # Totals
        y -= 0.1 * inch
        c.line(1 * inch, y, 7 * inch, y)
        y -= 0.2 * inch
        c.setFont("Helvetica-Bold", 9)
        c.drawString(4 * inch, y, "Total:")
        c.drawString(4.5 * inch, y, f"{total_weight:.2f} lbs")
        c.drawString(5.5 * inch, y, f"${total_value:.2f} USD")

        # Additional info
        y -= 0.4 * inch
        c.setFont("Helvetica", 9)
        c.drawString(1 * inch, y, f"Category: {shipment.customs.contents_type}")
        y -= 0.2 * inch

        if shipment.customs.eel_pfc:
            c.drawString(1 * inch, y, f"EEI/PFC: {shipment.customs.eel_pfc}")
            y -= 0.2 * inch

        # Certification
        y -= 0.3 * inch
        c.drawString(
            1 * inch, y, "I certify that the particulars given in this declaration are correct."
        )
        y -= 0.3 * inch
        c.drawString(1 * inch, y, f"Signer: {shipment.customs.customs_signer}")
        y -= 0.2 * inch
        c.drawString(1 * inch, y, f"Date: {datetime.now().strftime('%Y-%m-%d')}")

        c.showPage()
        c.save()

        buffer.seek(0)
        return base64.b64encode(buffer.read()).decode()
