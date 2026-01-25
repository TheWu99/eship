/**
 * Tests for Rate Shopping Service
 */

import { RateShoppingService } from '../services/RateShoppingService';
import { RateSelectionEngine, BusinessRule } from '../services/RateSelectionEngine';
import { Rate, Address, Package } from '../../common/interfaces';

describe('RateShoppingService', () => {
  let service: RateShoppingService;

  beforeEach(() => {
    service = new RateShoppingService();
  });

  const sampleAddress: Address = {
    name: 'John Doe',
    street1: '123 Main St',
    city: 'New York',
    state: 'NY',
    postalCode: '10001',
    country: 'US',
  };

  const samplePackage: Package = {
    type: 'box',
    dimensions: { length: 10, width: 8, height: 6, unit: 'in' },
    weight: { value: 5, unit: 'lb' },
  };

  describe('fetchRates', () => {
    it('should fetch rates from multiple carriers', async () => {
      const request = {
        from: sampleAddress,
        to: sampleAddress,
        packages: [samplePackage],
      };

      const rates = await service.fetchRates(request);

      expect(rates.length).toBeGreaterThan(0);
      expect(rates[0]).toHaveProperty('carrier');
      expect(rates[0]).toHaveProperty('cost');
      expect(rates[0]).toHaveProperty('estimatedDays');
    });

    it('should filter carriers based on includeCarriers', async () => {
      const request = {
        from: sampleAddress,
        to: sampleAddress,
        packages: [samplePackage],
        includeCarriers: ['USPS' as const],
      };

      const rates = await service.fetchRates(request);

      rates.forEach(rate => {
        expect(rate.carrier).toBe('USPS');
      });
    });

    it('should exclude carriers based on excludeCarriers', async () => {
      const request = {
        from: sampleAddress,
        to: sampleAddress,
        packages: [samplePackage],
        excludeCarriers: ['USPS' as const, 'UPS' as const],
      };

      const rates = await service.fetchRates(request);

      rates.forEach(rate => {
        expect(rate.carrier).not.toBe('USPS');
        expect(rate.carrier).not.toBe('UPS');
      });
    });
  });

  describe('getBestRate', () => {
    it('should return cheapest rate when criteria is cheapest', async () => {
      const request = {
        from: sampleAddress,
        to: sampleAddress,
        packages: [samplePackage],
      };

      const rule: BusinessRule = {
        criteria: 'cheapest',
      };

      const bestRate = await service.getBestRate(request, rule);

      expect(bestRate).toBeDefined();
      expect(bestRate).toHaveProperty('cost');
    });

    it('should return fastest rate when criteria is fastest', async () => {
      const request = {
        from: sampleAddress,
        to: sampleAddress,
        packages: [samplePackage],
      };

      const rule: BusinessRule = {
        criteria: 'fastest',
      };

      const bestRate = await service.getBestRate(request, rule);

      expect(bestRate).toBeDefined();
      expect(bestRate).toHaveProperty('estimatedDays');
    });
  });
});

describe('RateSelectionEngine', () => {
  const sampleRates: Rate[] = [
    {
      carrier: 'USPS',
      service: 'ground',
      cost: 10,
      currency: 'USD',
      estimatedDays: 5,
      reliability: 85,
      trackingAvailable: true,
      insuranceAvailable: true,
    },
    {
      carrier: 'UPS',
      service: 'express',
      cost: 25,
      currency: 'USD',
      estimatedDays: 2,
      reliability: 92,
      trackingAvailable: true,
      insuranceAvailable: true,
    },
    {
      carrier: 'FedEx',
      service: 'overnight',
      cost: 40,
      currency: 'USD',
      estimatedDays: 1,
      reliability: 95,
      trackingAvailable: true,
      insuranceAvailable: true,
    },
  ];

  describe('selectBestRate', () => {
    it('should select cheapest rate', () => {
      const rule: BusinessRule = { criteria: 'cheapest' };
      const best = RateSelectionEngine.selectBestRate(sampleRates, rule);

      expect(best?.carrier).toBe('USPS');
      expect(best?.cost).toBe(10);
    });

    it('should select fastest rate', () => {
      const rule: BusinessRule = { criteria: 'fastest' };
      const best = RateSelectionEngine.selectBestRate(sampleRates, rule);

      expect(best?.carrier).toBe('FedEx');
      expect(best?.estimatedDays).toBe(1);
    });

    it('should select most reliable rate', () => {
      const rule: BusinessRule = { criteria: 'most_reliable' };
      const best = RateSelectionEngine.selectBestRate(sampleRates, rule);

      expect(best?.carrier).toBe('FedEx');
      expect(best?.reliability).toBe(95);
    });

    it('should exclude carriers based on rule', () => {
      const rule: BusinessRule = {
        criteria: 'cheapest',
        excludeCarriers: ['USPS'],
      };
      const best = RateSelectionEngine.selectBestRate(sampleRates, rule);

      expect(best?.carrier).not.toBe('USPS');
    });

    it('should apply max cost constraint', () => {
      const rule: BusinessRule = {
        criteria: 'cheapest',
        maxCost: 20,
      };
      const best = RateSelectionEngine.selectBestRate(sampleRates, rule);

      expect(best?.cost).toBeLessThanOrEqual(20);
    });

    it('should return null when no rates match constraints', () => {
      const rule: BusinessRule = {
        criteria: 'cheapest',
        maxCost: 5,
      };
      const best = RateSelectionEngine.selectBestRate(sampleRates, rule);

      expect(best).toBeNull();
    });
  });
});
