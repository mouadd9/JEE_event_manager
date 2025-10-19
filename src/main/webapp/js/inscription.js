
if (typeof API_BASE === 'undefined') {
    var API_BASE = window.location.origin + '/jee-event-manager';
}

// ========== Modal Inscription ==========
function openInscriptionModal(eventId) {
    const dashboardData = window.dashboardAPI?.getDashboardData();
    if (!dashboardData) return;
    
    // Trouver l'événement
    const event = dashboardData.evenementsDisponibles.find(e => e.evenementId == eventId);
    if (!event) {
        window.dashboardAPI.showToast('Événement introuvable', 'error');
        return;
    }
    
    // Vérifier si déjà inscrit
    if (event.statutInscription) {
        window.dashboardAPI.showToast('Vous êtes déjà inscrit à cet événement', 'warning');
        return;
    }
    
    // Vérifier la capacité
    if (event.capaciteDisponible <= 0) {
        window.dashboardAPI.showToast('Cet événement est complet', 'error');
        return;
    }
    
    // Remplir le modal
    document.getElementById('inscriptionEventId').value = eventId;
    
    const eventInfo = document.getElementById('inscriptionEventInfo');
    eventInfo.innerHTML = `
        <div class="card bg-light">
            <div class="card-body">
                <h6 class="card-title mb-2">${escapeHtml(event.titre)}</h6>
                <p class="card-text small text-muted mb-1">
                    <i class="bi bi-calendar3 me-1"></i>
                    ${new Date(event.dateDebut).toLocaleDateString('fr-FR', {
                        day: 'numeric',
                        month: 'long',
                        year: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit'
                    })}
                </p>
                <p class="card-text small text-muted mb-0">
                    <i class="bi bi-geo-alt me-1"></i>
                    ${escapeHtml(event.lieu)}
                </p>
            </div>
        </div>
    `;
    
    // Afficher la capacité
    const capaciteText = document.getElementById('capaciteText');
    const capaciteClass = event.capaciteDisponible <= 10 ? 'text-danger' : 'text-success';
    capaciteText.innerHTML = `
        <strong class="${capaciteClass}">${event.capaciteDisponible}</strong> places disponibles
    `;
    
    // Réinitialiser le formulaire
    document.getElementById('formInscription').reset();
    document.getElementById('inscriptionQuantite').max = Math.min(10, event.capaciteDisponible);
    
    // Afficher le modal
    const modal = new bootstrap.Modal(document.getElementById('modalInscription'));
    modal.show();
}

// ========== Confirmation Inscription ==========
document.getElementById('btnConfirmInscription')?.addEventListener('click', async function() {
    const eventId = document.getElementById('inscriptionEventId').value;
    const typeBillet = document.getElementById('inscriptionTypeBillet').value;
    const quantite = parseInt(document.getElementById('inscriptionQuantite').value);
    
    // Validation
    if (!typeBillet) {
        window.dashboardAPI.showToast('Veuillez sélectionner un type de billet', 'warning');
        return;
    }
    
    if (!quantite || quantite < 1 || quantite > 10) {
        window.dashboardAPI.showToast('Quantité invalide (1-10)', 'warning');
        return;
    }
    
    // Désactiver le bouton
    const btn = this;
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Inscription...';
    
    try {
        const response = await fetch(`${API_BASE}/participant/inscriptions`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                evenementId: parseInt(eventId),
                typeBillet: typeBillet,
                quantite: quantite
            })
        });
        
        const result = await response.json();
        
        if (response.ok && result.success) {
            // Fermer le modal
            bootstrap.Modal.getInstance(document.getElementById('modalInscription')).hide();
            
            // Afficher le succès
            window.dashboardAPI.showToast('Inscription réussie ! 🎉', 'success');
            
            // Rafraîchir le dashboard complètement
            setTimeout(() => {
                window.location.reload();
            }, 1500);
            
        } else {
            window.dashboardAPI.showToast(result.message || 'Erreur lors de l\'inscription', 'error');
        }
        
    } catch (error) {
        console.error('Erreur inscription:', error);
        window.dashboardAPI.showToast('Erreur de connexion au serveur', 'error');
    } finally {
        // Réactiver le bouton
        btn.disabled = false;
        btn.innerHTML = '<i class="bi bi-check-circle me-1"></i>Confirmer l\'inscription';
    }
});

