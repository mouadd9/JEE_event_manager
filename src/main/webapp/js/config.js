
if (typeof API_BASE === 'undefined') {
    // Auto-detect context path - use current path or default to /jee-event-manager for local dev
    var contextPath = window.location.pathname.split('/')[1];
    // If we're at root or common pages, use root context, otherwise use /jee-event-manager
    var API_BASE = window.location.origin + (contextPath && !['participant', 'organisateur', 'admin', 'login.jsp', 'register.jsp', 'catalogue.jsp'].includes(contextPath) ? '/' + contextPath : '');
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
