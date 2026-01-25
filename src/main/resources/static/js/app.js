const API_BASE = '/api/v1';

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

// Helper function to display results
function displayResult(containerId, html) {
    const container = document.getElementById(containerId);
    container.innerHTML = html;
}

// Helper function to show loading
function showLoading(containerId) {
    displayResult(containerId, '<div class="loading">⏳ Loading...</div>');
}

// Helper function to show error
function showError(containerId, message) {
    displayResult(containerId, `<div class="error-message">❌ Error: ${message}</div>`);
}

// Helper function to show success
function showSuccess(containerId, message) {
    displayResult(containerId, `<div class="success-message">✅ ${message}</div>`);
}

// Rates Calculator
if (document.getElementById('ratesForm')) {
    document.getElementById('ratesForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        showLoading('ratesResult');
        
        const form = e.target;
        const shipment = {
            fromAddress: {
                name: form['origin.name'].value,
                street1: form['origin.street1'].value,
                city: form['origin.city'].value,
                state: form['origin.state'].value,
                postalCode: form['origin.postalCode'].value,
                country: form['origin.country'].value
            },
            toAddress: {
                name: form['destination.name'].value,
                street1: form['destination.street1'].value,
                city: form['destination.city'].value,
                state: form['destination.state'].value,
                postalCode: form['destination.postalCode'].value,
                country: form['destination.country'].value
            },
            packageInfo: {
                weight: parseFloat(form['package.weight'].value),
                length: parseFloat(form['package.length'].value),
                width: parseFloat(form['package.width'].value),
                height: parseFloat(form['package.height'].value)
            }
        };
        
        try {
            const response = await fetch(`${API_BASE}/rates`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(shipment)
            });
            
            if (!response.ok) throw new Error('Failed to fetch rates');
            
            const rates = await response.json();
            
            if (rates.length === 0) {
                displayResult('ratesResult', '<div class="info-message">No rates available</div>');
                return;
            }
            
            let html = '<div class="rates-container">';
            rates.forEach(rate => {
                html += `
                    <div class="result-card">
                        <div class="result-header">
                            <span class="carrier-name">${rate.carrier}</span>
                            <span class="rate-amount">$${rate.rate.toFixed(2)}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Service:</span>
                            <span class="info-value">${rate.service}</span>
                        </div>
                        ${rate.deliveryDays ? `
                        <div class="info-row">
                            <span class="info-label">Delivery:</span>
                            <span class="info-value">${rate.deliveryDays} day${rate.deliveryDays > 1 ? 's' : ''}</span>
                        </div>
                        ` : ''}
                    </div>
                `;
            });
            html += '</div>';
            
            displayResult('ratesResult', html);
        } catch (error) {
            showError('ratesResult', error.message);
        }
    });
}

// Label Generation
if (document.getElementById('labelsForm')) {
    document.getElementById('labelsForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        showLoading('labelsResult');
        
        const form = e.target;
        const shipment = {
            fromAddress: {
                name: form['origin.name'].value,
                street1: form['origin.street1'].value,
                city: form['origin.city'].value,
                state: form['origin.state'].value,
                postalCode: form['origin.postalCode'].value,
                country: form['origin.country'].value
            },
            toAddress: {
                name: form['destination.name'].value,
                street1: form['destination.street1'].value,
                city: form['destination.city'].value,
                state: form['destination.state'].value,
                postalCode: form['destination.postalCode'].value,
                country: form['destination.country'].value
            },
            packageInfo: {
                weight: parseFloat(form['package.weight'].value),
                length: parseFloat(form['package.length'].value),
                width: parseFloat(form['package.width'].value),
                height: parseFloat(form['package.height'].value)
            },
            carrier: form['carrier'].value || 'UPS',
            labelFormat: form['labelFormat'].value || 'PDF'
        };
        
        try {
            const response = await fetch(`${API_BASE}/labels`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(shipment)
            });
            
            if (!response.ok) throw new Error('Failed to generate label');
            
            const label = await response.json();
            
            const html = `
                <div class="result-card">
                    <div class="result-header">
                        <span class="carrier-name">${label.carrier}</span>
                        <span class="success-badge">✅ Generated</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Tracking Number:</span>
                        <span class="info-value tracking-number">${label.trackingNumber}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Format:</span>
                        <span class="info-value">${label.format}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Created:</span>
                        <span class="info-value">${new Date(label.createdAt).toLocaleString()}</span>
                    </div>
                    <div class="label-preview">
                        <button class="btn btn-primary" onclick="downloadLabel('${label.content}', '${label.trackingNumber}', '${label.format}')">
                            📥 Download Label
                        </button>
                    </div>
                </div>
            `;
            
            displayResult('labelsResult', html);
        } catch (error) {
            showError('labelsResult', error.message);
        }
    });
}

