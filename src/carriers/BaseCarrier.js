// Base Carrier Interface
class BaseCarrier {
  constructor(config) {
    this.name = config.name;
    this.apiKey = config.apiKey;
    this.apiSecret = config.apiSecret;
    this.accountNumber = config.accountNumber;
    this.endpoint = config.endpoint;
    this.services = config.services;
  }

  // Abstract methods to be implemented by specific carriers
  async getRates(shipment) {
    throw new Error('getRates must be implemented by carrier');
  }

  async createLabel(shipment) {
    throw new Error('createLabel must be implemented by carrier');
  }

  async trackPackage(trackingNumber) {
    throw new Error('trackPackage must be implemented by carrier');
  }

  async validateAddress(address) {
    throw new Error('validateAddress must be implemented by carrier');
  }

  async schedulePickup(pickupDetails) {
    throw new Error('schedulePickup must be implemented by carrier');
  }

  async createCustomsDocumentation(customsData) {
    throw new Error('createCustomsDocumentation must be implemented by carrier');
  }

  // Helper method to simulate API authentication
  authenticate() {
    // In a real implementation, this would handle OAuth or API key authentication
    return {
      headers: {
        'Authorization': `Bearer ${this.apiKey}`,
        'Content-Type': 'application/json'
      }
    };
  }
}

module.exports = BaseCarrier;
