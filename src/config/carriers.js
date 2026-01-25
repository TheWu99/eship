// Carrier configuration
module.exports = {
  ups: {
    name: 'UPS',
    apiKey: process.env.UPS_API_KEY,
    apiSecret: process.env.UPS_API_SECRET,
    accountNumber: process.env.UPS_ACCOUNT_NUMBER,
    endpoint: 'https://onlinetools.ups.com/api',
    services: ['Ground', 'Next Day Air', '2nd Day Air', 'Worldwide Express']
  },
  fedex: {
    name: 'FedEx',
    apiKey: process.env.FEDEX_API_KEY,
    apiSecret: process.env.FEDEX_API_SECRET,
    accountNumber: process.env.FEDEX_ACCOUNT_NUMBER,
    endpoint: 'https://apis.fedex.com',
    services: ['Ground', 'Express Saver', 'Priority Overnight', 'International Priority']
  },
  dhl: {
    name: 'DHL',
    apiKey: process.env.DHL_API_KEY,
    apiSecret: process.env.DHL_API_SECRET,
    accountNumber: process.env.DHL_ACCOUNT_NUMBER,
    endpoint: 'https://api.dhl.com',
    services: ['Express Worldwide', 'Economy Select', 'Domestic Express']
  }
};
