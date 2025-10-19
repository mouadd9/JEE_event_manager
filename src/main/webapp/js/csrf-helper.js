
function getCSRFToken() {
    // Essayer de récupérer depuis un meta tag
    const metaTag = document.querySelector('meta[name="csrf-token"]');
    if (metaTag) {
        return metaTag.getAttribute('content');
    }
    
    // Sinon, il sera ajouté automatiquement par le serveur dans les headers de réponse
    return null;
}

// Stocker le token CSRF global
let csrfToken = getCSRFToken();

// Intercepter toutes les requêtes fetch pour ajouter le token CSRF
const originalFetch = window.fetch;
window.fetch = function(url, options = {}) {
    // Ajouter le token CSRF pour les méthodes protégées
    const method = (options.method || 'GET').toUpperCase();
    const protectedMethods = ['POST', 'PUT', 'DELETE', 'PATCH'];
    
    if (protectedMethods.includes(method)) {
        options.headers = options.headers || {};
        
        // Ajouter le token CSRF si disponible
        if (csrfToken) {
            if (options.headers instanceof Headers) {
                options.headers.set('X-CSRF-Token', csrfToken);
            } else {
                options.headers['X-CSRF-Token'] = csrfToken;
            }
        }
    }
    
    // Appeler fetch original
    return originalFetch(url, options).then(response => {
        // Mettre à jour le token CSRF depuis les headers de réponse
        const newToken = response.headers.get('X-CSRF-Token');
        if (newToken) {
            csrfToken = newToken;
            
            // Mettre à jour le meta tag si présent
            const metaTag = document.querySelector('meta[name="csrf-token"]');
            if (metaTag) {
                metaTag.setAttribute('content', newToken);
            }
        }
        
        return response;
    });
};

// Ajouter le token CSRF aux formulaires HTML classiques
document.addEventListener('DOMContentLoaded', function() {
    // Ajouter un champ caché _csrf à tous les formulaires
    document.querySelectorAll('form').forEach(form => {
        const method = (form.method || 'GET').toUpperCase();
        const protectedMethods = ['POST', 'PUT', 'DELETE', 'PATCH'];
        
        if (protectedMethods.includes(method)) {
            // Vérifier si le champ n'existe pas déjà
            if (!form.querySelector('input[name="_csrf"]') && csrfToken) {
                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = '_csrf';
                input.value = csrfToken;
                form.appendChild(input);
            }
        }
    });
});

// Export pour utilisation dans d'autres modules
window.csrfHelper = {
    getToken: () => csrfToken,
    setToken: (token) => { csrfToken = token; }
};