// ========== Annulation Inscription ==========
function confirmCancelInscription(inscriptionId) {
    document.getElementById('cancelInscriptionId').value = inscriptionId;
    const modal = new bootstrap.Modal(document.getElementById('modalConfirmCancel'));
    modal.show();
}

document.getElementById('btnConfirmCancel')?.addEventListener('click', async function() {
    const inscriptionId = document.getElementById('cancelInscriptionId').value;
    
    if (!inscriptionId) return;
    
    // Désactiver le bouton
    const btn = this;
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Annulation...';
    
    try {
        const response = await fetch(`${API_BASE}/participant/inscriptions/${inscriptionId}`, {
            method: 'DELETE',
            credentials: 'include',
            headers: {
                'Accept': 'application/json'
            }
        });
        
        const result = await response.json();
        
        if (response.ok && result.success) {
            // Fermer le modal
            bootstrap.Modal.getInstance(document.getElementById('modalConfirmCancel')).hide();
            
            // Afficher le succès
            window.dashboardAPI.showToast('Inscription annulée', 'success');
            
            // Rafraîchir le dashboard complètement
            setTimeout(() => {
                window.location.reload();
            }, 1500);
            
        } else {
            window.dashboardAPI.showToast(result.message || 'Erreur lors de l\'annulation', 'error');
        }
        
    } catch (error) {
        console.error('Erreur annulation:', error);
        window.dashboardAPI.showToast('Erreur de connexion au serveur', 'error');
    } finally {
        // Réactiver le bouton
        btn.disabled = false;
        btn.innerHTML = '<i class="bi bi-x-circle me-1"></i>Oui, annuler';
    }
});

