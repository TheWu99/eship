/**
 * Cartonization and packing optimization service
 */

import { Package, ShipmentItem, Dimensions, Weight } from '../../common/interfaces';
import { STANDARD_BOX_SIZES, DIMENSIONAL_WEIGHT_DIVISORS } from '../../common/constants';
import { CarrierType, PackageType, DimensionUnit, WeightUnit } from '../../common/types';

export interface PackingResult {
  packages: Package[];
  totalCost: number;
  efficiency: number; // 0-100
  totalVolume: number;
  totalWeight: number;
}

export class CartonizationService {
  /**
   * Calculate optimal box configuration for multi-item orders
   */
  optimizePackaging(items: ShipmentItem[], carrier?: CarrierType): PackingResult {
    // Sort items by volume (largest first)
    const sortedItems = [...items].sort((a, b) => {
      const volA = this.calculateItemVolume(a);
      const volB = this.calculateItemVolume(b);
      return volB - volA;
    });

    const packages: Package[] = [];
    const remainingItems = [...sortedItems];

    // Use bin packing algorithm (First Fit Decreasing)
    while (remainingItems.length > 0) {
      const result = this.packItemsIntoBox(remainingItems);
      packages.push(result.package);
      
      // Remove packed items
      result.packedItems.forEach(item => {
        const index = remainingItems.findIndex(i => i.sku === item.sku);
        if (index !== -1) {
          remainingItems.splice(index, 1);
        }
      });
    }

    // Calculate metrics
    const totalVolume = packages.reduce((sum, pkg) => 
      sum + this.calculatePackageVolume(pkg), 0
    );
    const totalWeight = packages.reduce((sum, pkg) => 
      sum + pkg.weight.value, 0
    );
    const itemsVolume = items.reduce((sum, item) => 
      sum + this.calculateItemVolume(item), 0
    );
    const efficiency = (itemsVolume / totalVolume) * 100;

    return {
      packages,
      totalCost: this.estimatePackagingCost(packages),
      efficiency,
      totalVolume,
      totalWeight,
    };
  }

  /**
   * Calculate dimensional weight for a package
   */
  calculateDimensionalWeight(
    dimensions: Dimensions,
    carrier: CarrierType
  ): number {
    const divisor = (DIMENSIONAL_WEIGHT_DIVISORS as Record<string, number>)[carrier] || 166;
    const volume =
      dimensions.length * dimensions.width * dimensions.height;
    return volume / divisor;
  }

  /**
   * Determine billable weight (actual vs dimensional)
   */
  getBillableWeight(pkg: Package, carrier: CarrierType): number {
    const actualWeight = pkg.weight.value;
    const dimWeight = this.calculateDimensionalWeight(pkg.dimensions, carrier);
    return Math.max(actualWeight, dimWeight);
  }

  /**
   * Select optimal box size from standard sizes
   */
  selectOptimalBox(items: ShipmentItem[]): Dimensions {
    const totalVolume = items.reduce(
      (sum, item) => sum + this.calculateItemVolume(item),
      0
    );

    // Add 20% buffer for packing material
    const requiredVolume = totalVolume * 1.2;

    // Find smallest box that fits
    for (const box of STANDARD_BOX_SIZES) {
      const boxVolume = box.length * box.width * box.height;
      if (boxVolume >= requiredVolume) {
        return {
          length: box.length,
          width: box.width,
          height: box.height,
          unit: 'in' as DimensionUnit,
        };
      }
    }

    // If no standard box fits, use custom size
    const lastBox = STANDARD_BOX_SIZES[STANDARD_BOX_SIZES.length - 1];
    return {
      length: lastBox.length,
      width: lastBox.width,
      height: lastBox.height,
      unit: 'in' as DimensionUnit,
    };
  }

  /**
   * Pack items into a single box using 3D bin packing algorithm
   */
  private packItemsIntoBox(items: ShipmentItem[]): {
    package: Package;
    packedItems: ShipmentItem[];
  } {
    const packedItems: ShipmentItem[] = [];
    let remainingCapacity = STANDARD_BOX_SIZES[0].length *
      STANDARD_BOX_SIZES[0].width *
      STANDARD_BOX_SIZES[0].height;

    // Try to fit items into smallest box
    for (let i = 0; i < items.length; i++) {
      const itemVolume = this.calculateItemVolume(items[i]);
      if (itemVolume <= remainingCapacity) {
        packedItems.push(items[i]);
        remainingCapacity -= itemVolume;
      }
    }

    // If no items fit, take at least one
    if (packedItems.length === 0 && items.length > 0) {
      packedItems.push(items[0]);
    }

    const dimensions = this.selectOptimalBox(packedItems);
    const weight = this.calculateTotalWeight(packedItems);

    return {
      package: {
        type: 'box' as PackageType,
        dimensions,
        weight: {
          value: weight,
          unit: 'lb' as WeightUnit,
        },
        items: packedItems,
      },
      packedItems,
    };
  }

  private calculateItemVolume(item: ShipmentItem): number {
    if (!item.dimensions) {
      // Estimate based on weight if dimensions not provided
      return item.weight.value * 10; // rough estimate
    }
    return (
      item.dimensions.length *
      item.dimensions.width *
      item.dimensions.height
    );
  }

  private calculatePackageVolume(pkg: Package): number {
    return (
      pkg.dimensions.length *
      pkg.dimensions.width *
      pkg.dimensions.height
    );
  }

  private calculateTotalWeight(items: ShipmentItem[]): number {
    return items.reduce((sum, item) => sum + item.weight.value * item.quantity, 0);
  }

  private estimatePackagingCost(packages: Package[]): number {
    // Simple cost estimation based on box size
    return packages.reduce((sum, pkg) => {
      const volume = this.calculatePackageVolume(pkg);
      if (volume <= 192) return sum + 0.5; // Small
      if (volume <= 720) return sum + 1.0; // Medium
      if (volume <= 1536) return sum + 2.0; // Large
      return sum + 3.0; // Extra large
    }, 0);
  }
}
