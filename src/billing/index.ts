/**
 * Consolidated Billing Module
 * Provides one master account to pay for all postage across different carriers
 * with audit-ready reconciliation
 */

import { BillingTransaction } from '../types';

export interface BillingAccount {
  id: string;
  name: string;
  balance: number;
  currency: string;
  createdAt: Date;
}

export interface ReconciliationReport {
  accountId: string;
  period: {
    start: Date;
    end: Date;
  };
  transactions: BillingTransaction[];
  summary: {
    totalTransactions: number;
    totalAmount: number;
    byCarrier: Map<string, number>;
    byDate: Map<string, number>;
  };
}

export class ConsolidatedBilling {
  private masterAccount: BillingAccount;
  private transactions: BillingTransaction[];

  constructor(accountName: string = 'Master Shipping Account') {
    this.masterAccount = {
      id: `account_${Date.now()}`,
      name: accountName,
      balance: 0,
      currency: 'USD',
      createdAt: new Date()
    };
    this.transactions = [];
  }

  /**
   * Get the master account details
   */
  getMasterAccount(): BillingAccount {
    return { ...this.masterAccount };
  }

  /**
   * Add funds to the master account
   */
  addFunds(amount: number): void {
    if (amount <= 0) {
      throw new Error('Amount must be positive');
    }
    this.masterAccount.balance += amount;
  }

  /**
   * Process a shipping charge from any carrier
   */
  processCharge(carrier: string, amount: number, labelId: string, description: string = ''): BillingTransaction {
    if (amount <= 0) {
      throw new Error('Amount must be positive');
    }

    if (this.masterAccount.balance < amount) {
      throw new Error('Insufficient funds in master account');
    }

    const transaction: BillingTransaction = {
      id: `txn_${Date.now()}_${Math.random().toString(36).substring(2, 9)}`,
      accountId: this.masterAccount.id,
      carrier,
      amount,
      currency: this.masterAccount.currency,
      labelId,
      timestamp: new Date(),
      description: description || `Shipping charge for ${carrier}`
    };

    this.transactions.push(transaction);
    this.masterAccount.balance -= amount;

    return transaction;
  }

  /**
   * Get all transactions
   */
  getAllTransactions(): BillingTransaction[] {
    return [...this.transactions];
  }

  /**
   * Get transactions for a specific carrier
   */
  getTransactionsByCarrier(carrier: string): BillingTransaction[] {
    return this.transactions.filter(txn => txn.carrier === carrier);
  }

  /**
   * Get transactions within a date range
   */
  getTransactionsByDateRange(startDate: Date, endDate: Date): BillingTransaction[] {
    return this.transactions.filter(
      txn => txn.timestamp >= startDate && txn.timestamp <= endDate
    );
  }

  /**
   * Generate a reconciliation report for a given period
   */
  generateReconciliationReport(startDate: Date, endDate: Date): ReconciliationReport {
    const periodTransactions = this.getTransactionsByDateRange(startDate, endDate);
    
    const byCarrier = new Map<string, number>();
    const byDate = new Map<string, number>();
    let totalAmount = 0;

    for (const txn of periodTransactions) {
      totalAmount += txn.amount;
      
      // Aggregate by carrier
      const carrierTotal = byCarrier.get(txn.carrier) || 0;
      byCarrier.set(txn.carrier, carrierTotal + txn.amount);
      
      // Aggregate by date
      const dateKey = txn.timestamp.toISOString().split('T')[0];
      if (dateKey !== undefined) {
        const dateTotal = byDate.get(dateKey) || 0;
        byDate.set(dateKey, dateTotal + txn.amount);
      }
    }

    return {
      accountId: this.masterAccount.id,
      period: {
        start: startDate,
        end: endDate
      },
      transactions: periodTransactions,
      summary: {
        totalTransactions: periodTransactions.length,
        totalAmount,
        byCarrier,
        byDate
      }
    };
  }

  /**
   * Get current account balance
   */
  getBalance(): number {
    return this.masterAccount.balance;
  }

  /**
   * Get total spent across all carriers
   */
  getTotalSpent(): number {
    return this.transactions.reduce((total, txn) => total + txn.amount, 0);
  }

  /**
   * Get spending by carrier
   */
  getSpendingByCarrier(): Map<string, number> {
    const spending = new Map<string, number>();
    
    for (const txn of this.transactions) {
      const current = spending.get(txn.carrier) || 0;
      spending.set(txn.carrier, current + txn.amount);
    }
    
    return spending;
  }

  /**
   * Export reconciliation report to CSV format
   */
  exportReportToCSV(report: ReconciliationReport): string {
    const lines: string[] = [
      'Transaction ID,Date,Carrier,Label ID,Amount,Description',
      ...report.transactions.map(txn => 
        `${txn.id},${txn.timestamp.toISOString()},${txn.carrier},${txn.labelId},${txn.amount},"${txn.description}"`
      ),
      '',
      'Summary',
      `Total Transactions,${report.summary.totalTransactions}`,
      `Total Amount,${report.summary.totalAmount}`,
      '',
      'By Carrier'
    ];

    report.summary.byCarrier.forEach((amount, carrier) => {
      lines.push(`${carrier},${amount}`);
    });

    return lines.join('\n');
  }

  /**
   * Get transaction by ID
   */
  getTransactionById(id: string): BillingTransaction | undefined {
    return this.transactions.find(txn => txn.id === id);
  }
}
