<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mon Profil - EventManager</title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
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
                        <i class="bi bi-person-circle text-primary me-2"></i>Mon Profil
                    </h2>
                    <a href="${pageContext.request.contextPath}/participant-dashboard.jsp" 
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
                                            onclick="togglePassword('ancienMotDePasse')">
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
                                           placeholder="••••••••" required minlength="6">
                                    <button class="btn btn-outline-secondary" type="button" 
                                            onclick="togglePassword('nouveauMotDePasse')">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                                <div class="form-text">Minimum 6 caractères</div>
                                <div class="invalid-feedback">Le mot de passe doit contenir au moins 6 caractères</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="confirmationMotDePasse" class="form-label">
                                    Confirmer le nouveau mot de passe <span class="text-danger">*</span>
                                </label>
                                <div class="input-group">
                                    <input type="password" class="form-control" id="confirmationMotDePasse" 
                                           placeholder="••••••••" required minlength="6">
                                    <button class="btn btn-outline-secondary" type="button" 
                                            onclick="togglePassword('confirmationMotDePasse')">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                                <div class="invalid-feedback" id="passwordMismatch">
                                    Les mots de passe ne correspondent pas
                                </div>
                            </div>
                            
                            <!-- Password strength indicator -->
                            <div class="mb-3">
                                <div class="password-strength">
                                    <div class="progress" style="height: 5px;">
                                        <div class="progress-bar" id="passwordStrengthBar" 
                                             role="progressbar" style="width: 0%"></div>
                                    </div>
                                    <small class="text-muted" id="passwordStrengthText"></small>
                                </div>
                            </div>
                            
                            <div class="alert alert-warning">
                                <i class="bi bi-exclamation-triangle me-2"></i>
                                <small>Assurez-vous d'utiliser un mot de passe fort et unique</small>
                            </div>
                            
                            <button type="submit" class="btn btn-primary" id="btnChangePassword">
                                <i class="bi bi-shield-check me-1"></i>Changer le mot de passe
                            </button>
                        </form>
                    </div>
                </div>
                
            </div>
        </div>
    </div>
    
    <!-- Toast Container -->
    <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 9999;">
        <div id="toastNotification" class="toast" role="alert">
            <div class="toast-header">
                <i class="bi bi-bell-fill me-2 text-primary"></i>
                <strong class="me-auto">Notification</strong>
                <button type="button" class="btn-close" data-bs-dismiss="toast"></button>
            </div>
            <div class="toast-body" id="toastMessage"></div>
        </div>
    </div>
    
    <!-- Bootstrap 5 JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Custom JavaScript -->
    <script src="${pageContext.request.contextPath}/js/profil.js"></script>
</body>
</html>
