/**
 * Tests for Cartonization Service
 */

import { CartonizationService } from '../services/CartonizationService';
import { ShipmentItem } from '../../common/interfaces';

describe('CartonizationService', () => {
  let service: CartonizationService;

  beforeEach(() => {
    service = new CartonizationService();
  });

  const sampleItem: ShipmentItem = {
    sku: 'ITEM-001',
    name: 'Test Item',
    quantity: 1,
    weight: { value: 2, unit: 'lb' },
    dimensions: { length: 5, width: 4, height: 3, unit: 'in' },
    value: 25,
  };

  describe('optimizePackaging', () => {
    it('should pack single item into appropriate box', () => {
      const result = service.optimizePackaging([sampleItem]);

      expect(result.packages.length).toBeGreaterThan(0);
      expect(result.totalWeight).toBeGreaterThan(0);
      expect(result.efficiency).toBeGreaterThan(0);
    });

    it('should pack multiple items efficiently', () => {
      const items = [
        { ...sampleItem, sku: 'ITEM-001' },
        { ...sampleItem, sku: 'ITEM-002' },
        { ...sampleItem, sku: 'ITEM-003' },
      ];

      const result = service.optimizePackaging(items);

      expect(result.packages.length).toBeGreaterThan(0);
      expect(result.efficiency).toBeLessThanOrEqual(100);
    });

    it('should calculate total weight correctly', () => {
      const items = [
        { ...sampleItem, weight: { value: 5, unit: 'lb' as const } },
        { ...sampleItem, sku: 'ITEM-002', weight: { value: 3, unit: 'lb' as const } },
      ];

      const result = service.optimizePackaging(items);

      expect(result.totalWeight).toBeGreaterThanOrEqual(8);
    });
  });

  describe('calculateDimensionalWeight', () => {
    it('should calculate dimensional weight for USPS', () => {
      const dimensions = { length: 12, width: 10, height: 8, unit: 'in' as const };
      const dimWeight = service.calculateDimensionalWeight(dimensions, 'USPS');

      expect(dimWeight).toBeGreaterThan(0);
      expect(dimWeight).toBe(960 / 166);
    });

    it('should calculate dimensional weight for UPS', () => {
      const dimensions = { length: 12, width: 10, height: 8, unit: 'in' as const };
      const dimWeight = service.calculateDimensionalWeight(dimensions, 'UPS');

      expect(dimWeight).toBeGreaterThan(0);
      expect(dimWeight).toBe(960 / 139);
    });
  });

  describe('getBillableWeight', () => {
    it('should return actual weight when greater than dimensional', () => {
      const pkg = {
        type: 'box' as const,
        dimensions: { length: 6, width: 4, height: 4, unit: 'in' as const },
        weight: { value: 10, unit: 'lb' as const },
      };

      const billable = service.getBillableWeight(pkg, 'USPS');

      expect(billable).toBe(10);
    });

    it('should return dimensional weight when greater than actual', () => {
      const pkg = {
        type: 'box' as const,
        dimensions: { length: 20, width: 16, height: 12, unit: 'in' as const },
        weight: { value: 5, unit: 'lb' as const },
      };

      const billable = service.getBillableWeight(pkg, 'USPS');
      const expectedDimWeight = (20 * 16 * 12) / 166;

      expect(billable).toBe(expectedDimWeight);
    });
  });

  describe('selectOptimalBox', () => {
    it('should select appropriate box size for items', () => {
      const items = [sampleItem];
      const box = service.selectOptimalBox(items);

      expect(box).toHaveProperty('length');
      expect(box).toHaveProperty('width');
      expect(box).toHaveProperty('height');
      expect(box.unit).toBe('in');
    });

    it('should select larger box for multiple items', () => {
      const items = Array(5).fill(sampleItem).map((item, i) => ({
        ...item,
        sku: `ITEM-${i}`,
      }));

      const box = service.selectOptimalBox(items);

      expect(box.length).toBeGreaterThan(0);
      expect(box.width).toBeGreaterThan(0);
      expect(box.height).toBeGreaterThan(0);
    });
  });
});
