
if (typeof API_BASE === 'undefined') {
    var API_BASE = window.location.origin + '/jee-event-manager';
}

// Endpoints API
if (typeof ENDPOINTS === 'undefined') {
    var ENDPOINTS = {
        dashboard: `${API_BASE}/participant/dashboard`,
        inscriptions: `${API_BASE}/participant/inscriptions`,
        evenements: `${API_BASE}/catalogue`,
        commentaires: `${API_BASE}/api/commentaires`,
        evaluations: `${API_BASE}/api/evaluations`,
        profil: `${API_BASE}/participant/profil`
    };
}

// Configuration globale
if (typeof APP_CONFIG === 'undefined') {
    var APP_CONFIG = {
        dateFormat: 'DD/MM/YYYY',
        timeFormat: 'HH:mm',
        locale: 'fr-FR'
    };
}
