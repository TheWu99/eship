const UPSCarrier = require('./UPSCarrier');
const FedExCarrier = require('./FedExCarrier');
const DHLCarrier = require('./DHLCarrier');
const carrierConfigs = require('../config/carriers');

class CarrierFactory {
  constructor() {
    this.carriers = {};
    this.initializeCarriers();
  }

  initializeCarriers() {
    this.carriers.ups = new UPSCarrier(carrierConfigs.ups);
    this.carriers.fedex = new FedExCarrier(carrierConfigs.fedex);
    this.carriers.dhl = new DHLCarrier(carrierConfigs.dhl);
  }

  getCarrier(carrierName) {
    const carrier = this.carriers[carrierName.toLowerCase()];
    if (!carrier) {
      throw new Error(`Carrier ${carrierName} not supported`);
    }
    return carrier;
  }

  getAllCarriers() {
    return Object.keys(this.carriers).map(key => ({
      id: key,
      name: this.carriers[key].name,
      services: this.carriers[key].services
    }));
  }

  async getRatesFromAllCarriers(shipment) {
    const results = await Promise.all(
      Object.values(this.carriers).map(carrier => 
        carrier.getRates(shipment).catch(err => ({
          carrier: carrier.name,
          error: err.message
        }))
      )
    );
    return results;
  }
}

module.exports = new CarrierFactory();
