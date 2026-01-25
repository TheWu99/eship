const carrierFactory = require('../carriers/CarrierFactory');

class ShippingService {
  // Get rates from one or all carriers
  async getRates(shipment, carrierName = null) {
    if (carrierName) {
      const carrier = carrierFactory.getCarrier(carrierName);
      return await carrier.getRates(shipment);
    } else {
      return await carrierFactory.getRatesFromAllCarriers(shipment);
    }
  }

  // Create shipping label
  async createLabel(shipment, carrierName) {
    const carrier = carrierFactory.getCarrier(carrierName);
    return await carrier.createLabel(shipment);
  }

  // Track package
  async trackPackage(trackingNumber, carrierName) {
    const carrier = carrierFactory.getCarrier(carrierName);
    return await carrier.trackPackage(trackingNumber);
  }

  // Validate address
  async validateAddress(address, carrierName = 'ups') {
    const carrier = carrierFactory.getCarrier(carrierName);
    return await carrier.validateAddress(address);
  }

  // Schedule pickup
  async schedulePickup(pickupDetails, carrierName) {
    const carrier = carrierFactory.getCarrier(carrierName);
    return await carrier.schedulePickup(pickupDetails);
  }

  // Create customs documentation
  async createCustomsDocumentation(customsData, carrierName) {
    const carrier = carrierFactory.getCarrier(carrierName);
    return await carrier.createCustomsDocumentation(customsData);
  }

  // Get available carriers
  getAvailableCarriers() {
    return carrierFactory.getAllCarriers();
  }
}

module.exports = new ShippingService();
