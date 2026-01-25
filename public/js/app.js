const API_BASE = '/api/shipping';

// Navigation
document.querySelectorAll('.nav-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        const section = btn.dataset.section;
        
        // Update active states
        document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
        
        btn.classList.add('active');
        document.getElementById(section).classList.add('active');
    });
});

// Helper function to get form data as object
function getFormData(form) {
    const formData = new FormData(form);
    const data = {};
    
    for (let [key, value] of formData.entries()) {
        // Handle nested properties like "origin.city"
        const keys = key.split('.');
        let current = data;
        
        for (let i = 0; i < keys.length - 1; i++) {
            // Handle array notation
            const arrayMatch = keys[i].match(/^(.+)\[(\d+)\]$/);
            if (arrayMatch) {
                const arrayName = arrayMatch[1];
                const index = parseInt(arrayMatch[2]);
                if (!current[arrayName]) current[arrayName] = [];
                if (!current[arrayName][index]) current[arrayName][index] = {};
                current = current[arrayName][index];
            } else {
                if (!current[keys[i]]) current[keys[i]] = {};
                current = current[keys[i]];
            }
        }
        
        const lastKey = keys[keys.length - 1];
        const arrayMatch = lastKey.match(/^(.+)\[(\d+)\]$/);
        
        if (arrayMatch) {
            const arrayName = arrayMatch[1];
            const index = parseInt(arrayMatch[2]);
            if (!current[arrayName]) current[arrayName] = [];
            current[arrayName][index] = value;
        } else {
            current[lastKey] = value;
        }
    }
    
    return data;
}

// Helper function to display results
function displayResult(containerId, html) {
    const container = document.getElementById(containerId);
    container.innerHTML = html;
}

// Helper function to show loading
function showLoading(containerId) {
    displayResult(containerId, '<div class="loading">Loading...</div>');
}

// Helper function to show error
function showError(containerId, message) {
    displayResult(containerId, `<div class="error-message">Error: ${message}</div>`);
}

// Rates Form
document.getElementById('ratesForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    showLoading('ratesResult');
    
    const formData = getFormData(e.target);
    const { carrier, ...shipment } = formData;
    
    try {
        const response = await fetch(`${API_BASE}/rates`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ 
                shipment,
                carrier: carrier || null
            })
        });
        
        const result = await response.json();
        
        if (result.success) {
            let html = '';
            const rates = Array.isArray(result.data) ? result.data : [result.data];
            
            rates.forEach(carrierRates => {
                if (carrierRates.error) {
                    html += `
                        <div class="result-card">
                            <div class="result-header">
                                <span class="carrier-name">${carrierRates.carrier}</span>
                                <span class="error-badge">Error</span>
                            </div>
                            <p>${carrierRates.error}</p>
                        </div>
                    `;
                } else {
                    html += `
                        <div class="result-card">
                            <div class="result-header">
                                <span class="carrier-name">${carrierRates.carrier}</span>
                            </div>
                            <div class="rate-grid">
                                ${carrierRates.services.map(service => `
                                    <div class="rate-item">
                                        <div class="service-name">${service.serviceName}</div>
                                        <div class="rate-amount">$${service.rate.toFixed(2)}</div>
                                        <div class="delivery-days">${service.deliveryDays} day${service.deliveryDays > 1 ? 's' : ''}</div>
                                    </div>
                                `).join('')}
                            </div>
                        </div>
                    `;
                }
            });
            
            displayResult('ratesResult', html);
        } else {
            showError('ratesResult', result.error);
        }
    } catch (error) {
        showError('ratesResult', error.message);
    }
});

// Labels Form
document.getElementById('labelsForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    showLoading('labelsResult');
    
    const formData = getFormData(e.target);
    const { carrier, ...shipment } = formData;
    
    try {
        const response = await fetch(`${API_BASE}/labels`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ shipment, carrier })
        });
        
        const result = await response.json();
        
        if (result.success) {
            const label = result.data;
            const html = `
                <div class="result-card">
                    <div class="result-header">
                        <span class="carrier-name">${label.carrier}</span>
                        <span class="success-badge">Success</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Tracking Number:</span>
                        <span class="info-value">${label.trackingNumber}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Service:</span>
                        <span class="info-value">${label.service}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Cost:</span>
                        <span class="info-value">$${label.cost.toFixed(2)}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Label URL:</span>
                        <span class="info-value"><a href="${label.labelUrl}" target="_blank">${label.labelUrl}</a></span>
                    </div>
                </div>
            `;
            displayResult('labelsResult', html);
        } else {
            showError('labelsResult', result.error);
        }
    } catch (error) {
        showError('labelsResult', error.message);
    }
});

// Tracking Form
document.getElementById('trackingForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    showLoading('trackingResult');
    
    const formData = getFormData(e.target);
    
    try {
        const response = await fetch(`${API_BASE}/tracking/${formData.carrier}/${formData.trackingNumber}`);
        const result = await response.json();
        
        if (result.success) {
            const tracking = result.data;
            const html = `
                <div class="result-card">
                    <div class="result-header">
                        <span class="carrier-name">${tracking.carrier}</span>
                        <span class="success-badge">${tracking.status}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Tracking Number:</span>
                        <span class="info-value">${tracking.trackingNumber}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Estimated Delivery:</span>
                        <span class="info-value">${new Date(tracking.estimatedDelivery).toLocaleDateString()}</span>
                    </div>
                    <h3>Tracking Events</h3>
                    ${tracking.events.map(event => `
                        <div class="tracking-event">
                            <div class="event-header">
                                <span class="event-status">${event.status}</span>
                                <span class="event-date">${new Date(event.date).toLocaleString()}</span>
                            </div>
                            <div class="event-location">${event.location}</div>
                            <div class="event-description">${event.description}</div>
                        </div>
                    `).join('')}
                </div>
            `;
            displayResult('trackingResult', html);
        } else {
            showError('trackingResult', result.error);
        }
    } catch (error) {
        showError('trackingResult', error.message);
    }
});