// Download label function
function downloadLabel(base64Content, trackingNumber, format) {
    const byteCharacters = atob(base64Content);
    const byteNumbers = new Array(byteCharacters.length);
    for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], { type: getMimeType(format) });
    
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `label_${trackingNumber}.${format.toLowerCase()}`;
    link.click();
    window.URL.revokeObjectURL(url);
}

function getMimeType(format) {
    switch (format.toUpperCase()) {
        case 'PDF': return 'application/pdf';
        case 'PNG': return 'image/png';
        case 'ZPL': return 'text/plain';
        default: return 'application/octet-stream';
    }
}

// Package Tracking
if (document.getElementById('trackingForm')) {
    document.getElementById('trackingForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        showLoading('trackingResult');
        
        const form = e.target;
        const trackingNumber = form['trackingNumber'].value;
        const carrier = form['carrier'].value || '';
        
        try {
            const url = carrier 
                ? `${API_BASE}/tracking/${trackingNumber}?carrier=${carrier}`
                : `${API_BASE}/tracking/${trackingNumber}`;
                
            const response = await fetch(url);
            
            if (!response.ok) {
                if (response.status === 404) {
                    throw new Error('Tracking information not found');
                }
                throw new Error('Failed to fetch tracking information');
            }
            
            const tracking = await response.json();
            
            const html = `
                <div class="result-card">
                    <div class="result-header">
                        <span class="carrier-name">${tracking.carrier}</span>
                        <span class="status-badge status-${tracking.currentStatus.toLowerCase().replace('_', '-')}">${tracking.currentStatus.replace('_', ' ')}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Tracking Number:</span>
                        <span class="info-value tracking-number">${tracking.trackingNumber}</span>
                    </div>
                    ${tracking.estimatedDelivery ? `
                    <div class="info-row">
                        <span class="info-label">Estimated Delivery:</span>
                        <span class="info-value">${new Date(tracking.estimatedDelivery).toLocaleDateString()}</span>
                    </div>
                    ` : ''}
                    ${tracking.actualDelivery ? `
                    <div class="info-row">
                        <span class="info-label">Delivered:</span>
                        <span class="info-value">${new Date(tracking.actualDelivery).toLocaleDateString()}</span>
                    </div>
                    ` : ''}
                    <h3>Tracking History</h3>
                    <div class="tracking-timeline">
                        ${tracking.events.map(event => `
                            <div class="tracking-event">
                                <div class="event-icon ${event.status.toLowerCase().replace('_', '-')}">●</div>
                                <div class="event-details">
                                    <div class="event-header">
                                        <span class="event-status">${event.status.replace('_', ' ')}</span>
                                        <span class="event-time">${new Date(event.timestamp).toLocaleString()}</span>
                                    </div>
                                    <div class="event-message">${event.message}</div>
                                    ${event.location ? `<div class="event-location">📍 ${event.location}</div>` : ''}
                                </div>
                            </div>
                        `).join('')}
                    </div>
                </div>
            `;
            
            displayResult('trackingResult', html);
        } catch (error) {
            showError('trackingResult', error.message);
        }
    });
}

// Address Validation
if (document.getElementById('addressForm')) {
    document.getElementById('addressForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        showLoading('addressResult');
        
        const form = e.target;
        const address = {
            name: form['name'].value,
            street1: form['street1'].value,
            street2: form['street2'].value || null,
            city: form['city'].value,
            state: form['state'].value,
            postalCode: form['postalCode'].value,
            country: form['country'].value
        };
        
        try {
            const response = await fetch(`${API_BASE}/address/validate`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(address)
            });
            
            if (!response.ok) throw new Error('Failed to validate address');
            
            const result = await response.json();
            
            const html = `
                <div class="result-card">
                    <div class="result-header">
                        <span class="carrier-name">Address Validation</span>
                        <span class="${result.valid ? 'success-badge' : 'error-badge'}">${result.valid ? '✅ Valid' : '❌ Invalid'}</span>
                    </div>
                    <h3>Standardized Address</h3>
                    <div class="info-row">
                        <span class="info-label">Name:</span>
                        <span class="info-value">${result.address.name}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Street:</span>
                        <span class="info-value">${result.address.street1}${result.address.street2 ? ' ' + result.address.street2 : ''}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">City, State ZIP:</span>
                        <span class="info-value">${result.address.city}, ${result.address.state} ${result.address.postalCode}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Country:</span>
                        <span class="info-value">${result.address.country}</span>
                    </div>
                    ${result.addressType ? `
                    <div class="info-row">
                        <span class="info-label">Address Type:</span>
                        <span class="info-value">${result.addressType}</span>
                    </div>
                    ` : ''}
                </div>
            `;
            
            displayResult('addressResult', html);
        } catch (error) {
            showError('addressResult', error.message);
        }
    });
}

// Health check on page load
async function checkHealth() {
    try {
        const response = await fetch(`${API_BASE}/health`);
        const health = await response.json();
        console.log('API Health:', health);
    } catch (error) {
        console.error('API Health Check Failed:', error);
    }
}

// Run health check when page loads
document.addEventListener('DOMContentLoaded', checkHealth);
