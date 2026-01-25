const express = require('express');
const router = express.Router();
const shippingService = require('../services/ShippingService');

// Get available carriers
router.get('/carriers', (req, res) => {
  try {
    const carriers = shippingService.getAvailableCarriers();
    res.json({ success: true, data: carriers });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
});

// Get rates
router.post('/rates', async (req, res) => {
  try {
    const { shipment, carrier } = req.body;
    const rates = await shippingService.getRates(shipment, carrier);
    res.json({ success: true, data: rates });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
});

// Create shipping label
router.post('/labels', async (req, res) => {
  try {
    const { shipment, carrier } = req.body;
    if (!carrier) {
      return res.status(400).json({ success: false, error: 'Carrier is required' });
    }
    const label = await shippingService.createLabel(shipment, carrier);
    res.json({ success: true, data: label });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
});

// Track package
router.get('/tracking/:carrier/:trackingNumber', async (req, res) => {
  try {
    const { carrier, trackingNumber } = req.params;
    const tracking = await shippingService.trackPackage(trackingNumber, carrier);
    res.json({ success: true, data: tracking });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
});

// Validate address
router.post('/validate-address', async (req, res) => {
  try {
    const { address, carrier } = req.body;
    const validation = await shippingService.validateAddress(address, carrier);
    res.json({ success: true, data: validation });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
});

// Schedule pickup
router.post('/pickup', async (req, res) => {
  try {
    const { pickupDetails, carrier } = req.body;
    if (!carrier) {
      return res.status(400).json({ success: false, error: 'Carrier is required' });
    }
    const pickup = await shippingService.schedulePickup(pickupDetails, carrier);
    res.json({ success: true, data: pickup });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
});

// Create customs documentation
router.post('/customs', async (req, res) => {
  try {
    const { customsData, carrier } = req.body;
    if (!carrier) {
      return res.status(400).json({ success: false, error: 'Carrier is required' });
    }
    const customs = await shippingService.createCustomsDocumentation(customsData, carrier);
    res.json({ success: true, data: customs });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
});

module.exports = router;
