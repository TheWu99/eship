/**
 * Sandbox Environment Module
 * Provides a dedicated testing mode to simulate label purchases and tracking events
 * without incurring real-world costs
 */

import { Label, TrackingEvent, ShipmentRequest, SandboxConfig } from '../types';

export class SandboxEnvironment {
  private config: SandboxConfig;
  private mockLabels: Map<string, Label>;
  private mockTrackingEvents: Map<string, TrackingEvent[]>;

  constructor(config: SandboxConfig = {
    enabled: true,
    autoGenerateTracking: true,
    simulateDeliveryDelay: 5000
  }) {
    this.config = config;
    this.mockLabels = new Map();
    this.mockTrackingEvents = new Map();
  }

  /**
   * Check if sandbox mode is enabled
   */
  isEnabled(): boolean {
    return this.config.enabled;
  }

  /**
   * Enable or disable sandbox mode
   */
  setEnabled(enabled: boolean): void {
    this.config.enabled = enabled;
  }

  /**
   * Simulate label purchase without real cost
   */
  async createMockLabel(request: ShipmentRequest): Promise<Label> {
    if (!this.config.enabled) {
      throw new Error('Sandbox mode is not enabled');
    }

    const trackingNumber = this.generateMockTrackingNumber(request.carrier || 'MOCK');
    const label: Label = {
      id: `sandbox_label_${Date.now()}`,
      trackingNumber,
      carrier: request.carrier || 'SANDBOX_CARRIER',
      service: request.service || 'STANDARD',
      rate: this.calculateMockRate(request),
      labelUrl: `https://sandbox.eship.com/labels/${trackingNumber}.pdf`,
      createdAt: new Date(),
      isSandbox: true
    };

    this.mockLabels.set(trackingNumber, label);

    // Auto-generate initial tracking events if enabled
    if (this.config.autoGenerateTracking) {
      this.generateInitialTrackingEvents(trackingNumber);
    }

    return label;
  }

  /**
   * Simulate tracking events
   */
  async simulateTrackingEvent(trackingNumber: string, status: string, location: string): Promise<TrackingEvent> {
    if (!this.config.enabled) {
      throw new Error('Sandbox mode is not enabled');
    }

    const event: TrackingEvent = {
      id: `sandbox_event_${Date.now()}`,
      trackingNumber,
      status,
      location,
      timestamp: new Date(),
      description: `Package ${status.toLowerCase()} at ${location}`,
      isSandbox: true
    };

    const events = this.mockTrackingEvents.get(trackingNumber) || [];
    events.push(event);
    this.mockTrackingEvents.set(trackingNumber, events);

    return event;
  }

  /**
   * Get all tracking events for a tracking number
   */
  getTrackingEvents(trackingNumber: string): TrackingEvent[] {
    return this.mockTrackingEvents.get(trackingNumber) || [];
  }

  /**
   * Get a mock label by tracking number
   */
  getLabel(trackingNumber: string): Label | undefined {
    return this.mockLabels.get(trackingNumber);
  }

  /**
   * Simulate delivery with automated events
   */
  async simulateDelivery(trackingNumber: string): Promise<void> {
    if (!this.config.enabled) {
      throw new Error('Sandbox mode is not enabled');
    }

    const stages = [
      { status: 'IN_TRANSIT', location: 'Origin Facility' },
      { status: 'IN_TRANSIT', location: 'Regional Hub' },
      { status: 'OUT_FOR_DELIVERY', location: 'Local Facility' },
      { status: 'DELIVERED', location: 'Customer Address' }
    ];

    for (const stage of stages) {
      await new Promise(resolve => setTimeout(resolve, this.config.simulateDeliveryDelay));
      await this.simulateTrackingEvent(trackingNumber, stage.status, stage.location);
    }
  }

  /**
   * Generate a mock tracking number
   */
  private generateMockTrackingNumber(carrier: string): string {
    const prefix = carrier.substring(0, 3).toUpperCase();
    const timestamp = Date.now().toString().slice(-8);
    const random = Math.random().toString(36).substring(2, 6).toUpperCase();
    return `${prefix}${timestamp}${random}`;
  }

  /**
   * Calculate mock rate for testing
   */
  private calculateMockRate(request: ShipmentRequest): number {
    const baseRate = 5.00;
    const weightCost = request.weight * 0.5;
    const volumeCost = (request.dimensions.length * request.dimensions.width * request.dimensions.height) * 0.01;
    return baseRate + weightCost + volumeCost;
  }

  /**
   * Generate initial tracking events when label is created
   */
  private generateInitialTrackingEvents(trackingNumber: string): void {
    const initialEvent: TrackingEvent = {
      id: `sandbox_event_${Date.now()}`,
      trackingNumber,
      status: 'LABEL_CREATED',
      location: 'Origin',
      timestamp: new Date(),
      description: 'Shipping label created',
      isSandbox: true
    };

    this.mockTrackingEvents.set(trackingNumber, [initialEvent]);
  }

  /**
   * Clear all sandbox data
   */
  clearAll(): void {
    this.mockLabels.clear();
    this.mockTrackingEvents.clear();
  }

  /**
   * Get all sandbox labels
   */
  getAllLabels(): Label[] {
    return Array.from(this.mockLabels.values());
  }
}
