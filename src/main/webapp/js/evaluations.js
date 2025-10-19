
if (typeof API_BASE === 'undefined') {
    var API_BASE = window.location.origin + '/jee-event-manager';
}

// ========== Chargement Évaluations ==========
async function chargerEvaluations(evenementId) {
    const container = document.getElementById('evaluationsSection');
    if (!container) return;
    
    try {
        container.innerHTML = `
            <div class="text-center py-3">
                <div class="spinner-border spinner-border-sm text-primary"></div>
                <p class="text-muted small mt-2">Chargement des évaluations...</p>
            </div>
        `;
        
        const response = await fetch(`${API_BASE}/api/evaluations?evenementId=${evenementId}`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Accept': 'application/json'
            }
        });
        
        if (!response.ok) {
            throw new Error(`Erreur ${response.status}`);
        }
        
        const result = await response.json();
        
        if (result.success) {
            const moyenne = result.metadata?.moyenne || 0;
            const total = result.metadata?.total || 0;
            afficherEvaluations(result.data || [], evenementId, moyenne, total);
        } else {
            container.innerHTML = `
                <div class="alert alert-warning">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    Impossible de charger les évaluations
                </div>
            `;
        }
        
    } catch (error) {
        console.error('Erreur chargerEvaluations:', error);
        container.innerHTML = `
            <div class="alert alert-danger">
                <i class="bi bi-x-circle me-2"></i>
                Erreur lors du chargement des évaluations
            </div>
        `;
    }
}

