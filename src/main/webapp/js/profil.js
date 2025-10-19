
if (typeof API_BASE === 'undefined') {
    var API_BASE = window.location.origin + '/jee-event-manager';
}

let originalProfilData = null;
let profilInitialized = false;

// ========== Initialisation ==========
function initProfilModule() {
    if (profilInitialized) return;
    
    console.log('Profil module initialized');
    profilInitialized = true;
    
    // Charger le profil
    loadProfil();
    
    // Initialiser les formulaires
    initProfilForm();
    initPasswordForm();
    
    // Password strength checker
    initPasswordStrength();
}

// Exposer la fonction d'initialisation
window.initProfilModule = initProfilModule;

// ========== Chargement Profil ==========
async function loadProfil() {
    try {
        const response = await fetch(`${API_BASE}/participant/profil`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Accept': 'application/json'
            }
        });
        
        if (!response.ok) {
            if (response.status === 401) {
                window.location.href = `${API_BASE}/login.jsp`;
                return;
            }
            throw new Error(`Erreur ${response.status}`);
        }
        
        const result = await response.json();
        
        if (result.success && result.data) {
            originalProfilData = result.data;
            fillProfilForm(result.data);
        } else {
            showToast('Erreur lors du chargement du profil', 'error');
        }
        
    } catch (error) {
        console.error('Erreur loadProfil:', error);
        showToast('Impossible de charger le profil', 'error');
    }
}

// ========== Remplir Formulaire ==========
function fillProfilForm(data) {
    document.getElementById('profilNom').value = data.nom || '';
    document.getElementById('profilEmail').value = data.email || '';
    
    // Date de création
    if (data.createdAt) {
        const date = new Date(data.createdAt);
        document.getElementById('profilCreatedAt').value = 
            date.toLocaleDateString('fr-FR', {
                day: 'numeric',
                month: 'long',
                year: 'numeric'
            });
    }
}

// ========== Formulaire Profil ==========
function initProfilForm() {
    const form = document.getElementById('formProfil');
    const btnSave = document.getElementById('btnSaveProfil');
    const btnCancel = document.getElementById('btnCancelProfil');
    
    form?.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        // Validation
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;
        }
        
        const nom = document.getElementById('profilNom').value.trim();
        const email = document.getElementById('profilEmail').value.trim();
        
        // Vérifier si des changements ont été faits
        if (nom === originalProfilData.nom && email === originalProfilData.email) {
            showToast('Aucune modification détectée', 'info');
            return;
        }
        
        // Désactiver le bouton
        btnSave.disabled = true;
        btnSave.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Enregistrement...';
        
        try {
            const response = await fetch(`${API_BASE}/participant/profil`, {
                method: 'PUT',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify({ nom, email })
            });
            
            const result = await response.json();
            
            if (response.ok && result.success) {
                originalProfilData = result.data;
                showToast('Profil mis à jour avec succès ! ✓', 'success');
                form.classList.remove('was-validated');
                
                // Recharger après 1 seconde
                setTimeout(() => {
                    window.location.reload();
                }, 1500);
                
            } else {
                showToast(result.message || 'Erreur lors de la mise à jour', 'error');
            }
            
        } catch (error) {
            console.error('Erreur update profil:', error);
            showToast('Erreur de connexion au serveur', 'error');
        } finally {
            btnSave.disabled = false;
            btnSave.innerHTML = '<i class="bi bi-check-circle me-1"></i>Enregistrer les modifications';
        }
    });
    
    // Bouton annuler
    btnCancel?.addEventListener('click', function() {
        if (originalProfilData) {
            fillProfilForm(originalProfilData);
            form.classList.remove('was-validated');
        }
    });
}

