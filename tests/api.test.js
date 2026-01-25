const request = require('supertest');
const app = require('../src/server');

describe('eShip API Tests', () => {
  
  describe('GET /api/health', () => {
    it('should return health status', async () => {
      const res = await request(app)
        .get('/api/health')
        .expect(200);
      
      expect(res.body.status).toBe('ok');
      expect(res.body.message).toBe('eShip API is running');
    });
  });

  describe('GET /api/shipping/carriers', () => {
    it('should return list of available carriers', async () => {
      const res = await request(app)
        .get('/api/shipping/carriers')
        .expect(200);
      
      expect(res.body.success).toBe(true);
      expect(res.body.data).toBeInstanceOf(Array);
      expect(res.body.data.length).toBe(3);
      
      const carrierIds = res.body.data.map(c => c.id);
      expect(carrierIds).toContain('ups');
      expect(carrierIds).toContain('fedex');
      expect(carrierIds).toContain('dhl');
    });
  });

  describe('POST /api/shipping/rates', () => {
    it('should return rates from all carriers', async () => {
      const shipment = {
        origin: {
          city: 'New York',
          state: 'NY',
          postalCode: '10001',
          country: 'US'
        },
        destination: {
          city: 'Los Angeles',
          state: 'CA',
          postalCode: '90001',
          country: 'US'
        },
        weight: 5
      };

      const res = await request(app)
        .post('/api/shipping/rates')
        .send({ shipment })
        .expect(200);
      
      expect(res.body.success).toBe(true);
      expect(res.body.data).toBeInstanceOf(Array);
      expect(res.body.data.length).toBe(3);
    });

    it('should return rates from specific carrier', async () => {
      const shipment = {
        origin: {
          city: 'New York',
          state: 'NY',
          postalCode: '10001',
          country: 'US'
        },
        destination: {
          city: 'Los Angeles',
          state: 'CA',
          postalCode: '90001',
          country: 'US'
        },
        weight: 5
      };

      const res = await request(app)
        .post('/api/shipping/rates')
        .send({ shipment, carrier: 'ups' })
        .expect(200);
      
      expect(res.body.success).toBe(true);
      expect(res.body.data.carrier).toBe('UPS');
      expect(res.body.data.services).toBeInstanceOf(Array);
    });
  });

  describe('POST /api/shipping/labels', () => {
    it('should create a shipping label', async () => {
      const shipment = {
        origin: {
          street: '123 Main St',
          city: 'New York',
          state: 'NY',
          postalCode: '10001',
          country: 'US'
        },
        destination: {
          street: '456 Oak Ave',
          city: 'Los Angeles',
          state: 'CA',
          postalCode: '90001',
          country: 'US'
        },
        weight: 5
      };

      const res = await request(app)
        .post('/api/shipping/labels')
        .send({ shipment, carrier: 'ups' })
        .expect(200);
      
      expect(res.body.success).toBe(true);
      expect(res.body.data.carrier).toBe('UPS');
      expect(res.body.data.trackingNumber).toBeDefined();
      expect(res.body.data.labelUrl).toBeDefined();
    });

    it('should return error when carrier is missing', async () => {
      const shipment = {
        origin: { city: 'New York', state: 'NY', postalCode: '10001' },
        destination: { city: 'Los Angeles', state: 'CA', postalCode: '90001' },
        weight: 5
      };

      const res = await request(app)
        .post('/api/shipping/labels')
        .send({ shipment })
        .expect(400);
      
      expect(res.body.success).toBe(false);
      expect(res.body.error).toContain('Carrier is required');
    });
  });

  describe('GET /api/shipping/tracking/:carrier/:trackingNumber', () => {
    it('should return tracking information', async () => {
      const res = await request(app)
        .get('/api/shipping/tracking/ups/1Z999AA10123456784')
        .expect(200);
      
      expect(res.body.success).toBe(true);
      expect(res.body.data.carrier).toBe('UPS');
      expect(res.body.data.trackingNumber).toBe('1Z999AA10123456784');
      expect(res.body.data.status).toBeDefined();
      expect(res.body.data.events).toBeInstanceOf(Array);
    });
  });

  describe('POST /api/shipping/validate-address', () => {
    it('should validate an address', async () => {
      const address = {
        street: '123 Main St',
        city: 'New York',
        state: 'NY',
        postalCode: '10001',
        country: 'US'
      };

      const res = await request(app)
        .post('/api/shipping/validate-address')
        .send({ address, carrier: 'ups' })
        .expect(200);
      
      expect(res.body.success).toBe(true);
      expect(res.body.data.valid).toBeDefined();
      expect(res.body.data.validatedAddress).toBeDefined();
    });
  });

  describe('POST /api/shipping/pickup', () => {
    it('should schedule a pickup', async () => {
      const pickupDetails = {
        date: '2026-01-30',
        timeWindow: '9:00 AM - 5:00 PM',
        address: {
          street: '123 Main St',
          city: 'New York',
          state: 'NY',
          postalCode: '10001'
        }
      };

      const res = await request(app)
        .post('/api/shipping/pickup')
        .send({ pickupDetails, carrier: 'ups' })
        .expect(200);
      
      expect(res.body.success).toBe(true);
      expect(res.body.data.confirmationNumber).toBeDefined();
      expect(res.body.data.status).toBe('Confirmed');
    });
  });

  describe('POST /api/shipping/customs', () => {
    it('should create customs documentation', async () => {
      const customsData = {
        type: 'Commercial Invoice',
        items: [
          {
            description: 'Electronics',
            quantity: 2,
            value: 150.00
          }
        ]
      };

      const res = await request(app)
        .post('/api/shipping/customs')
        .send({ customsData, carrier: 'ups' })
        .expect(200);
      
      expect(res.body.success).toBe(true);
      expect(res.body.data.documentId).toBeDefined();
      expect(res.body.data.documentUrl).toBeDefined();
    });
  });
});
