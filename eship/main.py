"""FastAPI application and main entry point."""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from .api import rates, labels, tracking, webhooks, address, customs

app = FastAPI(
    title="eShip - Enterprise Shipping Platform",
    description="""
    A comprehensive multi-carrier shipping solution providing:
    
    * **Multi-Carrier Rating Engine**: Compare rates across UPS, FedEx, USPS, DHL, and more
    * **Dynamic Label Generation**: Create shipping labels in ZPL, PDF, or PNG formats
    * **Address Validation**: Verify and standardize addresses globally
    * **Unified Tracking**: Track shipments across all carriers with standardized status codes
    * **Carrier Webhooks**: Subscribe to real-time tracking updates
    * **International Documentation**: Generate customs forms and commercial invoices
    """,
    version="0.1.0",
    docs_url="/",
    redoc_url="/redoc",
)

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include routers
app.include_router(rates.router, prefix="/api/v1", tags=["Rates"])
app.include_router(labels.router, prefix="/api/v1", tags=["Labels"])
app.include_router(tracking.router, prefix="/api/v1", tags=["Tracking"])
app.include_router(webhooks.router, prefix="/api/v1", tags=["Webhooks"])
app.include_router(address.router, prefix="/api/v1", tags=["Address Validation"])
app.include_router(customs.router, prefix="/api/v1", tags=["Customs & International"])


@app.get("/health", tags=["Health"])
async def health_check():
    """Health check endpoint."""
    return {"status": "healthy", "service": "eship"}
