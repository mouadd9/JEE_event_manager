<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mon Dashboard - EventManager</title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <!-- FullCalendar CSS -->
    <link href="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.10/index.global.min.css" rel="stylesheet">
    <%@ include file="/WEB-INF/jspf/theme-head.jspf" %>
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
</head>
<body>
    <!-- Header -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary sticky-top shadow-sm">
        <div class="container-fluid">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/participant-dashboard.jsp">
                <i class="bi bi-calendar-event me-2"></i>EventHub
            </a>
            
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto align-items-center">
                    <li class="nav-item">
                        <a class="nav-link active" href="#evenements" data-section="evenements">
                            <i class="bi bi-grid me-1"></i>Événements
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#calendrier" data-section="calendrier">
                            <i class="bi bi-calendar3 me-1"></i>Calendrier
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#inscriptions" data-section="inscriptions">
                            <i class="bi bi-ticket-perforated me-1"></i>Mes Inscriptions
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#profil" data-section="profil">
                            <i class="bi bi-person-circle me-1"></i>Profil
                        </a>
                    </li>
                    <li class="nav-item dropdown ms-3">
                        <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" id="userDropdown" 
                           role="button" data-bs-toggle="dropdown">
                            <div class="user-avatar me-2">
                                <i class="bi bi-person-fill"></i>
                            </div>
                            <span id="userName">Chargement...</span>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li><a class="dropdown-item" href="#profil" data-section="profil">
                                <i class="bi bi-person me-2"></i>Mon Profil
                            </a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">
                                <i class="bi bi-box-arrow-right me-2"></i>Déconnexion
                            </a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container-fluid py-4">
        <!-- Welcome Banner -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="welcome-banner bg-gradient-primary text-white rounded-3 p-4 shadow-sm">
                    <h1 class="h3 mb-2">Bienvenue, <span id="welcomeName">Participant</span>!</h1>
                    <p class="mb-0 opacity-90">Découvrez les événements à venir et gérez vos inscriptions</p>
                </div>
            </div>
        </div>

        <!-- Statistics Cards -->
        <div class="row g-3 mb-4" id="statsCards">
            <div class="col-md-3 col-sm-6">
                <div class="stat-card bg-white rounded-3 p-3 shadow-sm h-100">
                    <div class="d-flex align-items-center">
                        <div class="stat-icon bg-primary bg-opacity-10 text-primary rounded-circle p-3 me-3">
                            <i class="bi bi-ticket-perforated fs-4"></i>
                        </div>
                        <div>
                            <div class="stat-value fs-3 fw-bold" id="statInscriptionsActives">0</div>
                            <div class="stat-label text-muted small">Inscriptions actives</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3 col-sm-6">
                <div class="stat-card bg-white rounded-3 p-3 shadow-sm h-100">
                    <div class="d-flex align-items-center">
                        <div class="stat-icon bg-success bg-opacity-10 text-success rounded-circle p-3 me-3">
                            <i class="bi bi-check-circle fs-4"></i>
                        </div>
                        <div>
                            <div class="stat-value fs-3 fw-bold" id="statEvenementsParticipes">0</div>
                            <div class="stat-label text-muted small">Événements participés</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3 col-sm-6">
                <div class="stat-card bg-white rounded-3 p-3 shadow-sm h-100">
                    <div class="d-flex align-items-center">
                        <div class="stat-icon bg-warning bg-opacity-10 text-warning rounded-circle p-3 me-3">
                            <i class="bi bi-chat-dots fs-4"></i>
                        </div>
                        <div>
                            <div class="stat-value fs-3 fw-bold" id="statCommentaires">0</div>
                            <div class="stat-label text-muted small">Commentaires</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3 col-sm-6">
                <div class="stat-card bg-white rounded-3 p-3 shadow-sm h-100">
                    <div class="d-flex align-items-center">
                        <div class="stat-icon bg-info bg-opacity-10 text-info rounded-circle p-3 me-3">
                            <i class="bi bi-star fs-4"></i>
                        </div>
                        <div>
                            <div class="stat-value fs-3 fw-bold" id="statEvaluations">0</div>
                            <div class="stat-label text-muted small">Évaluations</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Section: Calendrier -->
        <section id="section-calendrier" class="content-section d-none">
            <div class="section-header d-flex justify-content-between align-items-center mb-4">
                <h2 class="h4 mb-0">
                    <i class="bi bi-calendar3 text-primary me-2"></i>Mon Calendrier
                </h2>
                <button class="btn btn-outline-primary btn-sm" id="btnRefreshCalendar">
                    <i class="bi bi-arrow-clockwise me-1"></i>Actualiser
                </button>
            </div>

            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <div id="calendarContainer">
                        <div id="calendar" style="min-height: 600px;"></div>
                    </div>
                </div>
            </div>

            <!-- Légende -->
            <div class="card shadow-sm">
                <div class="card-body">
                    <h6 class="mb-3"><i class="bi bi-info-circle me-2"></i>Légende</h6>
                    <div class="d-flex flex-wrap gap-3">
                        <div class="d-flex align-items-center">
                            <span class="badge bg-success me-2" style="width: 20px; height: 20px;"></span>
                            <small>Événement accepté</small>
                        </div>
                        <div class="d-flex align-items-center">
                            <span class="badge bg-warning me-2" style="width: 20px; height: 20px;"></span>
                            <small>En attente</small>
                        </div>
                        <div class="d-flex align-items-center">
                            <span class="badge bg-secondary me-2" style="width: 20px; height: 20px;"></span>
                            <small>Annulé</small>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Section: Événements Disponibles -->
        <section id="section-evenements" class="content-section">
            <div class="section-header d-flex justify-content-between align-items-center mb-4">
                <h2 class="h4 mb-0">
                    <i class="bi bi-calendar-event text-primary me-2"></i>Événements Disponibles
                </h2>
                <button class="btn btn-outline-primary btn-sm" id="btnRefreshEvents">
                    <i class="bi bi-arrow-clockwise me-1"></i>Actualiser
                </button>
            </div>

            <!-- Filters -->
            <div class="card mb-4 shadow-sm">
                <div class="card-body">
                    <div class="row g-3">
                        <div class="col-md-4">
                            <label class="form-label small text-muted">Rechercher</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-search"></i></span>
                                <input type="text" class="form-control" id="searchEvents" 
                                       placeholder="Titre, lieu...">
                            </div>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label small text-muted">Catégorie</label>
                            <select class="form-select" id="filterCategorie">
                                <option value="">Toutes les catégories</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label small text-muted">Date</label>
                            <input type="date" class="form-control" id="filterDate">
                        </div>
                        <div class="col-md-2 d-flex align-items-end">
                            <button class="btn btn-outline-secondary w-100" id="btnClearFilters">
                                <i class="bi bi-x-circle me-1"></i>Effacer
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Events Grid -->
            <div id="eventsGrid" class="row g-4">
                <!-- Loading spinner -->
                <div class="col-12 text-center py-5" id="eventsLoading">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Chargement...</span>
                    </div>
                    <p class="text-muted mt-3">Chargement des événements...</p>
                </div>
            </div>

            <!-- Empty state -->
            <div id="eventsEmpty" class="text-center py-5 d-none">
                <i class="bi bi-calendar-x text-muted" style="font-size: 4rem;"></i>
                <h5 class="text-muted mt-3">Aucun événement disponible</h5>
                <p class="text-muted">Revenez plus tard pour découvrir de nouveaux événements</p>
            </div>
        </section>

        <!-- Section: Mes Inscriptions -->
        <section id="section-inscriptions" class="content-section d-none">
            <div class="section-header d-flex justify-content-between align-items-center mb-4">
                <h2 class="h4 mb-0">
                    <i class="bi bi-ticket-perforated text-primary me-2"></i>Mes Inscriptions
                </h2>
            </div>

            <!-- Tabs -->
            <ul class="nav nav-tabs mb-4" id="inscriptionsTabs" role="tablist">
                <li class="nav-item" role="presentation">
                    <button class="nav-link active" id="tab-actives" data-bs-toggle="tab" 
                            data-bs-target="#inscriptions-actives" type="button">
                        <i class="bi bi-clock-history me-1"></i>Actives 
                        <span class="badge bg-primary ms-1" id="countActives">0</span>
                    </button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link" id="tab-passees" data-bs-toggle="tab" 
                            data-bs-target="#inscriptions-passees" type="button">
                        <i class="bi bi-check-circle me-1"></i>Passées 
                        <span class="badge bg-success ms-1" id="countPassees">0</span>
                    </button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link" id="tab-annulees" data-bs-toggle="tab" 
                            data-bs-target="#inscriptions-annulees" type="button">
                        <i class="bi bi-x-circle me-1"></i>Annulées 
                        <span class="badge bg-secondary ms-1" id="countAnnulees">0</span>
                    </button>
                </li>
            </ul>

            <!-- Tab Content -->
            <div class="tab-content" id="inscriptionsTabContent">
                <!-- Actives -->
                <div class="tab-pane fade show active" id="inscriptions-actives">
                    <div id="listInscriptionsActives" class="row g-3">
                        <div class="col-12 text-center py-5">
                            <div class="spinner-border text-primary" role="status"></div>
                        </div>
                    </div>
                </div>
                <!-- Passées -->
                <div class="tab-pane fade" id="inscriptions-passees">
                    <div id="listInscriptionsPassees" class="row g-3">
                        <div class="col-12 text-center py-5">
                            <div class="spinner-border text-primary" role="status"></div>
                        </div>
                    </div>
                </div>
                <!-- Annulées -->
                <div class="tab-pane fade" id="inscriptions-annulees">
                    <div id="listInscriptionsAnnulees" class="row g-3">
                        <div class="col-12 text-center py-5">
                            <div class="spinner-border text-primary" role="status"></div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Section: Profil -->
        <section id="section-profil" class="content-section d-none">
            <div class="section-header mb-4">
                <h2 class="h4 mb-0">
                    <i class="bi bi-person-circle text-primary me-2"></i>Mon Profil
                </h2>
            </div>
            
            <div class="row">
                <div class="col-lg-8">
                    <!-- Formulaire de profil -->
                    <div class="card shadow-sm mb-4">
                        <div class="card-header bg-white">
                            <h5 class="mb-0">
                                <i class="bi bi-person-badge me-2"></i>Informations personnelles
                            </h5>
                        </div>
                        <div class="card-body">
                            <form id="formProfil" novalidate>
                                <div class="mb-3">
                                    <label for="profilNom" class="form-label">Nom complet <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="profilNom" required>
                                    <div class="invalid-feedback">Le nom est requis</div>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="profilEmail" class="form-label">Email <span class="text-danger">*</span></label>
                                    <input type="email" class="form-control" id="profilEmail" required>
                                    <div class="invalid-feedback">Un email valide est requis</div>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="profilCreatedAt" class="form-label">Membre depuis</label>
                                    <input type="text" class="form-control" id="profilCreatedAt" readonly>
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
                        <div class="card-header bg-white">
                            <h5 class="mb-0">
                                <i class="bi bi-shield-lock me-2"></i>Sécurité
                            </h5>
                        </div>
                        <div class="card-body">
                            <form id="formPassword" novalidate>
                                <div class="mb-3">
                                    <label for="ancienMotDePasse" class="form-label">Ancien mot de passe <span class="text-danger">*</span></label>
                                    <div class="input-group">
                                        <input type="password" class="form-control" id="ancienMotDePasse" autocomplete="current-password" required>
                                        <button class="btn btn-outline-secondary" type="button" onclick="togglePassword('ancienMotDePasse')">
                                            <i class="bi bi-eye"></i>
                                        </button>
                                    </div>
                                    <div class="invalid-feedback">L'ancien mot de passe est requis</div>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="nouveauMotDePasse" class="form-label">Nouveau mot de passe <span class="text-danger">*</span></label>
                                    <div class="input-group">
                                        <input type="password" class="form-control" id="nouveauMotDePasse" autocomplete="new-password" minlength="6" required>
                                        <button class="btn btn-outline-secondary" type="button" onclick="togglePassword('nouveauMotDePasse')">
                                            <i class="bi bi-eye"></i>
                                        </button>
                                    </div>
                                    <div class="invalid-feedback">Le mot de passe doit contenir au moins 6 caractères</div>
                                    <div class="progress mt-2" style="height: 5px;">
                                        <div class="progress-bar" id="passwordStrengthBar" role="progressbar" style="width: 0%"></div>
                                    </div>
                                    <div id="passwordStrengthText" class="text-muted small mt-1"></div>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="confirmationMotDePasse" class="form-label">Confirmer le mot de passe <span class="text-danger">*</span></label>
                                    <div class="input-group">
                                        <input type="password" class="form-control" id="confirmationMotDePasse" autocomplete="new-password" required>
                                        <button class="btn btn-outline-secondary" type="button" onclick="togglePassword('confirmationMotDePasse')">
                                            <i class="bi bi-eye"></i>
                                        </button>
                                    </div>
                                    <div class="invalid-feedback">Les mots de passe ne correspondent pas</div>
                                </div>
                                
                                <button type="submit" class="btn btn-primary" id="btnChangePassword">
                                    <i class="bi bi-shield-check me-1"></i>Changer le mot de passe
                                </button>
                            </form>
                        </div>
                    </div>
                    
                </div>
            </div>
        </section>
    </div>

    <!-- Modal: Event Details -->
    <div class="modal fade" id="modalEventDetails" tabindex="-1">
        <div class="modal-dialog modal-lg modal-dialog-scrollable">
            <div class="modal-content">
                <div class="modal-header border-0">
                    <h5 class="modal-title" id="modalEventTitle">Détails de l'événement</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body" id="modalEventBody">
                    <!-- Content loaded dynamically -->
                </div>
            </div>
        </div>
    </div>

    <!-- Modal: Inscription -->
    <div class="modal fade" id="modalInscription" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title">
                        <i class="bi bi-ticket-perforated me-2"></i>S'inscrire à l'événement
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div id="inscriptionEventInfo" class="mb-3"></div>
                    
                    <form id="formInscription">
                        <input type="hidden" id="inscriptionEventId">
                        
                        <div class="mb-3">
                            <label class="form-label">Type de billet <span class="text-danger">*</span></label>
                            <select class="form-select" id="inscriptionTypeBillet" required>
                                <option value="">Sélectionnez un type</option>
                                <option value="STANDARD">Standard</option>
                                <option value="VIP">VIP</option>
                                <option value="PREMIUM">Premium</option>
                            </select>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Quantité <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="inscriptionQuantite" 
                                   min="1" max="10" value="1" required>
                            <div class="form-text">Maximum 10 places par inscription</div>
                        </div>
                        
                        <div class="alert alert-info" id="inscriptionCapacite">
                            <i class="bi bi-info-circle me-2"></i>
                            <span id="capaciteText"></span>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                    <button type="button" class="btn btn-primary" id="btnConfirmInscription">
                        <i class="bi bi-check-circle me-1"></i>Confirmer l'inscription
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal: Confirm Cancel -->
    <div class="modal fade" id="modalConfirmCancel" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-danger text-white">
                    <h5 class="modal-title">
                        <i class="bi bi-exclamation-triangle me-2"></i>Confirmer l'annulation
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p>Êtes-vous sûr de vouloir annuler cette inscription ?</p>
                    <p class="text-muted mb-0">Cette action est irréversible.</p>
                    <input type="hidden" id="cancelInscriptionId">
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Non, garder</button>
                    <button type="button" class="btn btn-danger" id="btnConfirmCancel">
                        <i class="bi bi-x-circle me-1"></i>Oui, annuler
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal: Add Comment -->
    <div class="modal fade" id="modalAddComment" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-info text-white">
                    <h5 class="modal-title">
                        <i class="bi bi-chat-dots me-2"></i>Ajouter un commentaire
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div id="commentEventInfo" class="mb-3"></div>
                    
                    <form id="formComment">
                        <input type="hidden" id="commentEventId">
                        
                        <div class="mb-3">
                            <label class="form-label">Votre commentaire <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="commentTexte" rows="5" 
                                      maxlength="1000" required 
                                      placeholder="Partagez votre expérience..."></textarea>
                            <div class="form-text">
                                <span id="commentCharCount">0</span>/1000 caractères
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                    <button type="button" class="btn btn-info text-white" id="btnSubmitComment">
                        <i class="bi bi-send me-1"></i>Publier le commentaire
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal: Add Evaluation -->
    <div class="modal fade" id="modalAddEvaluation" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-warning text-dark">
                    <h5 class="modal-title">
                        <i class="bi bi-star me-2"></i>Évaluer l'événement
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div id="evaluationEventInfo" class="mb-3"></div>
                    
                    <form id="formEvaluation">
                        <input type="hidden" id="evaluationEventId">
                        
                        <div class="mb-3">
                            <label class="form-label">Note <span class="text-danger">*</span></label>
                            <div class="star-rating d-flex gap-2 fs-2" id="starRating">
                                <i class="bi bi-star" data-rating="1"></i>
                                <i class="bi bi-star" data-rating="2"></i>
                                <i class="bi bi-star" data-rating="3"></i>
                                <i class="bi bi-star" data-rating="4"></i>
                                <i class="bi bi-star" data-rating="5"></i>
                            </div>
                            <input type="hidden" id="evaluationNote" required>
                            <div class="form-text mt-2">
                                Cliquez sur les étoiles pour noter (1-5)
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Commentaire (optionnel)</label>
                            <textarea class="form-control" id="evaluationTexte" rows="4" 
                                      maxlength="500" 
                                      placeholder="Ajoutez un commentaire à votre évaluation..."></textarea>
                            <div class="form-text">
                                <span id="evaluationCharCount">0</span>/500 caractères
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                    <button type="button" class="btn btn-warning text-dark" id="btnSubmitEvaluation">
                        <i class="bi bi-check-circle me-1"></i>Soumettre l'évaluation
                    </button>
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

    <!-- Données du serveur (dans des attributs data) -->
    <div id="serverData" style="display:none;"
         data-participant-id="${participant.id}"
         data-participant-nom="${participant.nom}"
         data-participant-email="${participant.email}"
         data-evenements='<c:out value="${evenementsJSON}" escapeXml="false"/>'
         data-statistiques='<c:out value="${statistiquesJSON}" escapeXml="false"/>'
         data-inscriptions='<c:out value="${inscriptionsJSON}" escapeXml="false"/>'>
    </div>

    <!-- Bootstrap 5 JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- FullCalendar JS -->
    <script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.10/index.global.min.js"></script>
    
    <!-- Chargement des données depuis les attributs data -->
    <script>
        (function() {
            var dataElement = document.getElementById('serverData');
            if (dataElement) {
                try {
                    window.serverData = {
                        participant: {
                            id: parseInt(dataElement.getAttribute('data-participant-id')) || null,
                            nom: dataElement.getAttribute('data-participant-nom') || '',
                            email: dataElement.getAttribute('data-participant-email') || ''
                        },
                        evenementsDisponibles: JSON.parse(dataElement.getAttribute('data-evenements') || '[]'),
                        statistiques: JSON.parse(dataElement.getAttribute('data-statistiques') || '{}'),
                        inscriptions: JSON.parse(dataElement.getAttribute('data-inscriptions') || '[]')
                    };
                    console.log('ServerData chargé:', window.serverData);
                } catch (e) {
                    console.error('Erreur parsing serverData:', e);
                    window.serverData = {
                        participant: null,
                        evenementsDisponibles: [],
                        statistiques: {}
                    };
                }
            }
        })();
    </script>
    
    <!-- Configuration (doit être chargé en premier) -->
    <script src="${pageContext.request.contextPath}/js/config.js"></script>
    <!-- Custom JavaScript -->
    <script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
    <script src="${pageContext.request.contextPath}/js/calendar.js"></script>
    <script src="${pageContext.request.contextPath}/js/inscription.js"></script>
    <script src="${pageContext.request.contextPath}/js/profil.js"></script>
    <script src="${pageContext.request.contextPath}/js/commentaires.js"></script>
    <script src="${pageContext.request.contextPath}/js/evaluations.js"></script>
    <script src="${pageContext.request.contextPath}/js/event-actions.js"></script>
</body>
</html>
