<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mon Profil Admin - EventManager</title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <%@ include file="/WEB-INF/jspf/theme-head.jspf" %>
    <!-- Theme CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/theme.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
</head>
<body>
    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <!-- Header -->
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2 class="h3 mb-0">
                        <i class="bi bi-shield-lock-fill text-primary me-2"></i>Mon Profil Administrateur
                    </h2>
                    <a href="${pageContext.request.contextPath}/admin/dashboard" 
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
                            <div class="mb-3">
                                <label for="profilNom" class="form-label">
                                    Nom complet <span class="text-danger">*</span>
                                </label>
                                <input type="text" class="form-control" id="profilNom" 
                                       placeholder="Votre nom" required>
                                <div class="invalid-feedback">Le nom est requis</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="profilEmail" class="form-label">
                                    Adresse email <span class="text-danger">*</span>
                                </label>
                                <input type="email" class="form-control" id="profilEmail" 
                                       placeholder="votre@email.com" required>
                                <div class="invalid-feedback">Email invalide</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="profilRole" class="form-label">Rôle</label>
                                <input type="text" class="form-control" id="profilRole" 
                                       readonly disabled>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">Membre depuis</label>
                                <input type="text" class="form-control" id="profilCreatedAt" 
                                       readonly disabled>
                            </div>
                            
                            <div class="alert alert-info">
                                <i class="bi bi-shield-check me-2"></i>
                                <small>Compte administrateur - Accès complet au système</small>
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
                <div class="card shadow-sm">
                    <div class="card-header bg-white py-3">
                        <h5 class="mb-0">
                            <i class="bi bi-key me-2"></i>Sécurité du compte
                        </h5>
                    </div>
                    <div class="card-body">
                        <form id="formPassword">
                            <div class="mb-3">
                                <label for="currentPassword" class="form-label">
                                    Mot de passe actuel <span class="text-danger">*</span>
                                </label>
                                <div class="input-group">
                                    <input type="password" class="form-control" id="currentPassword" required>
                                    <button class="btn btn-outline-secondary" type="button" 
                                            onclick="togglePassword('currentPassword')">
                                        <i class="bi bi-eye" id="currentPasswordIcon"></i>
                                    </button>
                                </div>
                                <div class="invalid-feedback">Mot de passe actuel requis</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="newPassword" class="form-label">
                                    Nouveau mot de passe <span class="text-danger">*</span>
                                </label>
                                <div class="input-group">
                                    <input type="password" class="form-control" id="newPassword" 
                                           minlength="8" required>
                                    <button class="btn btn-outline-secondary" type="button" 
                                            onclick="togglePassword('newPassword')">
                                        <i class="bi bi-eye" id="newPasswordIcon"></i>
                                    </button>
                                </div>
                                <div class="form-text">Minimum 8 caractères</div>
                                <div class="invalid-feedback">Le nouveau mot de passe doit contenir au moins 8 caractères</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="confirmPassword" class="form-label">
                                    Confirmer le nouveau mot de passe <span class="text-danger">*</span>
                                </label>
                                <div class="input-group">
                                    <input type="password" class="form-control" id="confirmPassword" 
                                           minlength="8" required>
                                    <button class="btn btn-outline-secondary" type="button" 
                                            onclick="togglePassword('confirmPassword')">
                                        <i class="bi bi-eye" id="confirmPasswordIcon"></i>
                                    </button>
                                </div>
                                <div class="invalid-feedback">Les mots de passe ne correspondent pas</div>
                            </div>
                            
                            <div class="alert alert-warning">
                                <i class="bi bi-exclamation-triangle me-2"></i>
                                <small>
                                    <strong>Important:</strong> Après avoir changé votre mot de passe, vous devrez vous reconnecter.
                                </small>
                            </div>
                            
                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-primary" id="btnChangePassword">
                                    <i class="bi bi-shield-lock me-1"></i>Changer le mot de passe
                                </button>
                                <button type="button" class="btn btn-outline-secondary" id="btnCancelPassword">
                                    <i class="bi bi-x-circle me-1"></i>Annuler
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Toast Notifications -->
    <div class="toast-container position-fixed top-0 end-0 p-3">
        <div id="successToast" class="toast align-items-center text-white bg-success border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="bi bi-check-circle me-2"></i>
                    <span id="successMessage"></span>
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
        <div id="errorToast" class="toast align-items-center text-white bg-danger border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <span id="errorMessage"></span>
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Profile Script -->
    <script>
        const API_BASE = '${pageContext.request.contextPath}/admin';
        let originalData = {};
        
        // Toggle password visibility
        function togglePassword(inputId) {
            const input = document.getElementById(inputId);
            const icon = document.getElementById(inputId + 'Icon');
            
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
            const toastElement = document.getElementById(type + 'Toast');
            const messageElement = document.getElementById(type + 'Message');
            messageElement.textContent = message;
            
            const toast = new bootstrap.Toast(toastElement);
            toast.show();
        }
        
        // Load profile data
        async function loadProfile() {
            try {
                const response = await fetch(`\${API_BASE}/profil`);
                const data = await response.json();
                
                if (data.success && data.data) {
                    const profil = data.data;
                    originalData = profil;
                    
                    document.getElementById('profilNom').value = profil.nom || '';
                    document.getElementById('profilEmail').value = profil.email || '';
                    document.getElementById('profilRole').value = profil.role || 'ADMIN';
                    document.getElementById('profilCreatedAt').value = profil.createdAt ? 
                        new Date(profil.createdAt).toLocaleDateString('fr-FR') : '';
                }
            } catch (error) {
                console.error('Erreur lors du chargement du profil:', error);
                showToast('error', 'Erreur lors du chargement du profil');
            }
        }
        
        // Save profile
        document.getElementById('formProfil').addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const nom = document.getElementById('profilNom').value.trim();
            const email = document.getElementById('profilEmail').value.trim();
            
            if (!nom || !email) {
                showToast('error', 'Veuillez remplir tous les champs obligatoires');
                return;
            }
            
            const btnSave = document.getElementById('btnSaveProfil');
            const originalText = btnSave.innerHTML;
            btnSave.disabled = true;
            btnSave.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Enregistrement...';
            
            try {
                const response = await fetch(`\${API_BASE}/profil`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ nom, email })
                });
                
                const data = await response.json();
                
                if (data.success) {
                    showToast('success', 'Profil mis à jour avec succès');
                    await loadProfile();
                } else {
                    showToast('error', data.message || 'Erreur lors de la mise à jour');
                }
            } catch (error) {
                console.error('Erreur:', error);
                showToast('error', 'Erreur lors de la mise à jour du profil');
            } finally {
                btnSave.disabled = false;
                btnSave.innerHTML = originalText;
            }
        });
        
        // Cancel profile changes
        document.getElementById('btnCancelProfil').addEventListener('click', () => {
            if (originalData) {
                document.getElementById('profilNom').value = originalData.nom || '';
                document.getElementById('profilEmail').value = originalData.email || '';
            }
        });
        
        // Change password
        document.getElementById('formPassword').addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const currentPassword = document.getElementById('currentPassword').value;
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            if (newPassword !== confirmPassword) {
                showToast('error', 'Les mots de passe ne correspondent pas');
                return;
            }
            
            if (newPassword.length < 8) {
                showToast('error', 'Le mot de passe doit contenir au moins 8 caractères');
                return;
            }
            
            const btnChange = document.getElementById('btnChangePassword');
            const originalText = btnChange.innerHTML;
            btnChange.disabled = true;
            btnChange.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Modification...';
            
            try {
                const response = await fetch(`\${API_BASE}/mot-de-passe`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ currentPassword, newPassword, confirmPassword })
                });
                
                const data = await response.json();
                
                if (data.success) {
                    showToast('success', 'Mot de passe modifié avec succès. Redirection...');
                    document.getElementById('formPassword').reset();
                    
                    // Redirect to login after 2 seconds
                    setTimeout(() => {
                        window.location.href = '${pageContext.request.contextPath}/login';
                    }, 2000);
                } else {
                    showToast('error', data.message || 'Erreur lors du changement de mot de passe');
                }
            } catch (error) {
                console.error('Erreur:', error);
                showToast('error', 'Erreur lors du changement de mot de passe');
            } finally {
                btnChange.disabled = false;
                btnChange.innerHTML = originalText;
            }
        });
        
        // Cancel password change
        document.getElementById('btnCancelPassword').addEventListener('click', () => {
            document.getElementById('formPassword').reset();
        });
        
        // Load profile on page load
        document.addEventListener('DOMContentLoaded', loadProfile);
    </script>
</body>
</html>
