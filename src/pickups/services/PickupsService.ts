/**
 * Pickups and Manifesting Service
 */

import { Pickup, Manifest, Address, Shipment } from '../../common/interfaces';
import { CarrierType } from '../../common/types';

export interface PickupRequest {
  carrier: CarrierType;
  address: Address;
  date: Date;
  readyTime: string; // HH:MM format
  closeTime: string; // HH:MM format
  shipments: string[]; // shipment IDs
  specialInstructions?: string;
  packageCount?: number;
  totalWeight?: number;
}

export interface ManifestRequest {
  carrier: CarrierType;
  date: Date;
  shipments: string[];
}

export class PickupsService {
  /**
   * Schedule a carrier pickup
   */
  async schedulePickup(request: PickupRequest): Promise<Pickup> {
    // Validate pickup time
    this.validatePickupTime(request.readyTime, request.closeTime);

    const pickup: Pickup = {
      id: this.generateId('PU'),
      carrier: request.carrier,
      address: request.address,
      date: request.date,
      readyTime: request.readyTime,
      closeTime: request.closeTime,
      shipments: request.shipments,
      confirmationNumber: this.generateConfirmationNumber(request.carrier),
      specialInstructions: request.specialInstructions,
      status: 'scheduled',
    };

    // In production, this would call carrier API
    // e.g., USPS Package Pickup API, UPS Pickup API, FedEx Pickup API

    return pickup;
  }

  /**
   * Cancel a scheduled pickup
   */
  async cancelPickup(pickupId: string): Promise<void> {
    // In production, would call carrier API to cancel
    // and update database
  }

  /**
   * Get pickup status
   */
  async getPickupStatus(pickupId: string): Promise<Pickup> {
    // In production, would fetch from database
    throw new Error('Not implemented - would fetch from database');
  }

  /**
   * Update pickup status
   */
  async updatePickupStatus(
    pickupId: string,
    status: 'scheduled' | 'confirmed' | 'completed' | 'cancelled'
  ): Promise<Pickup> {
    // In production, would update database
    throw new Error('Not implemented - would update database');
  }

  /**
   * Get available pickup time windows for a carrier
   */
  getAvailablePickupWindows(carrier: CarrierType, date: Date): Array<{
    start: string;
    end: string;
  }> {
    // Different carriers have different pickup windows
    switch (carrier) {
      case 'USPS':
        return [
          { start: '08:00', end: '17:00' },
        ];
      case 'UPS':
        return [
          { start: '09:00', end: '17:00' },
        ];
      case 'FedEx':
        return [
          { start: '08:00', end: '18:00' },
        ];
      default:
        return [
          { start: '09:00', end: '17:00' },
        ];
    }
  }

  private validatePickupTime(readyTime: string, closeTime: string): void {
    const ready = this.parseTime(readyTime);
    const close = this.parseTime(closeTime);

    if (ready >= close) {
      throw new Error('Ready time must be before close time');
    }

    if (close - ready < 2 * 60) {
      throw new Error('Pickup window must be at least 2 hours');
    }
  }

  private parseTime(time: string): number {
    const [hours, minutes] = time.split(':').map(Number);
    return hours * 60 + minutes;
  }

  private generateId(prefix: string): string {
    return `${prefix}-${Date.now()}-${Math.random().toString(36).slice(2, 11)}`;
  }

  private generateConfirmationNumber(carrier: CarrierType): string {
    const prefix = carrier.substring(0, 3).toUpperCase();
    const number = Math.random().toString().slice(2, 12);
    return `${prefix}${number}`;
  }
}

export class ManifestingService {
  /**
   * Generate end-of-day manifest
   */
  async generateManifest(request: ManifestRequest): Promise<Manifest> {
    const manifest: Manifest = {
      id: this.generateId('MAN'),
      carrier: request.carrier,
      date: request.date,
      shipments: request.shipments,
      documentUrl: this.generateManifestUrl(),
      createdAt: new Date(),
    };

    // For USPS, also generate SCAN form
    if (request.carrier === 'USPS') {
      manifest.scanFormUrl = this.generateScanFormUrl();
    }

    // In production, this would:
    // 1. Call carrier API to create manifest
    // 2. Generate manifest document (PDF)
    // 3. Generate SCAN form for USPS
    // 4. Store in database

    return manifest;
  }

  /**
   * Generate USPS SCAN form (Shipment Confirmation Acceptance Notice)
   */
  async generateUSPSScanForm(shipments: string[]): Promise<{
    formId: string;
    formUrl: string;
    barcodeUrl: string;
  }> {
    const formId = this.generateId('SCAN');

    return {
      formId,
      formUrl: `https://api.eship.com/scan-forms/${formId}.pdf`,
      barcodeUrl: `https://api.eship.com/scan-forms/${formId}-barcode.png`,
    };
  }

  /**
   * Close out shipments for the day (create manifest and SCAN form)
   */
  async closeOutDay(
    carrier: CarrierType,
    shipments: Shipment[]
  ): Promise<{
    manifest: Manifest;
    scanForm?: any;
  }> {
    const shipmentIds = shipments.map((s) => s.id);

    const manifest = await this.generateManifest({
      carrier,
      date: new Date(),
      shipments: shipmentIds,
    });

    let scanForm;
    if (carrier === 'USPS') {
      scanForm = await this.generateUSPSScanForm(shipmentIds);
    }

    return {
      manifest,
      scanForm,
    };
  }

  /**
   * Get manifest by ID
   */
  async getManifest(manifestId: string): Promise<Manifest> {
    // In production, would fetch from database
    throw new Error('Not implemented - would fetch from database');
  }

  /**
   * List manifests for a date range
   */
  async listManifests(
    startDate: Date,
    endDate: Date,
    carrier?: CarrierType
  ): Promise<Manifest[]> {
    // In production, would query database
    return [];
  }

  private generateId(prefix: string): string {
    return `${prefix}-${Date.now()}-${Math.random().toString(36).slice(2, 11)}`;
  }

  private generateManifestUrl(): string {
    const id = Math.random().toString(36).slice(2, 14);
    return `https://api.eship.com/manifests/${id}.pdf`;
  }

  private generateScanFormUrl(): string {
    const id = Math.random().toString(36).slice(2, 14);
    return `https://api.eship.com/scan-forms/${id}.pdf`;
  }
}