// ========== Affichage Évaluations ==========
function afficherEvaluations(evaluations, evenementId, moyenne, total) {
    const container = document.getElementById('evaluationsSection');
    if (!container) return;
    
    // Vérifier si l'utilisateur est inscrit
    const dashboardData = window.dashboardAPI?.getDashboardData();
    const event = dashboardData?.evenementsDisponibles?.find(e => e.evenementId == evenementId);
    const isInscrit = event?.statutInscription === 'ACCEPTEE';
    
    // Vérifier si l'utilisateur a déjà évalué
    const participantId = dashboardData?.participant?.id;
    const evaluationParticipant = evaluations.find(e => e.participantId === participantId);
    
    let html = '';
    
    // Résumé des évaluations
    html += `
        <div class="card mb-3 bg-light">
            <div class="card-body">
                <div class="row align-items-center">
                    <div class="col-md-4 text-center border-end">
                        <div class="display-4 fw-bold text-primary">${moyenne.toFixed(1)}</div>
                        <div class="text-warning fs-4">${getStarsHTML(moyenne)}</div>
                        <div class="text-muted small">${total} évaluation${total > 1 ? 's' : ''}</div>
                    </div>
                    <div class="col-md-8">
                        ${creerDistributionNotesHTML(evaluations)}
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Formulaire d'évaluation (si inscrit)
    if (isInscrit) {
        html += creerFormulaireEvaluationHTML(evenementId, evaluationParticipant);
    }
    
    // Liste des évaluations
    if (evaluations.length === 0) {
        html += `
            <div class="text-center py-4">
                <i class="bi bi-star text-muted" style="font-size: 3rem;"></i>
                <p class="text-muted mt-2">Aucune évaluation pour le moment</p>
                ${isInscrit ? '<p class="text-muted small">Soyez le premier à évaluer !</p>' : ''}
            </div>
        `;
    } else {
        html += '<div class="evaluations-list mt-3">';
        evaluations.forEach(evaluation => {
            html += creerEvaluationHTML(evaluation);
        });
        html += '</div>';
    }
    
    container.innerHTML = html;
    
    // Attacher les event listeners
    attacherEvaluationListeners(evenementId);
}

// ========== Distribution des Notes ==========
function creerDistributionNotesHTML(evaluations) {
    const distribution = [0, 0, 0, 0, 0, 0]; // Index 0 non utilisé, 1-5 pour les notes
    
    evaluations.forEach(e => {
        if (e.note >= 1 && e.note <= 5) {
            distribution[e.note]++;
        }
    });
    
    const total = evaluations.length || 1;
    
    let html = '<div class="notes-distribution">';
    for (let i = 5; i >= 1; i--) {
        const count = distribution[i];
        const percentage = (count / total) * 100;
        
        html += `
            <div class="d-flex align-items-center mb-2">
                <span class="me-2 small" style="width: 60px;">${i} étoile${i > 1 ? 's' : ''}</span>
                <div class="progress flex-grow-1" style="height: 8px;">
                    <div class="progress-bar bg-warning" style="width: ${percentage}%"></div>
                </div>
                <span class="ms-2 small text-muted" style="width: 40px;">${count}</span>
            </div>
        `;
    }
    html += '</div>';
    
    return html;
}

// ========== Formulaire Évaluation ==========
function creerFormulaireEvaluationHTML(evenementId, evaluationExistante) {
    const noteActuelle = evaluationExistante?.note || 0;
    const texteActuel = evaluationExistante?.texte || '';
    const isModification = !!evaluationExistante;
    
    return `
        <div class="card mb-3">
            <div class="card-body">
                <h6 class="card-title mb-3">
                    ${isModification ? 'Modifier votre évaluation' : 'Évaluer cet événement'}
                </h6>
                <form id="formEvaluation" data-event-id="${evenementId}">
                    <div class="mb-3">
                        <label class="form-label">Note <span class="text-danger">*</span></label>
                        <div class="rating-input" id="ratingInput">
                            ${[1, 2, 3, 4, 5].map(i => `
                                <i class="bi bi-star${i <= noteActuelle ? '-fill' : ''} rating-star" 
                                   data-rating="${i}"></i>
                            `).join('')}
                        </div>
                        <input type="hidden" id="evaluationNote" value="${noteActuelle}" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="evaluationTexte" class="form-label">
                            Commentaire (optionnel)
                        </label>
                        <textarea class="form-control" id="evaluationTexte" 
                                  rows="3" maxlength="500" 
                                  placeholder="Partagez votre expérience...">${texteActuel}</textarea>
                        <div class="form-text">
                            <span id="evaluationCount">${texteActuel.length}</span>/500 caractères
                        </div>
                    </div>
                    
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary btn-sm">
                            <i class="bi bi-star me-1"></i>${isModification ? 'Modifier' : 'Publier'} l'évaluation
                        </button>
                        ${isModification ? `
                            <button type="button" class="btn btn-outline-danger btn-sm btn-delete-evaluation" 
                                    data-evaluation-id="${evaluationExistante.evaluationId}">
                                <i class="bi bi-trash me-1"></i>Supprimer
                            </button>
                        ` : ''}
                    </div>
                </form>
            </div>
        </div>
    `;
}

// ========== Création HTML Évaluation ==========
function creerEvaluationHTML(evaluation) {
    const date = new Date(evaluation.horodatage);
    
    return `
        <div class="evaluation-item border-bottom py-3">
            <div class="d-flex justify-content-between align-items-start mb-2">
                <div class="d-flex align-items-center">
                    <div class="avatar-circle bg-primary text-white me-2">
                        ${evaluation.participantNom ? evaluation.participantNom.charAt(0).toUpperCase() : 'U'}
                    </div>
                    <div>
                        <div class="fw-semibold">${escapeHtml(evaluation.participantNom || 'Utilisateur')}</div>
                        <div class="text-warning small">${getStarsHTML(evaluation.note)}</div>
                    </div>
                </div>
                <div class="text-muted small">
                    ${date.toLocaleDateString('fr-FR', {
                        day: 'numeric',
                        month: 'short',
                        year: 'numeric'
                    })}
                </div>
            </div>
            ${evaluation.texte ? `
                <p class="mb-0 ms-5">${escapeHtml(evaluation.texte)}</p>
            ` : ''}
        </div>
    `;
}

// ========== Event Listeners ==========
function attacherEvaluationListeners(evenementId) {
    // Étoiles interactives
    const stars = document.querySelectorAll('.rating-star');
    const noteInput = document.getElementById('evaluationNote');
    
    stars.forEach(star => {
        star.addEventListener('click', function() {
            const rating = parseInt(this.dataset.rating);
            if (noteInput) noteInput.value = rating;
            
            // Mettre à jour l'affichage
            stars.forEach((s, index) => {
                if (index < rating) {
                    s.classList.remove('bi-star');
                    s.classList.add('bi-star-fill');
                } else {
                    s.classList.remove('bi-star-fill');
                    s.classList.add('bi-star');
                }
            });
        });
        
        // Effet hover
        star.addEventListener('mouseenter', function() {
            const rating = parseInt(this.dataset.rating);
            stars.forEach((s, index) => {
                if (index < rating) {
                    s.style.color = '#ffc107';
                } else {
                    s.style.color = '';
                }
            });
        });
    });
    
    // Reset hover
    document.getElementById('ratingInput')?.addEventListener('mouseleave', function() {
        const currentRating = parseInt(noteInput?.value || 0);
        stars.forEach((s, index) => {
            s.style.color = '';
        });
    });
    
    // Compteur de caractères
    const textarea = document.getElementById('evaluationTexte');
    const counter = document.getElementById('evaluationCount');
    
    textarea?.addEventListener('input', function() {
        if (counter) counter.textContent = this.value.length;
    });
    
    // Soumission du formulaire
    const form = document.getElementById('formEvaluation');
    form?.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const note = parseInt(noteInput?.value || 0);
        const texte = textarea?.value.trim() || '';
        
        if (note < 1 || note > 5) {
            window.dashboardAPI?.showToast('Veuillez sélectionner une note', 'warning');
            return;
        }
        
        await soumettreEvaluation(evenementId, note, texte);
    });
    
    // Bouton supprimer
    document.querySelector('.btn-delete-evaluation')?.addEventListener('click', async function() {
        const evaluationId = this.dataset.evaluationId;
        if (confirm('Êtes-vous sûr de vouloir supprimer votre évaluation ?')) {
            await supprimerEvaluation(evaluationId, evenementId);
        }
    });
}

// ========== Soumettre Évaluation ==========
async function soumettreEvaluation(evenementId, note, texte) {
    const form = document.getElementById('formEvaluation');
    const submitBtn = form?.querySelector('button[type="submit"]');
    
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Publication...';
    }
    
    try {
        const response = await fetch(`${API_BASE}/api/evaluations?evenementId=${evenementId}`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({ note, texte })
        });
        
        const result = await response.json();
        
        if (response.ok && result.success) {
            window.dashboardAPI?.showToast('Évaluation enregistrée avec succès ! ✓', 'success');
            
            // Afficher la nouvelle moyenne si disponible
            if (result.metadata?.nouvelleMoyenne) {
                console.log('Nouvelle moyenne:', result.metadata.nouvelleMoyenne);
            }
            
            // Recharger les évaluations
            await chargerEvaluations(evenementId);
            
        } else {
            window.dashboardAPI?.showToast(result.message || 'Erreur lors de l\'enregistrement', 'error');
        }
        
    } catch (error) {
        console.error('Erreur soumettreEvaluation:', error);
        window.dashboardAPI?.showToast('Erreur de connexion au serveur', 'error');
    } finally {
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i class="bi bi-star me-1"></i>Publier l\'évaluation';
        }
    }
}

// ========== Supprimer Évaluation ==========
async function supprimerEvaluation(evaluationId, evenementId) {
    try {
        const response = await fetch(`${API_BASE}/evaluations/${evaluationId}`, {
            method: 'DELETE',
            credentials: 'include',
            headers: {
                'Accept': 'application/json'
            }
        });
        
        const result = await response.json();
        
        if (response.ok && result.success) {
            window.dashboardAPI?.showToast('Évaluation supprimée', 'success');
            
            // Recharger les évaluations
            await chargerEvaluations(evenementId);
            
        } else {
            window.dashboardAPI?.showToast(result.message || 'Erreur lors de la suppression', 'error');
        }
        
    } catch (error) {
        console.error('Erreur supprimerEvaluation:', error);
        window.dashboardAPI?.showToast('Erreur de connexion au serveur', 'error');
    }
}

// ========== Utilitaires ==========
function getStarsHTML(rating) {
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 >= 0.5;
    const emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
    
    let html = '';
    for (let i = 0; i < fullStars; i++) html += '★';
    if (hasHalfStar) html += '⯨';
    for (let i = 0; i < emptyStars; i++) html += '☆';
    
    return html;
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// ========== CSS Inline ==========
const evaluationsStyle = document.createElement('style');
evaluationsStyle.textContent = `
    .rating-star {
        font-size: 2rem;
        cursor: pointer;
        color: #ddd;
        transition: color 0.2s;
    }
    
    .rating-star.bi-star-fill {
        color: #ffc107;
    }
    
    .rating-star:hover {
        color: #ffc107;
    }
    
    .rating-input {
        display: flex;
        gap: 0.25rem;
    }
    
    .evaluation-item:last-child {
        border-bottom: none !important;
    }
    
    .evaluations-list {
        max-height: 500px;
        overflow-y: auto;
    }
`;
document.head.appendChild(evaluationsStyle);

// ========== Export ==========
window.chargerEvaluations = chargerEvaluations;