// ========== Modal Détails Événement ==========
function openEventDetailsModal(eventId) {
    const dashboardData = window.dashboardAPI?.getDashboardData();
    if (!dashboardData) return;
    
    // Trouver l'événement
    const event = dashboardData.evenementsDisponibles.find(e => e.evenementId == eventId);
    if (!event) {
        window.dashboardAPI.showToast('Événement introuvable', 'error');
        return;
    }
    
    // Construire le contenu du modal
    const modalBody = document.getElementById('modalEventBody');
    const dateDebut = new Date(event.dateDebut);
    const dateFin = new Date(event.dateFin);
    
    // Badge statut inscription
    let inscriptionBadge = '';
    if (event.statutInscription) {
        const badges = {
            'ACCEPTEE': '<span class="badge badge-inscrit fs-6">✓ Vous êtes inscrit</span>',
            'EN_ATTENTE': '<span class="badge bg-warning fs-6">⏳ En attente</span>',
            'ANNULEE': '<span class="badge bg-secondary fs-6">✗ Annulée</span>'
        };
        inscriptionBadge = badges[event.statutInscription] || '';
    }
    
    // Note moyenne
    const rating = event.noteMoyenne || 0;
    const stars = '★'.repeat(Math.floor(rating)) + '☆'.repeat(5 - Math.floor(rating));
    
    modalBody.innerHTML = `
        <div class="event-details">
            <!-- Header -->
            <div class="mb-4">
                <div class="d-flex justify-content-between align-items-start mb-3">
                    <h4 class="mb-0">${escapeHtml(event.titre)}</h4>
                    ${inscriptionBadge}
                </div>
                
                <div class="d-flex align-items-center gap-3 mb-3">
                    <div class="event-rating">
                        <span class="stars text-warning fs-5">${stars}</span>
                        <span class="text-muted">${rating.toFixed(1)}/5</span>
                        <span class="text-muted small">(${event.nombreEvaluations || 0} avis)</span>
                    </div>
                    <span class="badge bg-primary">${event.statut}</span>
                </div>
            </div>
            
            <!-- Informations principales -->
            <div class="card mb-4">
                <div class="card-body">
                    <h6 class="card-title mb-3">
                        <i class="bi bi-info-circle text-primary me-2"></i>Informations
                    </h6>
                    
                    <div class="row g-3">
                        <div class="col-md-6">
                            <div class="d-flex align-items-start">
                                <i class="bi bi-calendar3 text-primary me-2 mt-1"></i>
                                <div>
                                    <div class="fw-semibold small text-muted">Date et heure</div>
                                    <div>${dateDebut.toLocaleDateString('fr-FR', {
                                        weekday: 'long',
                                        day: 'numeric',
                                        month: 'long',
                                        year: 'numeric'
                                    })}</div>
                                    <div class="text-muted small">
                                        ${dateDebut.toLocaleTimeString('fr-FR', {hour: '2-digit', minute: '2-digit'})} - 
                                        ${dateFin.toLocaleTimeString('fr-FR', {hour: '2-digit', minute: '2-digit'})}
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="d-flex align-items-start">
                                <i class="bi bi-geo-alt text-primary me-2 mt-1"></i>
                                <div>
                                    <div class="fw-semibold small text-muted">Lieu</div>
                                    <div>${escapeHtml(event.lieu)}</div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="d-flex align-items-start">
                                <i class="bi bi-people text-primary me-2 mt-1"></i>
                                <div>
                                    <div class="fw-semibold small text-muted">Participants</div>
                                    <div>${event.nombreInscrits}/${event.capacite} inscrits</div>
                                    <div class="text-${event.capaciteDisponible <= 10 ? 'danger' : 'success'} small">
                                        ${event.capaciteDisponible} places restantes
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        ${event.organisateurNom ? `
                        <div class="col-md-6">
                            <div class="d-flex align-items-start">
                                <i class="bi bi-person-badge text-primary me-2 mt-1"></i>
                                <div>
                                    <div class="fw-semibold small text-muted">Organisateur</div>
                                    <div>${escapeHtml(event.organisateurNom)}</div>
                                </div>
                            </div>
                        </div>
                        ` : ''}
                    </div>
                </div>
            </div>
            
            <!-- Description -->
            ${event.description ? `
            <div class="card mb-4">
                <div class="card-body">
                    <h6 class="card-title mb-3">
                        <i class="bi bi-file-text text-primary me-2"></i>Description
                    </h6>
                    <p class="mb-0">${escapeHtml(event.description)}</p>
                </div>
            </div>
            ` : ''}
            
            <!-- Catégories -->
            ${event.categories && event.categories.length > 0 ? `
            <div class="mb-4">
                <h6 class="mb-2">
                    <i class="bi bi-tags text-primary me-2"></i>Catégories
                </h6>
                <div class="d-flex flex-wrap gap-2">
                    ${event.categories.map(cat => 
                        `<span class="badge bg-secondary">${escapeHtml(cat)}</span>`
                    ).join('')}
                </div>
            </div>
            ` : ''}
            
            <!-- Actions -->
            <div class="d-flex gap-2 justify-content-end">
                ${!event.statutInscription && event.capaciteDisponible > 0 ? `
                    <button class="btn btn-primary" onclick="openInscriptionModal(${event.evenementId})">
                        <i class="bi bi-ticket-perforated me-1"></i>S'inscrire à cet événement
                    </button>
                ` : event.capaciteDisponible === 0 ? `
                    <button class="btn btn-secondary" disabled>
                        <i class="bi bi-x-circle me-1"></i>Événement complet
                    </button>
                ` : ''}
            </div>
            
            <!-- Section Commentaires -->
            <div class="mt-4 pt-4 border-top">
                <h6 class="mb-3">
                    <i class="bi bi-chat-dots text-primary me-2"></i>
                    Commentaires (${event.nombreCommentaires || 0})
                </h6>
                <div id="commentairesSection"></div>
            </div>
            
            <!-- Section Évaluations -->
            <div class="mt-4 pt-4 border-top">
                <h6 class="mb-3">
                    <i class="bi bi-star text-primary me-2"></i>
                    Évaluations (${event.nombreEvaluations || 0})
                </h6>
                <div id="evaluationsSection"></div>
            </div>
        </div>
    `;
    
    // Mettre à jour le titre
    document.getElementById('modalEventTitle').textContent = event.titre;
    
    // Afficher le modal
    const modal = new bootstrap.Modal(document.getElementById('modalEventDetails'));
    modal.show();
    
    // Charger les commentaires et évaluations après l'ouverture du modal
    setTimeout(() => {
        if (window.chargerCommentaires) {
            window.chargerCommentaires(event.evenementId);
        }
        if (window.chargerEvaluations) {
            window.chargerEvaluations(event.evenementId);
        }
    }, 300);
}

// ========== Utilitaires ==========
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// ========== Exports ==========
window.openInscriptionModal = openInscriptionModal;
window.openEventDetailsModal = openEventDetailsModal;
window.confirmCancelInscription = confirmCancelInscription;
