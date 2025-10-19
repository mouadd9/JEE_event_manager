
document.addEventListener('DOMContentLoaded', function() {
    initializeEventActions();
});

function initializeEventActions() {
    console.log('Initialisation Event Actions...');
    
    // Gérer les clics sur les boutons d'action des événements
    document.addEventListener('click', function(e) {
        // Bouton Commenter
        if (e.target.closest('.btn-add-comment')) {
            e.preventDefault();
            e.stopPropagation();
            console.log('Clic sur bouton Commenter détecté');
            const btn = e.target.closest('.btn-add-comment');
            const evenementId = btn.dataset.evenementId;
            const evenementTitre = btn.dataset.evenementTitre;
            console.log('Données:', { evenementId, evenementTitre });
            openCommentModal(evenementId, evenementTitre);
        }
        
        // Bouton Évaluer
        if (e.target.closest('.btn-add-evaluation')) {
            e.preventDefault();
            e.stopPropagation();
            console.log('Clic sur bouton Évaluer détecté');
            const btn = e.target.closest('.btn-add-evaluation');
            const evenementId = btn.dataset.evenementId;
            const evenementTitre = btn.dataset.evenementTitre;
            console.log('Données:', { evenementId, evenementTitre });
            openEvaluationModal(evenementId, evenementTitre);
        }
    });
    
    // Initialiser les listeners des modals
    initializeCommentModal();
    initializeEvaluationModal();
    
    console.log('Event Actions initialisé');
}

// ========== Modal Commentaire ==========
function openCommentModal(evenementId, evenementTitre) {
    console.log('openCommentModal appelé:', evenementId, evenementTitre);
    
    const modal = document.getElementById('modalAddComment');
    if (!modal) {
        console.error('Modal modalAddComment non trouvé');
        return;
    }
    
    // Remplir les informations
    document.getElementById('commentEventId').value = evenementId;
    document.getElementById('commentEventInfo').innerHTML = `
        <div class="alert alert-info mb-0">
            <i class="bi bi-calendar-event me-2"></i>
            <strong>${escapeHtml(evenementTitre)}</strong>
        </div>
    `;
    
    // Réinitialiser le formulaire
    document.getElementById('commentTexte').value = '';
    document.getElementById('commentCharCount').textContent = '0';
    
    // Ouvrir le modal
    if (typeof bootstrap === 'undefined') {
        console.error('Bootstrap non chargé');
        return;
    }
    
    const bsModal = new bootstrap.Modal(modal);
    bsModal.show();
    console.log('Modal commentaire ouvert');
}

function initializeCommentModal() {
    const textarea = document.getElementById('commentTexte');
    const counter = document.getElementById('commentCharCount');
    const submitBtn = document.getElementById('btnSubmitComment');
    
    // Compteur de caractères
    textarea?.addEventListener('input', function() {
        if (counter) {
            counter.textContent = this.value.length;
        }
    });
    
    // Soumission
    submitBtn?.addEventListener('click', async function() {
        const evenementId = document.getElementById('commentEventId').value;
        const texte = textarea.value.trim();
        
        if (!texte) {
            showToast('Le commentaire ne peut pas être vide', 'warning');
            return;
        }
        
        if (texte.length > 1000) {
            showToast('Le commentaire ne peut pas dépasser 1000 caractères', 'warning');
            return;
        }
        
        await submitComment(evenementId, texte);
    });
}

async function submitComment(evenementId, texte) {
    const submitBtn = document.getElementById('btnSubmitComment');
    const originalHTML = submitBtn.innerHTML;
    
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Publication...';
    
    try {
        const API_BASE = window.location.origin + '/jee-event-manager';
        const response = await fetch(`${API_BASE}/api/commentaires?evenementId=${evenementId}`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({ texte })
        });
        
        const result = await response.json();
        
        if (response.ok && result.success) {
            showToast('Commentaire publié avec succès ! ✓', 'success');
            
            // Fermer le modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('modalAddComment'));
            modal?.hide();
            
            // Recharger le dashboard si disponible
            if (window.dashboardAPI?.loadDashboard) {
                await window.dashboardAPI.loadDashboard();
            }
            
        } else {
            showToast(result.message || 'Erreur lors de la publication', 'error');
        }
        
    } catch (error) {
        console.error('Erreur submitComment:', error);
        showToast('Erreur de connexion au serveur', 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = originalHTML;
    }
}

