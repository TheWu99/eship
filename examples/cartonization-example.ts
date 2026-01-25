/**
 * Example: Cartonization and Packing Optimization
 * Demonstrates how to optimize packaging for multi-item orders
 */

import { CartonizationService, ShipmentItem } from '../src';

// Sample items from an order
const orderItems: ShipmentItem[] = [
  {
    sku: 'SHOE-001',
    name: 'Running Shoes - Size 10',
    quantity: 1,
    weight: { value: 1.5, unit: 'lb' },
    dimensions: { length: 13, width: 8, height: 5, unit: 'in' },
    value: 89.99,
    category: 'footwear',
  },
  {
    sku: 'SHIRT-002',
    name: 'Cotton T-Shirt - Medium',
    quantity: 2,
    weight: { value: 0.5, unit: 'lb' },
    dimensions: { length: 12, width: 9, height: 2, unit: 'in' },
    value: 24.99,
    category: 'apparel',
  },
  {
    sku: 'HAT-003',
    name: 'Baseball Cap',
    quantity: 1,
    weight: { value: 0.3, unit: 'lb' },
    dimensions: { length: 8, width: 7, height: 4, unit: 'in' },
    value: 19.99,
    category: 'accessories',
  },
  {
    sku: 'BOOK-004',
    name: 'Programming Guide',
    quantity: 1,
    weight: { value: 2.0, unit: 'lb' },
    dimensions: { length: 9, width: 6, height: 1, unit: 'in' },
    value: 39.99,
    category: 'books',
  },
];

function demonstrateCartonization() {
  const cartonService = new CartonizationService();

  console.log('=== Cartonization & Packing Optimization Demo ===\n');

  // 1. Optimize packaging
  console.log('1. Optimizing package configuration...');
  const packingResult = cartonService.optimizePackaging(orderItems);

  console.log(`\nResults:`);
  console.log(`  Number of boxes: ${packingResult.packages.length}`);
  console.log(`  Total weight: ${packingResult.totalWeight.toFixed(2)} lb`);
  console.log(`  Total volume: ${packingResult.totalVolume.toFixed(2)} cubic inches`);
  console.log(`  Packing efficiency: ${packingResult.efficiency.toFixed(1)}%`);
  console.log(`  Estimated packaging cost: $${packingResult.totalCost.toFixed(2)}`);

  console.log('\n  Package details:');
  packingResult.packages.forEach((pkg, index) => {
    console.log(`\n  Box ${index + 1}:`);
    console.log(
      `    Dimensions: ${pkg.dimensions.length}" x ${pkg.dimensions.width}" x ${pkg.dimensions.height}"`
    );
    console.log(`    Weight: ${pkg.weight.value} lb`);
    console.log(`    Items: ${pkg.items?.length || 0}`);
    pkg.items?.forEach((item) => {
      console.log(`      - ${item.name} (qty: ${item.quantity})`);
    });
  });

  // 2. Calculate dimensional weight for different carriers
  console.log('\n2. Calculating dimensional weight for carriers...');
  const firstPackage = packingResult.packages[0];
  if (firstPackage) {
    console.log('\nDimensional weight comparison:');
    const carriers: Array<'USPS' | 'UPS' | 'FedEx' | 'DHL'> = ['USPS', 'UPS', 'FedEx', 'DHL'];
    carriers.forEach((carrier) => {
      const dimWeight = cartonService.calculateDimensionalWeight(
        firstPackage.dimensions,
        carrier
      );
      const billableWeight = cartonService.getBillableWeight(firstPackage, carrier);
      console.log(
        `  ${carrier}: Dim weight = ${dimWeight.toFixed(2)} lb, Billable = ${billableWeight.toFixed(2)} lb`
      );
    });
  }

  // 3. Demonstrate box selection
  console.log('\n3. Selecting optimal box for specific items...');
  const smallOrder = [orderItems[1], orderItems[2]]; // T-shirt and hat
  const optimalBox = cartonService.selectOptimalBox(smallOrder);
  console.log(
    `Optimal box: ${optimalBox.length}" x ${optimalBox.width}" x ${optimalBox.height}"`
  );

  // 4. Show potential savings
  console.log('\n4. Cost comparison: Optimized vs Individual Boxes');
  const individualBoxCost = orderItems.length * 2.0; // Assume $2 per box
  console.log(`  Individual boxes: $${individualBoxCost.toFixed(2)}`);
  console.log(`  Optimized packing: $${packingResult.totalCost.toFixed(2)}`);
  console.log(
    `  Savings: $${(individualBoxCost - packingResult.totalCost).toFixed(2)} (${(((individualBoxCost - packingResult.totalCost) / individualBoxCost) * 100).toFixed(1)}%)`
  );
}

// Run the demo
demonstrateCartonization();
