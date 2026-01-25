/**
 * Multi-Warehouse Routing Module
 * Provides logic to route shipments from the fulfillment center closest to the customer
 * to reduce transit time and cost
 */

import { Warehouse, Address, Location, RoutingResult } from '../types';

export class MultiWarehouseRouter {
  private warehouses: Map<string, Warehouse>;

  constructor() {
    this.warehouses = new Map();
  }

  /**
   * Add a warehouse to the routing system
   */
  addWarehouse(warehouse: Warehouse): void {
    this.warehouses.set(warehouse.id, warehouse);
  }

  /**
   * Remove a warehouse from the routing system
   */
  removeWarehouse(warehouseId: string): boolean {
    return this.warehouses.delete(warehouseId);
  }

  /**
   * Get a warehouse by ID
   */
  getWarehouse(warehouseId: string): Warehouse | undefined {
    return this.warehouses.get(warehouseId);
  }

  /**
   * Get all warehouses
   */
  getAllWarehouses(): Warehouse[] {
    return Array.from(this.warehouses.values());
  }

  /**
   * Get all active warehouses
   */
  getActiveWarehouses(): Warehouse[] {
    return this.getAllWarehouses().filter(w => w.isActive);
  }

  /**
   * Find the optimal warehouse for a destination address
   */
  findOptimalWarehouse(destinationAddress: Address): RoutingResult | null {
    const activeWarehouses = this.getActiveWarehouses();
    
    if (activeWarehouses.length === 0) {
      return null;
    }

    // Convert destination address to location (simplified - in production use geocoding API)
    const destinationLocation = this.addressToLocation(destinationAddress);
    
    let closestWarehouse: Warehouse | null = null;
    let minDistance = Infinity;

    for (const warehouse of activeWarehouses) {
      const distance = this.calculateDistance(warehouse.location, destinationLocation);
      
      if (distance < minDistance) {
        minDistance = distance;
        closestWarehouse = warehouse;
      }
    }

    if (!closestWarehouse) {
      return null;
    }

    return {
      warehouseId: closestWarehouse.id,
      warehouse: closestWarehouse,
      distance: minDistance,
      estimatedTransitTime: this.estimateTransitTime(minDistance)
    };
  }

  /**
   * Find multiple warehouse options ranked by distance
   */
  findWarehouseOptions(destinationAddress: Address, limit: number = 3): RoutingResult[] {
    const activeWarehouses = this.getActiveWarehouses();
    
    if (activeWarehouses.length === 0) {
      return [];
    }

    const destinationLocation = this.addressToLocation(destinationAddress);
    
    const results: RoutingResult[] = activeWarehouses.map(warehouse => {
      const distance = this.calculateDistance(warehouse.location, destinationLocation);
      return {
        warehouseId: warehouse.id,
        warehouse,
        distance,
        estimatedTransitTime: this.estimateTransitTime(distance)
      };
    });

    // Sort by distance (ascending) and return top N
    return results
      .sort((a, b) => a.distance - b.distance)
      .slice(0, limit);
  }

  /**
   * Calculate distance between two locations using Haversine formula
   * Returns distance in miles
   */
  private calculateDistance(location1: Location, location2: Location): number {
    const R = 3959; // Earth's radius in miles
    const dLat = this.toRadians(location2.latitude - location1.latitude);
    const dLon = this.toRadians(location2.longitude - location1.longitude);
    
    const a = 
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(this.toRadians(location1.latitude)) * 
      Math.cos(this.toRadians(location2.latitude)) *
      Math.sin(dLon / 2) * Math.sin(dLon / 2);
    
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    const distance = R * c;
    
    return Math.round(distance * 10) / 10; // Round to 1 decimal place
  }

  /**
   * Estimate transit time based on distance
   * Returns estimated days
   */
  private estimateTransitTime(distanceMiles: number): number {
    // Simple estimation: 
    // < 100 miles: 1 day
    // 100-500 miles: 2 days
    // 500-1500 miles: 3 days
    // 1500+ miles: 5 days
    if (distanceMiles < 100) return 1;
    if (distanceMiles < 500) return 2;
    if (distanceMiles < 1500) return 3;
    return 5;
  }

  /**
   * Convert degrees to radians
   */
  private toRadians(degrees: number): number {
    return degrees * (Math.PI / 180);
  }

  /**
   * Convert address to location (simplified geocoding)
   * In production, use a real geocoding API
   */
  private addressToLocation(address: Address): Location {
    // This is a simplified mock implementation
    // In production, integrate with Google Maps API, Mapbox, or similar
    
    // Mock coordinates based on ZIP code (first digit for latitude, second for longitude)
    const zipNumber = parseInt(address.zipCode.substring(0, 2), 10) || 0;
    
    return {
      latitude: 30 + (zipNumber % 20), // Range: 30-50 (approximate US latitude range)
      longitude: -120 + (zipNumber % 50) // Range: -120 to -70 (approximate US longitude range)
    };
  }

  /**
   * Update warehouse active status
   */
  setWarehouseActive(warehouseId: string, isActive: boolean): void {
    const warehouse = this.warehouses.get(warehouseId);
    if (warehouse) {
      warehouse.isActive = isActive;
      this.warehouses.set(warehouseId, warehouse);
    }
  }

  /**
   * Get routing statistics
   */
  getRoutingStats(): {
    totalWarehouses: number;
    activeWarehouses: number;
    inactiveWarehouses: number;
  } {
    const all = this.getAllWarehouses();
    const active = this.getActiveWarehouses();
    
    return {
      totalWarehouses: all.length,
      activeWarehouses: active.length,
      inactiveWarehouses: all.length - active.length
    };
  }
}