// Address Validation Form
document.getElementById('addressForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    showLoading('addressResult');
    
    const formData = getFormData(e.target);
    const { carrier, ...address } = formData;
    
    try {
        const response = await fetch(`${API_BASE}/validate-address`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ address, carrier })
        });
        
        const result = await response.json();
        
        if (result.success) {
            const validation = result.data;
            const html = `
                <div class="result-card">
                    <div class="result-header">
                        <span class="carrier-name">${validation.carrier}</span>
                        <span class="${validation.valid ? 'success-badge' : 'error-badge'}">
                            ${validation.valid ? 'Valid' : 'Invalid'}
                        </span>
                    </div>
                    ${validation.valid ? `
                        <h3>Validated Address</h3>
                        <div class="info-row">
                            <span class="info-label">Street:</span>
                            <span class="info-value">${validation.validatedAddress.street}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">City:</span>
                            <span class="info-value">${validation.validatedAddress.city}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">State:</span>
                            <span class="info-value">${validation.validatedAddress.state}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Postal Code:</span>
                            <span class="info-value">${validation.validatedAddress.postalCode}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Country:</span>
                            <span class="info-value">${validation.validatedAddress.country}</span>
                        </div>
                    ` : ''}
                </div>
            `;
            displayResult('addressResult', html);
        } else {
            showError('addressResult', result.error);
        }
    } catch (error) {
        showError('addressResult', error.message);
    }
});

// Pickup Form
document.getElementById('pickupForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    showLoading('pickupResult');
    
    const formData = getFormData(e.target);
    const { carrier, ...pickupDetails } = formData;
    
    try {
        const response = await fetch(`${API_BASE}/pickup`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ pickupDetails, carrier })
        });
        
        const result = await response.json();
        
        if (result.success) {
            const pickup = result.data;
            const html = `
                <div class="result-card">
                    <div class="result-header">
                        <span class="carrier-name">${pickup.carrier}</span>
                        <span class="success-badge">${pickup.status}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Confirmation Number:</span>
                        <span class="info-value">${pickup.confirmationNumber}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Pickup Date:</span>
                        <span class="info-value">${pickup.pickupDate}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Time Window:</span>
                        <span class="info-value">${pickup.timeWindow}</span>
                    </div>
                </div>
            `;
            displayResult('pickupResult', html);
        } else {
            showError('pickupResult', result.error);
        }
    } catch (error) {
        showError('pickupResult', error.message);
    }
});

// Customs Form
document.getElementById('customsForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    showLoading('customsResult');
    
    const formData = getFormData(e.target);
    const { carrier, ...customsData } = formData;
    
    try {
        const response = await fetch(`${API_BASE}/customs`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ customsData, carrier })
        });
        
        const result = await response.json();
        
        if (result.success) {
            const customs = result.data;
            const html = `
                <div class="result-card">
                    <div class="result-header">
                        <span class="carrier-name">${customs.carrier}</span>
                        <span class="success-badge">Generated</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Document ID:</span>
                        <span class="info-value">${customs.documentId}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Document Type:</span>
                        <span class="info-value">${customs.type}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Document URL:</span>
                        <span class="info-value"><a href="${customs.documentUrl}" target="_blank">${customs.documentUrl}</a></span>
                    </div>
                    <h3>Items</h3>
                    ${customs.items.map((item, i) => `
                        <div class="customs-item">
                            <strong>Item ${i + 1}:</strong> ${item.description || item} 
                            ${item.quantity ? `(Qty: ${item.quantity})` : ''}
                            ${item.value ? `- $${item.value}` : ''}
                        </div>
                    `).join('')}
                </div>
            `;
            displayResult('customsResult', html);
        } else {
            showError('customsResult', result.error);
        }
    } catch (error) {
        showError('customsResult', error.message);
    }
});

// Function to add customs items dynamically
let customsItemCount = 1;
function addCustomsItem() {
    const container = document.getElementById('customsItems');
    const newItem = `
        <div class="customs-item">
            <div class="form-row">
                <div class="form-group">
                    <label>Description</label>
                    <input type="text" name="items[${customsItemCount}].description" placeholder="Product description" required>
                </div>
                <div class="form-group">
                    <label>Quantity</label>
                    <input type="number" name="items[${customsItemCount}].quantity" value="1" required>
                </div>
                <div class="form-group">
                    <label>Value (USD)</label>
                    <input type="number" name="items[${customsItemCount}].value" step="0.01" required>
                </div>
                <div class="form-group">
                    <label>HS Code</label>
                    <input type="text" name="items[${customsItemCount}].hsCode" placeholder="Optional">
                </div>
            </div>
        </div>
    `;
    container.insertAdjacentHTML('beforeend', newItem);
    customsItemCount++;
}
