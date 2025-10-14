<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Découvrez les meilleurs événements - EventManager</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Neue+Plak:wght@400;600;700&display=swap" rel="stylesheet">
    
    <style>
        :root {
            --primary-color: #d1410c;
            --secondary-color: #1e0a3c;
            --accent-color: #f05537;
            --text-dark: #39364f;
            --text-light: #6f7287;
            --bg-light: #f8f7fa;
            --border-color: #e8e7ed;
        }
        
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Neue Plak', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            color: var(--text-dark);
            background-color: #fff;
            line-height: 1.6;
        }
        
        /* Header */
        .navbar {
            background: #fff;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            padding: 1rem 0;
        }
        
        .navbar-brand {
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--primary-color) !important;
        }
        
        .nav-link {
            color: var(--text-dark) !important;
            font-weight: 500;
            margin: 0 0.5rem;
            transition: color 0.3s;
        }
        
        .nav-link:hover {
            color: var(--primary-color) !important;
        }
        
        .btn-create {
            background: var(--primary-color);
            color: white;
            border: none;
            padding: 0.6rem 1.5rem;
            border-radius: 8px;
            font-weight: 600;
            transition: all 0.3s;
        }
        
        .btn-create:hover {
            background: var(--accent-color);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(209, 65, 12, 0.3);
        }
        
        /* Hero Section */
        .hero-section {
            background: linear-gradient(135deg, #6e48aa 0%, #9d50bb 100%);
            color: white;
            padding: 4rem 0;
            margin-bottom: 3rem;
        }
        
        .hero-title {
            font-size: 3rem;
            font-weight: 700;
            margin-bottom: 1rem;
        }
        
        .hero-subtitle {
            font-size: 1.3rem;
            opacity: 0.95;
            margin-bottom: 2rem;
        }
        
        /* Search Bar */
        .search-container {
            background: white;
            border-radius: 12px;
            padding: 1.5rem;
            box-shadow: 0 8px 24px rgba(0,0,0,0.12);
            margin-top: -3rem;
            position: relative;
            z-index: 10;
        }
        
        .search-input {
            border: 2px solid var(--border-color);
            border-radius: 8px;
            padding: 0.8rem 1rem;
            font-size: 1rem;
            transition: all 0.3s;
        }
        
        .search-input:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 3px rgba(209, 65, 12, 0.1);
            outline: none;
        }
        
        .btn-search {
            background: var(--primary-color);
            color: white;
            border: none;
            padding: 0.8rem 2rem;
            border-radius: 8px;
            font-weight: 600;
            transition: all 0.3s;
            width: 100%;
        }
        
        .btn-search:hover {
            background: var(--accent-color);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(209, 65, 12, 0.3);
        }
        
        /* Filters */
        .filters-section {
            margin: 2rem 0;
            padding: 1rem 0;
        }
        
        .filter-chip {
            display: inline-block;
            padding: 0.5rem 1.2rem;
            margin: 0.3rem;
            background: white;
            border: 2px solid var(--border-color);
            border-radius: 50px;
            cursor: pointer;
            transition: all 0.3s;
            font-weight: 500;
        }
        
        .filter-chip:hover, .filter-chip.active {
            background: var(--primary-color);
            color: white;
            border-color: var(--primary-color);
        }
        
        /* Event Cards */
        .events-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
            gap: 1.5rem;
            margin: 2rem 0;
        }
        
        .event-card {
            background: white;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            transition: all 0.3s;
            cursor: pointer;
            border: 1px solid var(--border-color);
        }
        
        .event-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 12px 24px rgba(0,0,0,0.15);
        }
        
        .event-image {
            width: 100%;
            height: 200px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            position: relative;
            overflow: hidden;
        }
        
        .event-image::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(to bottom, transparent 0%, rgba(0,0,0,0.3) 100%);
        }
        
        .event-date-badge {
            position: absolute;
            top: 1rem;
            left: 1rem;
            background: white;
            padding: 0.5rem 1rem;
            border-radius: 8px;
            font-weight: 700;
            color: var(--primary-color);
            z-index: 1;
            box-shadow: 0 2px 8px rgba(0,0,0,0.15);
        }
        
        .event-content {
            padding: 1.5rem;
        }
        
        .event-category {
            display: inline-block;
            padding: 0.3rem 0.8rem;
            background: var(--bg-light);
            color: var(--primary-color);
            border-radius: 6px;
            font-size: 0.85rem;
            font-weight: 600;
            margin-bottom: 0.8rem;
        }
        
        .event-title {
            font-size: 1.3rem;
            font-weight: 700;
            color: var(--text-dark);
            margin-bottom: 0.8rem;
            line-height: 1.3;
        }
        
        .event-info {
            display: flex;
            align-items: center;
            color: var(--text-light);
            font-size: 0.95rem;
            margin-bottom: 0.5rem;
        }
        
        .event-info i {
            margin-right: 0.5rem;
            color: var(--primary-color);
        }
        
        .event-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding-top: 1rem;
            border-top: 1px solid var(--border-color);
            margin-top: 1rem;
        }
        
        .event-price {
            font-size: 1.2rem;
            font-weight: 700;
            color: var(--text-dark);
        }
        
        .event-price.free {
            color: #00ab55;
        }
        
        .btn-event {
            background: var(--primary-color);
            color: white;
            border: none;
            padding: 0.6rem 1.5rem;
            border-radius: 8px;
            font-weight: 600;
            transition: all 0.3s;
            text-decoration: none;
        }
        
        .btn-event:hover {
            background: var(--accent-color);
            color: white;
            transform: scale(1.05);
        }
        
        /* Empty State */
        .empty-state {
            text-align: center;
            padding: 4rem 2rem;
        }
        
        .empty-state i {
            font-size: 4rem;
            color: var(--text-light);
            margin-bottom: 1.5rem;
        }
        
        .empty-state h3 {
            font-size: 1.8rem;
            font-weight: 700;
            margin-bottom: 1rem;
        }
        
        .empty-state p {
            color: var(--text-light);
            font-size: 1.1rem;
        }
        
        /* Location Toggle */
        .location-toggle {
            display: flex;
            align-items: center;
            padding: 1rem;
            background: var(--bg-light);
            border-radius: 8px;
            margin-top: 1rem;
        }
        
        .location-toggle input[type="checkbox"] {
            width: 20px;
            height: 20px;
            margin-right: 0.8rem;
            cursor: pointer;
        }
        
        .location-info {
            margin-top: 0.5rem;
            padding: 0.5rem;
            background: white;
            border-radius: 6px;
            font-size: 0.9rem;
        }
        
        /* Alert Messages */
        .alert {
            padding: 1rem;
            border-radius: 8px;
            margin: 1rem 0;
        }
        
        .alert-info {
            background: #e3f2fd;
            color: #1976d2;
            border: 1px solid #90caf9;
        }
        
        .alert-danger {
            background: #ffebee;
            color: #c62828;
            border: 1px solid #ef9a9a;
        }
        
        /* Responsive */
        @media (max-width: 768px) {
            .hero-title {
                font-size: 2rem;
            }
            
            .hero-subtitle {
                font-size: 1.1rem;
            }
            
            .events-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg">
        <div class="container">
            <a class="navbar-brand" href="#">
                <i class="fas fa-calendar-star"></i> EventManager
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/catalogue">Parcourir les événements</a>
                    </li>
                    
                    <c:choose>
                        <c:when test="${not empty sessionScope.user}">
                            <!-- Menu pour utilisateur connecté -->
                            <li class="nav-item">
                                <a class="nav-link" href="#">Mes événements</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="#">
                                    <i class="fas fa-user-circle me-1"></i>${sessionScope.userName}
                                </a>
                            </li>
                            <c:if test="${sessionScope.userType == 'ORGANISATEUR'}">
                                <li class="nav-item ms-3">
                                    <button class="btn btn-create" onclick="window.location.href='${pageContext.request.contextPath}/events/create'">
                                        <i class="fas fa-plus me-2"></i>Créer un événement
                                    </button>
                                </li>
                            </c:if>
                            <li class="nav-item ms-2">
                                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger" style="border-radius: 8px; padding: 0.6rem 1.5rem;">
                                    <i class="fas fa-sign-out-alt me-2"></i>Déconnexion
                                </a>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <!-- Menu pour utilisateur non connecté -->
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/login">
                                    <i class="fas fa-sign-in-alt me-1"></i>Se connecter
                                </a>
                            </li>
                            <li class="nav-item ms-3">
                                <button class="btn btn-create" onclick="window.location.href='${pageContext.request.contextPath}/register'">
                                    <i class="fas fa-user-plus me-2"></i>Ouvrir un compte
                                </button>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Hero Section -->
    <section class="hero-section">
        <div class="container text-center">
            <h1 class="hero-title">Découvrez les meilleurs événements</h1>
            <p class="hero-subtitle">Trouvez votre prochaine expérience inoubliable</p>
        </div>
    </section>

    <!-- Main Content -->
    <div class="container">
        <!-- Search Container -->
        <div class="search-container mb-4">
            <form action="${pageContext.request.contextPath}/catalogue" method="GET" class="mb-3">
                <div class="row g-3 align-items-end">
                    <!-- Champ Date -->
                    <div class="col-md-3">
                        <label for="date" class="form-label">Date</label>
                        <input type="date" 
                               class="form-control search-input" 
                               id="date" 
                               name="date" 
                               value="${not empty selectedDate ? selectedDate : ''}"
                               placeholder="Toutes les dates">
                    </div>
                    
                    <!-- Champ Lieu -->
                    <div class="col-md-3">
                        <label for="lieu" class="form-label">Lieu</label>
                        <input type="text" 
                               class="form-control search-input" 
                               id="lieu" 
                               name="lieu" 
                               value="${not empty selectedLieu ? selectedLieu : ''}"
                               placeholder="Ville ou lieu">
                    </div>
                    
                    <!-- Sélecteur de catégorie -->
                    <div class="col-md-3">
                        <label for="categorie" class="form-label">Catégorie</label>
                        <select class="form-select search-input" id="categorie" name="categorie">
                            <option value="">Toutes les catégories</option>
                            <c:forEach items="${categories}" var="categorie">
                                <option value="${categorie.id}" 
                                        ${not empty selectedCategorie and selectedCategorie eq categorie.id.toString() ? 'selected' : ''}>
                                    ${categorie.nom}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <!-- Bouton de recherche -->
                    <div class="col-md-3 d-flex align-items-end">
                        <button type="submit" class="btn btn-primary w-100 py-2">
                            <i class="fas fa-search me-2"></i>Rechercher
                        </button>
                    </div>
                </div>
                
                <!-- Bouton de réinitialisation -->
                <div class="row mt-2">
                    <div class="col-12 text-end">
                        <a href="${pageContext.request.contextPath}/catalogue" class="btn btn-link btn-sm">
                            <i class="fas fa-times me-1"></i>Réinitialiser les filtres
                        </a>
                    </div>
                </div>
            </form>
            
            <!-- Affichage des erreurs -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
        </div>
        
        <!-- Script pour la géolocalisation -->
        <script>
            function toggleLocation() {
                const useLocation = document.getElementById('useLocation');
                const locationInfo = document.getElementById('locationInfo');
                const lieuInput = document.getElementById('lieu');
                
                if (useLocation.checked) {
                    if (navigator.geolocation) {
                        navigator.geolocation.getCurrentPosition(
                            function(position) {
                                // Ici, vous pourriez utiliser un service de géocodage inversé
                                // pour obtenir le nom du lieu à partir des coordonnées
                                const latitude = position.coords.latitude;
                                const longitude = position.coords.longitude;
                                
                                // Pour l'instant, on affiche juste les coordonnées
                                lieuInput.value = `${latitude.toFixed(4)}, ${longitude.toFixed(4)}`;
                                locationInfo.textContent = `Position: ${latitude.toFixed(4)}, ${longitude.toFixed(4)}`;
                                locationInfo.style.display = 'block';
                            },
                            function(error) {
                                let errorMessage = "Impossible d'obtenir votre position: ";
                                switch(error.code) {
                                    case error.PERMISSION_DENIED:
                                        errorMessage += "Vous avez refusé la demande de géolocalisation.";
                                        break;
                                    case error.POSITION_UNAVAILABLE:
                                        errorMessage += "Les informations de localisation ne sont pas disponibles.";
                                        break;
                                    case error.TIMEOUT:
                                        errorMessage += "La demande de géolocalisation a expiré.";
                                        break;
                                    case error.UNKNOWN_ERROR:
                                        errorMessage += "Une erreur inconnue s'est produite.";
                                        break;
                                }
                                locationInfo.textContent = errorMessage;
                                locationInfo.style.display = 'block';
                                useLocation.checked = false;
                            }
                        );
                    } else {
                        locationInfo.textContent = "La géolocalisation n'est pas prise en charge par votre navigateur.";
                        locationInfo.style.display = 'block';
                        useLocation.checked = false;
                    }
                } else {
                    locationInfo.style.display = 'none';
                    lieuInput.value = '';
                }
            }
        </script>

        <!-- Events Grid -->
        <!-- Debug Info -->
        <c:if test="${not empty param.debug}">
            <div class="alert alert-info">
                <strong>Debug:</strong> Nombre d'événements trouvés: ${evenements.size()}<br>
                <strong>Catégories disponibles:</strong> ${categories.size()}
            </div>
        </c:if>
        
        <c:choose>
            <c:when test="${empty evenements}">
                <div class="empty-state">
                    <i class="fas fa-calendar-times"></i>
                    <h3>Aucun événement trouvé</h3>
                    <p>Essayez de modifier vos critères de recherche ou explorez d'autres catégories</p>
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger mt-3">
                            <i class="fas fa-exclamation-triangle"></i> ${error}
                        </div>
                    </c:if>
                </div>
            </c:when>
            <c:otherwise>
                <div style="margin: 1rem 0; color: var(--text-light);">
                    <i class="fas fa-info-circle"></i> ${evenements.size()} événement(s) trouvé(s)
                </div>
                <div class="events-grid">
                    <c:forEach items="${evenements}" var="evenement">
                        <div class="event-card" onclick="window.location.href='${pageContext.request.contextPath}/events/${evenement.id}'">
                            <div class="event-image">
                                <div class="event-date-badge">
                                    ${evenement.dateDebut}
                                </div>
                            </div>
                            <div class="event-content">
                                <c:choose>
                                    <c:when test="${not empty evenement.categories}">
                                        <c:forEach items="${evenement.categories}" var="cat" varStatus="status" begin="0" end="0">
                                            <span class="event-category">
                                                <i class="fas fa-tag"></i> ${cat.nom}
                                            </span>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="event-category">
                                            <i class="fas fa-tag"></i> Général
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                                
                                <h3 class="event-title">${evenement.titre}</h3>
                                
                                <c:if test="${not empty evenement.description}">
                                    <p style="color: var(--text-light); font-size: 0.95rem; margin-bottom: 1rem; line-height: 1.5;">
                                        <c:choose>
                                            <c:when test="${evenement.description.length() > 120}">
                                                ${evenement.description.substring(0, 120)}...
                                            </c:when>
                                            <c:otherwise>
                                                ${evenement.description}
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                </c:if>
                                
                                <div class="event-info">
                                    <i class="far fa-clock"></i>
                                    ${evenement.dateDebut}
                                </div>
                                
                                <div class="event-info">
                                    <i class="fas fa-map-marker-alt"></i>
                                    <c:choose>
                                        <c:when test="${evenement.lieu.length() > 50}">
                                            ${evenement.lieu.substring(0, 50)}...
                                        </c:when>
                                        <c:otherwise>
                                            ${evenement.lieu}
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                
                                <div class="event-footer">
                                    <div class="event-price free">Gratuit</div>
                                    <div class="d-flex gap-2">
                                        <button class="btn btn-outline-primary btn-sm view-details" 
                                                data-id="${evenement.id}"
                                                data-titre="${fn:escapeXml(evenement.titre)}"
                                                data-description="${fn:escapeXml(evenement.description)}"
                                                data-datedebut="${evenement.dateDebut}"
                                                data-datefin="${evenement.dateFin}"
                                                data-lieu="${fn:escapeXml(evenement.lieu)}"
                                                data-capacite="${evenement.capacite}">
                                            <i class="fas fa-eye me-1"></i> Détails
                                        </button>
                                        <a href="${pageContext.request.contextPath}/events/${evenement.id}/register" 
                                           class="btn btn-primary btn-sm" 
                                           onclick="event.stopPropagation();">
                                            S'inscrire
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- Modal pour afficher les détails de l'événement -->
                <div class="modal fade" id="eventDetailsModal" tabindex="-1" aria-labelledby="eventDetailsModalLabel" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="eventDetailsModalLabel">Détails de l'événement</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <h4 id="modalEventTitle" class="mb-4"></h4>
                                
                                <div class="mb-3">
                                    <h6><i class="fas fa-info-circle text-primary me-2"></i>Description</h6>
                                    <p id="modalEventDescription" class="ms-4"></p>
                                </div>
                                
                                <div class="row">
                                    <div class="col-md-6 mb-3">
                                        <h6><i class="far fa-calendar-alt text-primary me-2"></i>Date et heure</h6>
                                        <p class="ms-4">
                                            <span id="modalEventDateDebut"></span><br>
                                            <small class="text-muted">au <span id="modalEventDateFin"></span></small>
                                        </p>
                                    </div>
                                    <div class="col-md-6 mb-3">
                                        <h6><i class="fas fa-map-marker-alt text-primary me-2"></i>Lieu</h6>
                                        <p id="modalEventLieu" class="ms-4"></p>
                                    </div>
                                </div>
                                
                                <div class="row">
                                    <div class="col-md-6">
                                        <h6><i class="fas fa-users text-primary me-2"></i>Capacité</h6>
                                        <p id="modalEventCapacite" class="ms-4"></p>
                                    </div>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fermer</button>
                                <a id="modalInscriptionLink" href="#" class="btn btn-primary">S'inscrire</a>
                            </div>
                        </div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // Gestion du clic sur le bouton Détails
        document.addEventListener('DOMContentLoaded', function() {
            document.querySelectorAll('.view-details').forEach(button => {
                button.addEventListener('click', function(e) {
                    e.stopPropagation();
                    
                    // Récupérer les données de l'événement
                    const eventData = {
                        id: this.getAttribute('data-id'),
                        titre: this.getAttribute('data-titre'),
                        description: this.getAttribute('data-description'),
                        dateDebut: this.getAttribute('data-datedebut'),
                        dateFin: this.getAttribute('data-datefin'),
                        lieu: this.getAttribute('data-lieu'),
                        capacite: this.getAttribute('data-capacite')
                    };
                    
                    // Mettre à jour la modale
                    document.getElementById('modalEventTitle').textContent = eventData.titre;
                    document.getElementById('modalEventDescription').textContent = 
                        eventData.description || 'Aucune description disponible';
                    
                    // Formater les dates
                    const formatDate = (dateString) => {
                        const options = { 
                            weekday: 'long', 
                            year: 'numeric', 
                            month: 'long', 
                            day: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit'
                        };
                        return new Date(dateString).toLocaleDateString('fr-FR', options);
                    };
                    
                    document.getElementById('modalEventDateDebut').textContent = formatDate(eventData.dateDebut);
                    document.getElementById('modalEventDateFin').textContent = formatDate(eventData.dateFin);
                    
                    // Mettre à jour les autres champs
                    document.getElementById('modalEventLieu').textContent = eventData.lieu || 'Lieu non spécifié';
                    document.getElementById('modalEventCapacite').textContent = 
                        eventData.capacite ? `${eventData.capacite} personnes` : 'Illimité';
                    
                    // Mettre à jour le lien d'inscription
                    const baseUrl = window.location.origin + window.location.pathname.split('/').slice(0, -1).join('/');
                    document.getElementById('modalInscriptionLink').href = `${baseUrl}/events/${eventData.id}/register`;
                    
                    // Afficher la modale
                    const modal = new bootstrap.Modal(document.getElementById('eventDetailsModal'));
                    modal.show();
                });
            });
        });

        function toggleLocation() {
            const checkbox = document.getElementById('useLocation');
            const locationInput = document.getElementById('location');
            const locationInfo = document.getElementById('locationInfo');
            
            if (checkbox.checked && navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(function(position) {
                    const lat = position.coords.latitude;
                    const lng = position.coords.longitude;
                    // Ici, vous pourriez utiliser une API de géocodage inverse pour obtenir l'adresse
                    locationInput.value = `${lat}, ${lng}`;
                    locationInfo.style.display = 'block';
                    locationInfo.innerHTML = `
                        <i class="fas fa-check-circle text-success"></i> 
                        Position détectée: ${lat.toFixed(4)}, ${lng.toFixed(4)}
                    `;
                }, function(error) {
                    console.error("Erreur de géolocalisation:", error);
                    locationInput.value = "";
                    locationInput.placeholder = "Impossible d'obtenir votre localisation";
                    locationInfo.style.display = 'block';
                    locationInfo.innerHTML = `
                        <i class="fas fa-exclamation-triangle text-warning"></i> 
                        Impossible d'accéder à votre position
                    `;
                    checkbox.checked = false;
                });
            } else {
                locationInput.value = "";
                locationInfo.style.display = 'none';
            }
            locationInput.disabled = !checkbox.checked;
        }

        // Fonction pour afficher les détails de l'événement dans la modale
        function showEventDetails(id, titre, description, dateDebut, dateFin, lieu, capacite) {
            // Mettre à jour le titre de la modale
            document.getElementById('modalEventTitle').textContent = titre;
            
            // Mettre à jour la description
            const descElement = document.getElementById('modalEventDescription');
            descElement.textContent = description || 'Aucune description disponible';
            
            // Formater et mettre à jour les dates
            const dateDebutObj = new Date(dateDebut);
            const dateFinObj = new Date(dateFin);
            
            const options = { 
                weekday: 'long', 
                year: 'numeric', 
                month: 'long', 
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            };
            
            document.getElementById('modalEventDateDebut').textContent = dateDebutObj.toLocaleDateString('fr-FR', options);
            document.getElementById('modalEventDateFin').textContent = dateFinObj.toLocaleDateString('fr-FR', options);
            
            // Mettre à jour le lieu
            document.getElementById('modalEventLieu').textContent = lieu || 'Lieu non spécifié';
            
            // Mettre à jour la capacité
            const capaciteElement = document.getElementById('modalEventCapacite');
            capaciteElement.textContent = capacite ? `${capacite} personnes` : 'Illimité';
            
            // Mettre à jour le lien d'inscription
            const inscriptionLink = document.getElementById('modalInscriptionLink');
            const baseUrl = window.location.origin + window.location.pathname.split('/').slice(0, -1).join('/');
            inscriptionLink.href = `${baseUrl}/events/${id}/register`;
            
            // Afficher la modale
            const modal = new bootstrap.Modal(document.getElementById('eventDetailsModal'));
            modal.show();
        }
    </script>
</body>
</html>
