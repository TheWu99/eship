/**
 * Insurance and Claims Management Service
 */

import { Insurance, Claim, Shipment } from '../../common/interfaces';
import { ClaimStatus, Currency } from '../../common/types';
import { INSURANCE_RATES } from '../../common/constants';

export interface InsuranceOptions {
  shipmentId: string;
  declaredValue: number;
  currency?: Currency;
}

export interface ClaimRequest {
  insuranceId: string;
  shipmentId: string;
  damageDescription: string;
  claimAmount: number;
  supportingDocuments?: string[]; // URLs to photos/documents
}

export class InsuranceService {
  /**
   * Calculate insurance premium based on declared value
   */
  calculatePremium(declaredValue: number): number {
    const premium = declaredValue * INSURANCE_RATES.base;
    return Math.max(premium, INSURANCE_RATES.minimum);
  }

  /**
   * Purchase insurance for a shipment
   */
  async purchaseInsurance(options: InsuranceOptions): Promise<Insurance> {
    const premium = this.calculatePremium(options.declaredValue);

    const insurance: Insurance = {
      id: this.generateId('INS'),
      shipmentId: options.shipmentId,
      carrier: 'USPS', // Would be determined by shipment
      coverage: options.declaredValue,
      premium,
      currency: options.currency || 'USD',
      policyNumber: this.generatePolicyNumber(),
      issuedAt: new Date(),
    };

    // In production, this would call insurance provider API
    return insurance;
  }

  /**
   * Validate insurance eligibility
   */
  validateInsuranceEligibility(shipment: Shipment): {
    eligible: boolean;
    reason?: string;
  } {
    // Check if shipment meets insurance requirements
    if (!shipment.packages || shipment.packages.length === 0) {
      return {
        eligible: false,
        reason: 'Shipment must have at least one package',
      };
    }

    const totalValue = shipment.packages.reduce(
      (sum, pkg) => sum + (pkg.declaredValue || 0),
      0
    );

    if (totalValue > INSURANCE_RATES.maximum) {
      return {
        eligible: false,
        reason: `Declared value exceeds maximum coverage of $${INSURANCE_RATES.maximum}`,
      };
    }

    return { eligible: true };
  }

  private generateId(prefix: string): string {
    return `${prefix}-${Date.now()}-${Math.random().toString(36).slice(2, 11)}`;
  }

  private generatePolicyNumber(): string {
    const timestamp = Date.now().toString();
    const random = Math.random().toString(36).slice(2, 8).toUpperCase();
    return `POL-${timestamp}-${random}`;
  }
}

export class ClaimsService {
  /**
   * File an insurance claim
   */
  async fileClaim(request: ClaimRequest): Promise<Claim> {
    const claim: Claim = {
      id: this.generateId('CLM'),
      insuranceId: request.insuranceId,
      shipmentId: request.shipmentId,
      status: 'pending' as ClaimStatus,
      amount: request.claimAmount,
      description: request.damageDescription,
      filedAt: new Date(),
    };

    // In production, this would submit to insurance provider
    return claim;
  }

  /**
   * Get claim status
   */
  async getClaimStatus(claimId: string): Promise<Claim> {
    // In production, this would fetch from database/API
    throw new Error('Not implemented - would fetch from database');
  }

  /**
   * Process claim automatically based on predefined rules
   */
  async autoProcessClaim(claim: Claim, insurance: Insurance): Promise<Claim> {
    // Auto-approval logic for small claims
    if (claim.amount <= 100 && claim.amount <= insurance.coverage) {
      return {
        ...claim,
        status: 'approved' as ClaimStatus,
        resolvedAt: new Date(),
        resolution: 'Auto-approved based on claim amount and coverage',
      };
    }

    // Claims over threshold require manual review
    return {
      ...claim,
      status: 'in_review' as ClaimStatus,
    };
  }

  /**
   * Update claim status
   */
  async updateClaimStatus(
    claimId: string,
    status: ClaimStatus,
    resolution?: string
  ): Promise<Claim> {
    // In production, this would update database
    const claim: Claim = {
      id: claimId,
      insuranceId: 'INS-123',
      shipmentId: 'SHP-123',
      status,
      amount: 0,
      description: '',
      filedAt: new Date(),
      resolvedAt: status === 'approved' || status === 'denied' ? new Date() : undefined,
      resolution,
    };

    return claim;
  }

  /**
   * Generate claim report
   */
  generateClaimReport(claims: Claim[]): {
    totalClaims: number;
    approvedClaims: number;
    deniedClaims: number;
    pendingClaims: number;
    totalApprovedAmount: number;
  } {
    return {
      totalClaims: claims.length,
      approvedClaims: claims.filter((c) => c.status === 'approved').length,
      deniedClaims: claims.filter((c) => c.status === 'denied').length,
      pendingClaims: claims.filter(
        (c) => c.status === 'pending' || c.status === 'in_review'
      ).length,
      totalApprovedAmount: claims
        .filter((c) => c.status === 'approved')
        .reduce((sum, c) => sum + c.amount, 0),
    };
  }

  private generateId(prefix: string): string {
    return `${prefix}-${Date.now()}-${Math.random().toString(36).slice(2, 11)}`;
  }
}
