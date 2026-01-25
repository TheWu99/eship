"""Label generation service."""

import base64
from datetime import datetime
from io import BytesIO

from PIL import Image, ImageDraw, ImageFont
from reportlab.pdfgen import canvas
from reportlab.lib.units import inch
from reportlab.lib.utils import ImageReader
import qrcode

from ..models import Label, LabelFormat, CarrierType, Shipment


class LabelGenerationService:
    """Service for generating shipping labels in multiple formats."""

    async def generate_label(
        self,
        shipment: Shipment,
        tracking_number: str,
        carrier: CarrierType,
        format: LabelFormat = LabelFormat.PDF,
    ) -> Label:
        """
        Generate a shipping label.

        Args:
            shipment: Shipment details
            tracking_number: Tracking number for the shipment
            carrier: Carrier handling the shipment
            format: Desired label format

        Returns:
            Generated label
        """
        if format == LabelFormat.ZPL:
            content = await self._generate_zpl_label(shipment, tracking_number, carrier)
        elif format == LabelFormat.PDF:
            content = await self._generate_pdf_label(shipment, tracking_number, carrier)
        elif format == LabelFormat.PNG:
            content = await self._generate_png_label(shipment, tracking_number, carrier)
        else:
            raise ValueError(f"Unsupported label format: {format}")

        return Label(
            tracking_number=tracking_number, carrier=carrier, format=format, content=content
        )

    async def _generate_zpl_label(
        self, shipment: Shipment, tracking_number: str, carrier: CarrierType
    ) -> str:
        """Generate a ZPL (Zebra Programming Language) label."""

        # Basic ZPL template for a 4x6 label
        zpl = f"""^XA
^FO50,50^A0N,50,50^FD{carrier.value}^FS
^FO50,120^BY3^BCN,100,Y,N,N^FD{tracking_number}^FS
^FO50,250^A0N,30,30^FDFrom:^FS
^FO50,290^A0N,25,25^FD{shipment.from_address.name}^FS
^FO50,320^A0N,25,25^FD{shipment.from_address.street1}^FS
^FO50,350^A0N,25,25^FD{shipment.from_address.city}, {shipment.from_address.state} {shipment.from_address.postal_code}^FS
^FO50,400^A0N,30,30^FDTo:^FS
^FO50,440^A0N,25,25^FD{shipment.to_address.name}^FS
^FO50,470^A0N,25,25^FD{shipment.to_address.street1}^FS
^FO50,500^A0N,25,25^FD{shipment.to_address.city}, {shipment.to_address.state} {shipment.to_address.postal_code}^FS
^FO50,550^A0N,20,20^FDWeight: {shipment.package.weight} lbs^FS
^XZ"""

        return base64.b64encode(zpl.encode()).decode()

    async def _generate_pdf_label(
        self, shipment: Shipment, tracking_number: str, carrier: CarrierType
    ) -> str:
        """Generate a PDF label."""

        buffer = BytesIO()

        # Create 4x6 inch label (standard shipping label size)
        c = canvas.Canvas(buffer, pagesize=(4 * inch, 6 * inch))

        # Carrier name
        c.setFont("Helvetica-Bold", 16)
        c.drawString(0.25 * inch, 5.5 * inch, carrier.value)

        # Tracking number and barcode area
        c.setFont("Helvetica-Bold", 12)
        c.drawString(0.25 * inch, 5 * inch, f"Tracking: {tracking_number}")

        # Generate QR code for tracking
        qr = qrcode.QRCode(version=1, box_size=3, border=1)
        qr.add_data(tracking_number)
        qr.make(fit=True)
        qr_img = qr.make_image(fill_color="black", back_color="white")

        # Save QR to temporary buffer and add to PDF
        qr_buffer = BytesIO()
        qr_img.save(qr_buffer, format="PNG")
        qr_buffer.seek(0)
        c.drawImage(
            ImageReader(qr_buffer), 2.5 * inch, 4 * inch, width=1.25 * inch, height=1.25 * inch
        )

        # From address
        c.setFont("Helvetica-Bold", 10)
        c.drawString(0.25 * inch, 3.5 * inch, "FROM:")
        c.setFont("Helvetica", 9)
        c.drawString(0.25 * inch, 3.3 * inch, shipment.from_address.name[:40])
        c.drawString(0.25 * inch, 3.1 * inch, shipment.from_address.street1[:40])
        if shipment.from_address.street2:
            c.drawString(0.25 * inch, 2.9 * inch, shipment.from_address.street2[:40])
        c.drawString(
            0.25 * inch,
            2.7 * inch,
            f"{shipment.from_address.city}, {shipment.from_address.state} {shipment.from_address.postal_code}",
        )

        # To address (larger for readability)
        c.setFont("Helvetica-Bold", 12)
        c.drawString(0.25 * inch, 2.2 * inch, "TO:")
        c.setFont("Helvetica-Bold", 11)
        c.drawString(0.25 * inch, 1.95 * inch, shipment.to_address.name[:40])
        c.setFont("Helvetica", 10)
        c.drawString(0.25 * inch, 1.7 * inch, shipment.to_address.street1[:40])
        if shipment.to_address.street2:
            c.drawString(0.25 * inch, 1.5 * inch, shipment.to_address.street2[:40])
        c.setFont("Helvetica-Bold", 11)
        c.drawString(
            0.25 * inch,
            1.25 * inch,
            f"{shipment.to_address.city}, {shipment.to_address.state} {shipment.to_address.postal_code}",
        )

        # Package info
        c.setFont("Helvetica", 8)
        c.drawString(
            0.25 * inch,
            0.75 * inch,
            f"Weight: {shipment.package.weight} lbs | "
            f"Dims: {shipment.package.length}x{shipment.package.width}x{shipment.package.height} in",
        )

        # Reference number if provided
        if shipment.reference:
            c.drawString(0.25 * inch, 0.5 * inch, f"Ref: {shipment.reference}")

        # Date
        c.drawString(0.25 * inch, 0.25 * inch, f"Date: {datetime.now().strftime('%Y-%m-%d')}")

        c.showPage()
        c.save()

        buffer.seek(0)
        return base64.b64encode(buffer.read()).decode()

    async def _generate_png_label(
        self, shipment: Shipment, tracking_number: str, carrier: CarrierType
    ) -> str:
        """Generate a PNG label."""

        # Create 4x6 inch label at 203 DPI (standard thermal printer resolution)
        dpi = 203
        width = 4 * dpi  # 812 pixels
        height = 6 * dpi  # 1218 pixels

        # Create image
        img = Image.new("RGB", (width, height), "white")
        draw = ImageDraw.Draw(img)

        # Try to use a basic font, fall back to default if not available
        # Try common font paths for different operating systems
        font_paths = [
            "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",  # Linux
            "/System/Library/Fonts/Helvetica.ttc",  # macOS
            "C:\\Windows\\Fonts\\arial.ttf",  # Windows
        ]

        try:
            # Try to find available font
            font_path = None
            for path in font_paths:
                try:
                    ImageFont.truetype(path, 12)
                    font_path = path
                    break
                except Exception:
                    continue

            if font_path:
                title_font = ImageFont.truetype(font_path, 32)
                header_font = ImageFont.truetype(font_path, 20)
                body_font = ImageFont.truetype(font_path, 18)
                small_font = ImageFont.truetype(font_path, 14)
            else:
                raise Exception("No TrueType fonts found")
        except Exception:
            title_font = header_font = body_font = small_font = ImageFont.load_default()

        # Carrier name
        draw.text((50, 50), carrier.value, fill="black", font=title_font)

        # Tracking number
        draw.text((50, 100), f"Tracking: {tracking_number}", fill="black", font=header_font)

        # Generate QR code
        qr = qrcode.QRCode(version=1, box_size=6, border=1)
        qr.add_data(tracking_number)
        qr.make(fit=True)
        qr_img = qr.make_image(fill_color="black", back_color="white")
        qr_img = qr_img.resize((250, 250))
        img.paste(qr_img, (500, 80))

        # From address
        y = 350
        draw.text((50, y), "FROM:", fill="black", font=header_font)
        y += 35
        draw.text((50, y), shipment.from_address.name[:40], fill="black", font=body_font)
        y += 30
        draw.text((50, y), shipment.from_address.street1[:40], fill="black", font=body_font)
        y += 30
        if shipment.from_address.street2:
            draw.text((50, y), shipment.from_address.street2[:40], fill="black", font=body_font)
            y += 30
        draw.text(
            (50, y),
            f"{shipment.from_address.city}, {shipment.from_address.state} {shipment.from_address.postal_code}",
            fill="black",
            font=body_font,
        )

        # To address
        y = 650
        draw.text((50, y), "TO:", fill="black", font=header_font)
        y += 40
        draw.text((50, y), shipment.to_address.name[:40], fill="black", font=header_font)
        y += 35
        draw.text((50, y), shipment.to_address.street1[:40], fill="black", font=body_font)
        y += 30
        if shipment.to_address.street2:
            draw.text((50, y), shipment.to_address.street2[:40], fill="black", font=body_font)
            y += 30
        draw.text(
            (50, y),
            f"{shipment.to_address.city}, {shipment.to_address.state} {shipment.to_address.postal_code}",
            fill="black",
            font=header_font,
        )

        # Package info
        y = height - 150
        draw.text(
            (50, y),
            f"Weight: {shipment.package.weight} lbs | Dims: {shipment.package.length}x{shipment.package.width}x{shipment.package.height} in",
            fill="black",
            font=small_font,
        )

        # Reference
        if shipment.reference:
            y += 30
            draw.text((50, y), f"Ref: {shipment.reference}", fill="black", font=small_font)

        # Date
        y = height - 50
        draw.text(
            (50, y), f"Date: {datetime.now().strftime('%Y-%m-%d')}", fill="black", font=small_font
        )

        # Convert to base64
        buffer = BytesIO()
        img.save(buffer, format="PNG")
        buffer.seek(0)
        return base64.b64encode(buffer.read()).decode()