// ========== Modal Évaluation ==========
function openEvaluationModal(evenementId, evenementTitre) {
    console.log('openEvaluationModal appelé:', evenementId, evenementTitre);
    
    const modal = document.getElementById('modalAddEvaluation');
    if (!modal) {
        console.error('Modal modalAddEvaluation non trouvé');
        return;
    }
    
    // Remplir les informations
    document.getElementById('evaluationEventId').value = evenementId;
    document.getElementById('evaluationEventInfo').innerHTML = `
        <div class="alert alert-warning mb-0">
            <i class="bi bi-calendar-event me-2"></i>
            <strong>${escapeHtml(evenementTitre)}</strong>
        </div>
    `;
    
    // Réinitialiser le formulaire
    document.getElementById('evaluationNote').value = '';
    document.getElementById('evaluationTexte').value = '';
    document.getElementById('evaluationCharCount').textContent = '0';
    
    // Réinitialiser les étoiles
    document.querySelectorAll('#starRating i').forEach(star => {
        star.classList.remove('bi-star-fill');
        star.classList.add('bi-star');
    });
    
    // Ouvrir le modal
    if (typeof bootstrap === 'undefined') {
        console.error('Bootstrap non chargé');
        return;
    }
    
    const bsModal = new bootstrap.Modal(modal);
    bsModal.show();
    console.log('Modal évaluation ouvert');
}

function initializeEvaluationModal() {
    const stars = document.querySelectorAll('#starRating i');
    const noteInput = document.getElementById('evaluationNote');
    const textarea = document.getElementById('evaluationTexte');
    const counter = document.getElementById('evaluationCharCount');
    const submitBtn = document.getElementById('btnSubmitEvaluation');
    
    // Gestion des étoiles
    stars.forEach(star => {
        star.addEventListener('click', function() {
            const rating = parseInt(this.dataset.rating);
            noteInput.value = rating;
            
            // Mettre à jour l'affichage
            stars.forEach((s, index) => {
                const starRating = parseInt(s.dataset.rating);
                if (starRating <= rating) {
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
            stars.forEach(s => {
                const starRating = parseInt(s.dataset.rating);
                if (starRating <= rating) {
                    s.style.color = '#ffc107';
                } else {
                    s.style.color = '';
                }
            });
        });
    });
    
    // Reset hover
    document.getElementById('starRating')?.addEventListener('mouseleave', function() {
        stars.forEach(s => {
            s.style.color = '';
        });
    });
    
    // Compteur de caractères
    textarea?.addEventListener('input', function() {
        if (counter) {
            counter.textContent = this.value.length;
        }
    });
    
    // Soumission
    submitBtn?.addEventListener('click', async function() {
        const evenementId = document.getElementById('evaluationEventId').value;
        const note = parseInt(noteInput.value);
        const texte = textarea.value.trim();
        
        if (!note || note < 1 || note > 5) {
            showToast('Veuillez sélectionner une note (1-5 étoiles)', 'warning');
            return;
        }
        
        if (texte.length > 500) {
            showToast('Le commentaire ne peut pas dépasser 500 caractères', 'warning');
            return;
        }
        
        await submitEvaluation(evenementId, note, texte);
    });
}

async function submitEvaluation(evenementId, note, texte) {
    const submitBtn = document.getElementById('btnSubmitEvaluation');
    const originalHTML = submitBtn.innerHTML;
    
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Publication...';
    
    try {
        const API_BASE = window.location.origin + '/jee-event-manager';
        const response = await fetch(`${API_BASE}/api/evaluations?evenementId=${evenementId}`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({ note, texte: texte || null })
        });
        
        const result = await response.json();
        
        if (response.ok && result.success) {
            showToast('Évaluation enregistrée avec succès ! ✓', 'success');
            
            // Fermer le modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('modalAddEvaluation'));
            modal?.hide();
            
            // Recharger le dashboard si disponible
            if (window.dashboardAPI?.loadDashboard) {
                await window.dashboardAPI.loadDashboard();
            }
            
        } else {
            showToast(result.message || 'Erreur lors de l\'enregistrement', 'error');
        }
        
    } catch (error) {
        console.error('Erreur submitEvaluation:', error);
        showToast('Erreur de connexion au serveur', 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = originalHTML;
    }
}

// ========== Utilitaires ==========
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function showToast(message, type = 'info') {
    // Utiliser le système de toast du dashboard si disponible
    if (window.dashboardAPI?.showToast) {
        window.dashboardAPI.showToast(message, type);
        return;
    }
    
    // Fallback: toast Bootstrap simple
    const toastEl = document.getElementById('toastNotification');
    if (toastEl) {
        const toastBody = document.getElementById('toastMessage');
        if (toastBody) {
            toastBody.textContent = message;
        }
        
        const toast = new bootstrap.Toast(toastEl);
        toast.show();
    } else {
        // Fallback ultime: alert
        alert(message);
    }
}

// ========== CSS pour les étoiles ==========
const starStyle = document.createElement('style');
starStyle.textContent = `
    #starRating i {
        font-size: 2rem;
        cursor: pointer;
        color: #ddd;
        transition: color 0.2s;
    }
    
    #starRating i.bi-star-fill {
        color: #ffc107;
    }
    
    #starRating i:hover {
        color: #ffc107;
    }
`;
document.head.appendChild(starStyle);

console.log('Event Actions Module loaded');