// ========== Formulaire Mot de Passe ==========
function initPasswordForm() {
    const form = document.getElementById('formPassword');
    const btnChange = document.getElementById('btnChangePassword');
    
    form?.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const ancienMotDePasse = document.getElementById('ancienMotDePasse').value;
        const nouveauMotDePasse = document.getElementById('nouveauMotDePasse').value;
        const confirmation = document.getElementById('confirmationMotDePasse').value;
        
        // Validation
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;
        }
        
        // Vérifier que les mots de passe correspondent
        if (nouveauMotDePasse !== confirmation) {
            document.getElementById('confirmationMotDePasse').setCustomValidity('Les mots de passe ne correspondent pas');
            form.classList.add('was-validated');
            return;
        }
        
        // Vérifier la longueur
        if (nouveauMotDePasse.length < 6) {
            showToast('Le mot de passe doit contenir au moins 6 caractères', 'warning');
            return;
        }
        
        // Désactiver le bouton
        btnChange.disabled = true;
        btnChange.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Changement...';
        
        try {
            const response = await fetch(`${API_BASE}/participant/mot-de-passe`, {
                method: 'PUT',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify({
                    ancienMotDePasse,
                    nouveauMotDePasse,
                    confirmation
                })
            });
            
            const result = await response.json();
            
            if (response.ok && result.success) {
                showToast('Mot de passe modifié avec succès ! ✓', 'success');
                form.reset();
                form.classList.remove('was-validated');
                
                // Réinitialiser la barre de force
                updatePasswordStrength('');
                
            } else {
                showToast(result.message || 'Erreur lors du changement de mot de passe', 'error');
            }
            
        } catch (error) {
            console.error('Erreur change password:', error);
            showToast('Erreur de connexion au serveur', 'error');
        } finally {
            btnChange.disabled = false;
            btnChange.innerHTML = '<i class="bi bi-shield-check me-1"></i>Changer le mot de passe';
        }
    });
    
    // Validation en temps réel de la confirmation
    document.getElementById('confirmationMotDePasse')?.addEventListener('input', function() {
        const nouveauMotDePasse = document.getElementById('nouveauMotDePasse').value;
        if (this.value === nouveauMotDePasse) {
            this.setCustomValidity('');
        } else {
            this.setCustomValidity('Les mots de passe ne correspondent pas');
        }
    });
}

// ========== Password Strength ==========
function initPasswordStrength() {
    const passwordInput = document.getElementById('nouveauMotDePasse');
    
    passwordInput?.addEventListener('input', function() {
        updatePasswordStrength(this.value);
    });
}

function updatePasswordStrength(password) {
    const bar = document.getElementById('passwordStrengthBar');
    const text = document.getElementById('passwordStrengthText');
    
    if (!bar || !text) return;
    
    if (!password) {
        bar.style.width = '0%';
        bar.className = 'progress-bar';
        text.textContent = '';
        return;
    }
    
    let strength = 0;
    let label = '';
    let colorClass = '';
    
    // Critères de force
    if (password.length >= 6) strength += 20;
    if (password.length >= 8) strength += 20;
    if (password.length >= 12) strength += 10;
    if (/[a-z]/.test(password)) strength += 10;
    if (/[A-Z]/.test(password)) strength += 15;
    if (/[0-9]/.test(password)) strength += 15;
    if (/[^a-zA-Z0-9]/.test(password)) strength += 10;
    
    // Déterminer le label et la couleur
    if (strength < 40) {
        label = 'Faible';
        colorClass = 'bg-danger';
    } else if (strength < 60) {
        label = 'Moyen';
        colorClass = 'bg-warning';
    } else if (strength < 80) {
        label = 'Bon';
        colorClass = 'bg-info';
    } else {
        label = 'Fort';
        colorClass = 'bg-success';
    }
    
    bar.style.width = strength + '%';
    bar.className = 'progress-bar ' + colorClass;
    text.textContent = 'Force du mot de passe : ' + label;
    text.className = 'text-muted small';
}

// ========== Toggle Password Visibility ==========
function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    const button = input.nextElementSibling;
    const icon = button.querySelector('i');
    
    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.remove('bi-eye');
        icon.classList.add('bi-eye-slash');
    } else {
        input.type = 'password';
        icon.classList.remove('bi-eye-slash');
        icon.classList.add('bi-eye');
    }
}

// ========== Chargement Statistiques ==========
function loadStatistiques() {
    try {
        // Utiliser les données déjà chargées par le dashboard
        if (window.serverData && window.serverData.statistiques) {
            const stats = window.serverData.statistiques;
            
            document.getElementById('statTotalInscriptions').textContent = 
                stats.nombreInscriptionsActives || 0;
            document.getElementById('statEvenementsParticipes').textContent = 
                stats.nombreEvenementsParticipes || 0;
            document.getElementById('statCommentaires').textContent = 
                stats.nombreCommentaires || 0;
            document.getElementById('statEvaluations').textContent = 
                stats.nombreEvaluations || 0;
        }
    } catch (error) {
        console.error('Erreur loadStatistiques:', error);
    }
}

// Charger les statistiques après le profil
setTimeout(loadStatistiques, 500);

// ========== Toast Notifications ==========
function showToast(message, type = 'info') {
    const toast = document.getElementById('toastNotification');
    const toastBody = document.getElementById('toastMessage');
    
    if (toast && toastBody) {
        toastBody.textContent = message;
        
        // Changer la couleur selon le type
        toast.classList.remove('bg-success', 'bg-danger', 'bg-warning', 'bg-info');
        if (type === 'success') toast.classList.add('bg-success', 'text-white');
        else if (type === 'error') toast.classList.add('bg-danger', 'text-white');
        else if (type === 'warning') toast.classList.add('bg-warning');
        
        const bsToast = new bootstrap.Toast(toast);
        bsToast.show();
    }
}

// ========== Export ==========
window.togglePassword = togglePassword;
