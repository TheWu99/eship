/**
 * Business rules for carrier selection
 */

import { Rate } from '../../common/interfaces';
import { CarrierType } from '../../common/types';

export type SelectionCriteria = 'cheapest' | 'fastest' | 'most_reliable' | 'balanced' | 'custom';

export interface BusinessRule {
  criteria: SelectionCriteria;
  weightCost?: number; // 0-1
  weightSpeed?: number; // 0-1
  weightReliability?: number; // 0-1
  excludeCarriers?: CarrierType[];
  preferredCarriers?: CarrierType[];
  maxCost?: number;
  maxDays?: number;
  minReliability?: number;
}

export class RateSelectionEngine {
  /**
   * Select the best rate based on business rules
   */
  static selectBestRate(rates: Rate[], rule: BusinessRule): Rate | null {
    if (!rates || rates.length === 0) {
      return null;
    }

    // Filter out excluded carriers
    let filteredRates = rates;
    if (rule.excludeCarriers && rule.excludeCarriers.length > 0) {
      filteredRates = filteredRates.filter(
        (rate) => !rule.excludeCarriers!.includes(rate.carrier)
      );
    }

    // Apply constraints
    if (rule.maxCost) {
      filteredRates = filteredRates.filter((rate) => rate.cost <= rule.maxCost!);
    }
    if (rule.maxDays) {
      filteredRates = filteredRates.filter((rate) => rate.estimatedDays <= rule.maxDays!);
    }
    if (rule.minReliability) {
      filteredRates = filteredRates.filter(
        (rate) => (rate.reliability || 0) >= rule.minReliability!
      );
    }

    if (filteredRates.length === 0) {
      return null;
    }

    // Apply selection criteria
    switch (rule.criteria) {
      case 'cheapest':
        return this.selectCheapest(filteredRates);
      case 'fastest':
        return this.selectFastest(filteredRates);
      case 'most_reliable':
        return this.selectMostReliable(filteredRates);
      case 'balanced':
        return this.selectBalanced(filteredRates);
      case 'custom':
        return this.selectCustom(filteredRates, rule);
      default:
        return this.selectCheapest(filteredRates);
    }
  }

  private static selectCheapest(rates: Rate[]): Rate {
    return rates.reduce((min, rate) => (rate.cost < min.cost ? rate : min));
  }

  private static selectFastest(rates: Rate[]): Rate {
    return rates.reduce((min, rate) =>
      rate.estimatedDays < min.estimatedDays ? rate : min
    );
  }

  private static selectMostReliable(rates: Rate[]): Rate {
    return rates.reduce((max, rate) =>
      (rate.reliability || 0) > (max.reliability || 0) ? rate : max
    );
  }

  private static selectBalanced(rates: Rate[]): Rate {
    // Normalize and score each rate
    const costs = rates.map((r) => r.cost);
    const days = rates.map((r) => r.estimatedDays);
    const reliabilities = rates.map((r) => r.reliability || 0);

    const minCost = Math.min(...costs);
    const maxCost = Math.max(...costs);
    const minDays = Math.min(...days);
    const maxDays = Math.max(...days);
    const minReliability = Math.min(...reliabilities);
    const maxReliability = Math.max(...reliabilities);

    const scored = rates.map((rate) => {
      const costScore =
        maxCost === minCost ? 0 : (maxCost - rate.cost) / (maxCost - minCost);
      const speedScore =
        maxDays === minDays
          ? 0
          : (maxDays - rate.estimatedDays) / (maxDays - minDays);
      const reliabilityScore =
        maxReliability === minReliability
          ? 0
          : ((rate.reliability || 0) - minReliability) /
            (maxReliability - minReliability);

      const score = (costScore + speedScore + reliabilityScore) / 3;
      return { rate, score };
    });

    return scored.reduce((max, item) => (item.score > max.score ? item : max))
      .rate;
  }

  private static selectCustom(rates: Rate[], rule: BusinessRule): Rate {
    const weightCost = rule.weightCost || 0.33;
    const weightSpeed = rule.weightSpeed || 0.33;
    const weightReliability = rule.weightReliability || 0.34;

    // Normalize and score
    const costs = rates.map((r) => r.cost);
    const days = rates.map((r) => r.estimatedDays);
    const reliabilities = rates.map((r) => r.reliability || 0);

    const minCost = Math.min(...costs);
    const maxCost = Math.max(...costs);
    const minDays = Math.min(...days);
    const maxDays = Math.max(...days);
    const minReliability = Math.min(...reliabilities);
    const maxReliability = Math.max(...reliabilities);

    const scored = rates.map((rate) => {
      const costScore =
        maxCost === minCost ? 0 : (maxCost - rate.cost) / (maxCost - minCost);
      const speedScore =
        maxDays === minDays
          ? 0
          : (maxDays - rate.estimatedDays) / (maxDays - minDays);
      const reliabilityScore =
        maxReliability === minReliability
          ? 0
          : ((rate.reliability || 0) - minReliability) /
            (maxReliability - minReliability);

      const score =
        costScore * weightCost +
        speedScore * weightSpeed +
        reliabilityScore * weightReliability;

      // Boost preferred carriers
      const boost =
        rule.preferredCarriers?.includes(rate.carrier) ? 0.1 : 0;

      return { rate, score: score + boost };
    });

    return scored.reduce((max, item) => (item.score > max.score ? item : max))
      .rate;
  }
}
