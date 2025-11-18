<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mon Profil - Organisateur | EventManager</title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <%@ include file="/WEB-INF/jspf/theme-head.jspf" %>
    <!-- Theme CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/organizer-theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
</head>
<body>
    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-lg-9">
                <!-- Header -->
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2 class="h3 mb-0">
                        <i class="bi bi-person-badge text-primary me-2"></i>Mon Profil Organisateur
                    </h2>
                    <a href="${pageContext.request.contextPath}/organizer/dashboard" 
                       class="btn btn-outline-secondary">
                        <i class="bi bi-arrow-left me-1"></i>Retour au dashboard
                    </a>
                </div>
                
                <!-- Informations Personnelles -->
                <div class="card shadow-sm mb-4">
                    <div class="card-header bg-white py-3">
                        <h5 class="mb-0">
                            <i class="bi bi-person me-2"></i>Informations personnelles
                        </h5>
                    </div>
                    <div class="card-body">
                        <form id="formProfil">
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="profilNom" class="form-label">
                                        Nom complet <span class="text-danger">*</span>
                                    </label>
                                    <input type="text" class="form-control" id="profilNom" 
                                           placeholder="Votre nom" required>
                                    <div class="invalid-feedback">Le nom est requis</div>
                                </div>
                                
                                <div class="col-md-6 mb-3">
                                    <label for="profilEmail" class="form-label">
                                        Adresse email <span class="text-danger">*</span>
                                    </label>
                                    <input type="email" class="form-control" id="profilEmail" 
                                           placeholder="votre@email.com" required>
                                    <div class="invalid-feedback">Email invalide</div>
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="profilEntreprise" class="form-label">
                                        Nom de l'entreprise
                                    </label>
                                    <input type="text" class="form-control" id="profilEntreprise" 
                                           placeholder="Nom de votre entreprise">
                                </div>
                                
                                <div class="col-md-6 mb-3">
                                    <label for="profilSiret" class="form-label">
                                        SIRET
                                    </label>
                                    <input type="text" class="form-control" id="profilSiret" 
                                           placeholder="Numéro SIRET" maxlength="14">
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="profilSiteWeb" class="form-label">
                                    Site web
                                </label>
                                <input type="url" class="form-control" id="profilSiteWeb" 
                                       placeholder="https://www.votre-site.com">
                            </div>
                            
                            <div class="mb-3">
                                <label for="profilDescription" class="form-label">
                                    Description
                                </label>
                                <textarea class="form-control" id="profilDescription" rows="4"
                                          placeholder="Décrivez votre entreprise ou vos activités..."></textarea>
                                <small class="text-muted">Cette description sera visible sur vos événements</small>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">Membre depuis</label>
                                <input type="text" class="form-control" id="profilCreatedAt" 
                                       readonly disabled>
                            </div>
                            
                            <div class="alert alert-info">
                                <i class="bi bi-info-circle me-2"></i>
                                <small>Vos informations sont sécurisées et ne seront jamais partagées</small>
                            </div>
                            
                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-primary" id="btnSaveProfil">
                                    <i class="bi bi-check-circle me-1"></i>Enregistrer les modifications
                                </button>
                                <button type="button" class="btn btn-outline-secondary" id="btnCancelProfil">
                                    <i class="bi bi-x-circle me-1"></i>Annuler
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
                
                <!-- Changement de mot de passe -->
                <div class="card shadow-sm mb-4">
                    <div class="card-header bg-white py-3">
                        <h5 class="mb-0">
                            <i class="bi bi-shield-lock me-2"></i>Sécurité
                        </h5>
                    </div>
                    <div class="card-body">
                        <form id="formPassword">
                            <div class="mb-3">
                                <label for="ancienMotDePasse" class="form-label">
                                    Ancien mot de passe <span class="text-danger">*</span>
                                </label>
                                <div class="input-group">
                                    <input type="password" class="form-control" id="ancienMotDePasse" 
                                           placeholder="••••••••" required>
                                    <button class="btn btn-outline-secondary" type="button" 
                                            onclick="togglePasswordVisibility('ancienMotDePasse', this)">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                                <div class="invalid-feedback">L'ancien mot de passe est requis</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="nouveauMotDePasse" class="form-label">
                                    Nouveau mot de passe <span class="text-danger">*</span>
                                </label>
                                <div class="input-group">
                                    <input type="password" class="form-control" id="nouveauMotDePasse" 
                                           placeholder="••••••••" required minlength="8">
                                    <button class="btn btn-outline-secondary" type="button" 
                                            onclick="togglePasswordVisibility('nouveauMotDePasse', this)">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                                <div class="form-text">Minimum 8 caractères</div>
                                <div class="invalid-feedback">Le nouveau mot de passe doit contenir au moins 8 caractères</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="confirmMotDePasse" class="form-label">
                                    Confirmer le nouveau mot de passe <span class="text-danger">*</span>
                                </label>
                                <div class="input-group">
                                    <input type="password" class="form-control" id="confirmMotDePasse" 
                                           placeholder="••••••••" required>
                                    <button class="btn btn-outline-secondary" type="button" 
                                            onclick="togglePasswordVisibility('confirmMotDePasse', this)">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                                <div class="invalid-feedback">Les mots de passe ne correspondent pas</div>
                            </div>
                            
                            <div class="alert alert-warning">
                                <i class="bi bi-exclamation-triangle me-2"></i>
                                <small>Assurez-vous de choisir un mot de passe fort et unique</small>
                            </div>
                            
                            <button type="submit" class="btn btn-warning" id="btnChangePassword">
                                <i class="bi bi-shield-check me-1"></i>Changer le mot de passe
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Toast notifications -->
    <div class="toast-container position-fixed bottom-0 end-0 p-3">
        <div id="successToast" class="toast align-items-center text-bg-success border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="bi bi-check-circle me-2"></i><span id="successMessage"></span>
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
        
        <div id="errorToast" class="toast align-items-center text-bg-danger border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="bi bi-exclamation-circle me-2"></i><span id="errorMessage"></span>
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Custom JS -->
    <script src="${pageContext.request.contextPath}/js/config.js"></script>
    <script>
        const API_BASE_URL = CONFIG.API_BASE_URL;
        let originalData = {};
        
        // Toggle password visibility
        function togglePasswordVisibility(inputId, button) {
            const input = document.getElementById(inputId);
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
        
        // Show toast notification
        function showToast(type, message) {
            const toastId = type === 'success' ? 'successToast' : 'errorToast';
            const messageId = type === 'success' ? 'successMessage' : 'errorMessage';
            
            document.getElementById(messageId).textContent = message;
            const toast = new bootstrap.Toast(document.getElementById(toastId));
            toast.show();
        }
        
        // Load profile data
        async function loadProfile() {
            try {
                const response = await fetch(`${API_BASE_URL}/organizer/profil`);
                const data = await response.json();
                
                if (data.success && data.data) {
                    const profil = data.data;
                    originalData = { ...profil };
                    
                    document.getElementById('profilNom').value = profil.nom || '';
                    document.getElementById('profilEmail').value = profil.email || '';
                    document.getElementById('profilEntreprise').value = profil.entreprise || '';
                    document.getElementById('profilSiret').value = profil.siret || '';
                    document.getElementById('profilSiteWeb').value = profil.siteWeb || '';
                    document.getElementById('profilDescription').value = profil.description || '';
                    document.getElementById('profilCreatedAt').value = new Date(profil.createdAt).toLocaleDateString('fr-FR');
                } else {
                    showToast('error', data.message || 'Erreur lors du chargement du profil');
                }
            } catch (error) {
                console.error('Error loading profile:', error);
                showToast('error', 'Erreur de connexion');
            }
        }
        
        // Handle profile form submission
        document.getElementById('formProfil').addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const btnSave = document.getElementById('btnSaveProfil');
            const originalText = btnSave.innerHTML;
            btnSave.disabled = true;
            btnSave.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Enregistrement...';
            
            try {
                const updates = {
                    nom: document.getElementById('profilNom').value,
                    email: document.getElementById('profilEmail').value,
                    entreprise: document.getElementById('profilEntreprise').value,
                    siret: document.getElementById('profilSiret').value,
                    siteWeb: document.getElementById('profilSiteWeb').value,
                    description: document.getElementById('profilDescription').value
                };
                
                const response = await fetch(`${API_BASE_URL}/organizer/profil`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(updates)
                });
                
                const data = await response.json();
                
                if (data.success) {
                    showToast('success', 'Profil mis à jour avec succès');
                    originalData = { ...updates };
                } else {
                    showToast('error', data.message || 'Erreur lors de la mise à jour');
                }
            } catch (error) {
                console.error('Error updating profile:', error);
                showToast('error', 'Erreur de connexion');
            } finally {
                btnSave.disabled = false;
                btnSave.innerHTML = originalText;
            }
        });
        
        // Handle cancel button
        document.getElementById('btnCancelProfil').addEventListener('click', () => {
            document.getElementById('profilNom').value = originalData.nom || '';
            document.getElementById('profilEmail').value = originalData.email || '';
            document.getElementById('profilEntreprise').value = originalData.entreprise || '';
            document.getElementById('profilSiret').value = originalData.siret || '';
            document.getElementById('profilSiteWeb').value = originalData.siteWeb || '';
            document.getElementById('profilDescription').value = originalData.description || '';
        });
        
        // Handle password change
        document.getElementById('formPassword').addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const currentPassword = document.getElementById('ancienMotDePasse').value;
            const newPassword = document.getElementById('nouveauMotDePasse').value;
            const confirmPassword = document.getElementById('confirmMotDePasse').value;
            
            if (newPassword !== confirmPassword) {
                showToast('error', 'Les mots de passe ne correspondent pas');
                return;
            }
            
            const btnChange = document.getElementById('btnChangePassword');
            const originalText = btnChange.innerHTML;
            btnChange.disabled = true;
            btnChange.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Modification...';
            
            try {
                const response = await fetch(`${API_BASE_URL}/organizer/mot-de-passe`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ currentPassword, newPassword, confirmPassword })
                });
                
                const data = await response.json();
                
                if (data.success) {
                    showToast('success', 'Mot de passe modifié avec succès');
                    document.getElementById('formPassword').reset();
                } else {
                    showToast('error', data.message || 'Erreur lors de la modification');
                }
            } catch (error) {
                console.error('Error changing password:', error);
                showToast('error', 'Erreur de connexion');
            } finally {
                btnChange.disabled = false;
                btnChange.innerHTML = originalText;
            }
        });
        
        // Load profile on page load
        document.addEventListener('DOMContentLoaded', () => {
            loadProfile();
        });
    </script>
</body>
</html>
