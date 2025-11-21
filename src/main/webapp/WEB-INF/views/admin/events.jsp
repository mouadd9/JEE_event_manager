<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    // Define date formatter for LocalDateTime
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    request.setAttribute("dateFormatter", dateFormatter);
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Modération Événements - Admin</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Neue+Plak:wght@400;600;700&display=swap" rel="stylesheet">
    <%@ include file="/WEB-INF/jspf/theme-head.jspf" %>
    <!-- Theme CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/theme.css">
    
    <style>
        body {
            font-family: 'Neue Plak', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background-color: #f8f9fa;
        }
        
        .admin-header {
            background: linear-gradient(135deg, #8c65a7 0%, #7d24bd 100%);
            color: white;
            padding: 2rem 0;
            margin-bottom: 2rem;
        }
        
        .event-card {
            background: white;
            border-radius: 10px;
            padding: 1.5rem;
            margin-bottom: 1rem;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            transition: transform 0.3s;
        }
        
        .event-card h5,
        .event-card p {
            color: #2d1038 !important;
        }
        
        .event-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.15);
        }
        
        .event-image {
            width: 100%;
            height: 150px;
            object-fit: cover;
            border-radius: 10px;
        }
        
        .badge-publié {
            background: #28a745;
        }
        
        .badge-brouillon {
            background: #6c757d;
        }
        
        .badge-cache {
            background: #ffc107;
            color: #000;
        }
        
        .badge-annule {
            background: #dc3545;
        }
        
        .btn-action {
            padding: 0.4rem 0.8rem;
            border-radius: 5px;
            font-size: 0.85rem;
            margin: 0.2rem;
        }
        
        .filter-card {
            background: white;
            border-radius: 10px;
            padding: 1.5rem;
            margin-bottom: 2rem;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .nav-pills .nav-link {
            color: #6c757d;
            border-radius: 10px;
        }
        
        .nav-pills .nav-link.active {
            background: #8c65a7;
        }
        
        h4, h5, .form-label {
            color: #2d1038 !important;
        }
    </style>
</head>
<body>
    <!-- Admin Header -->
    <div class="admin-header">
        <div class="container">
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <h1><i class="fas fa-calendar-check me-2"></i>Modération des Événements</h1>
                    <p class="mb-0">Gérer et modérer les événements de la plateforme</p>
                </div>
                <div>
                    <span class="me-3"><i class="fas fa-user me-2"></i>${sessionScope.userName}</span>
                    <a href="${pageContext.request.contextPath}/admin/profil" class="btn btn-outline-light me-2">
                        <i class="fas fa-user-circle me-2"></i>Profil
                    </a>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light">
                        <i class="fas fa-sign-out-alt me-2"></i>Déconnexion
                    </a>
                </div>
            </div>
        </div>
    </div>
    
    <div class="container">
        <!-- Navigation Tabs -->
        <ul class="nav nav-pills mb-4">
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">
                    <i class="fas fa-chart-line me-2"></i>Dashboard
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/users">
                    <i class="fas fa-users me-2"></i>Utilisateurs
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link active" href="${pageContext.request.contextPath}/admin/events">
                    <i class="fas fa-calendar me-2"></i>Événements
                </a>
            </li>
        </ul>
        
        <!-- Filters -->
        <div class="filter-card">
            <form method="GET" action="${pageContext.request.contextPath}/admin/events">
                <div class="row g-3 align-items-end">
                    <div class="col-md-6">
                        <label class="form-label text-dark"><i class="fas fa-filter me-1"></i>Statut de l'événement</label>
                        <select name="statut" class="form-select">
                            <option value="">Tous les statuts</option>
                            <option value="PUBLIE" ${selectedStatut == 'PUBLIE' ? 'selected' : ''}>Publiés</option>
                            <option value="BROUILLON" ${selectedStatut == 'BROUILLON' ? 'selected' : ''}>Brouillons</option>
                            <option value="CACHE" ${selectedStatut == 'CACHE' ? 'selected' : ''}>Masqués</option>
                            <option value="ANNULE" ${selectedStatut == 'ANNULE' ? 'selected' : ''}>Annulés</option>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <button type="submit" class="btn btn-primary w-100">
                            <i class="fas fa-filter me-2"></i>Filtrer
                        </button>
                    </div>
                </div>
            </form>
        </div>
        
        <!-- Events List -->
        <h4 class="mb-3" style="color: #2d1038 !important;">Liste des événements (${events.size()})</h4>
        <div class="row">
            <c:forEach var="event" items="${events}">
                <div class="col-md-6">
                    <div class="event-card">
                        <div class="row">
                            <div class="col-md-4">
                                <c:choose>
                                    <c:when test="${not empty event.imageUrl}">
                                        <c:set var="imgUrl" value="${event.imageUrl}"/>
                                        <c:set var="finalUrl" value="${fn:startsWith(imgUrl, 'http') ? imgUrl : pageContext.request.contextPath.concat('/').concat(imgUrl)}"/>
                                        <img src="${finalUrl}" alt="${event.titre}" class="event-image" onerror="this.src='https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=800';">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="event-image bg-secondary d-flex align-items-center justify-content-center">
                                            <i class="fas fa-calendar fa-3x text-white"></i>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="col-md-8">
                                <h5 class="text-dark">${event.titre}</h5>
                                <p class="text-dark mb-1">
                                    <i class="fas fa-user-tie me-1"></i>
                                    <c:if test="${not empty event.organisateur}">
                                        ${event.organisateur.nom}
                                    </c:if>
                                </p>
                                <p class="text-dark mb-1">
                                    <i class="fas fa-map-marker-alt me-1"></i>${event.lieu}
                                </p>
                                <p class="text-dark mb-2">
                                    <i class="fas fa-calendar me-1"></i>
                                    ${event.dateDebut.format(dateFormatter)}
                                </p>
                                <span class="badge badge-${event.statut == 'PUBLIE' ? 'publié' : event.statut == 'BROUILLON' ? 'brouillon' : event.statut == 'CACHE' ? 'cache' : 'annule'}">
                                    ${event.statut}
                                </span>
                            </div>
                        </div>
                        <div class="mt-3 pt-3 border-top">
                            <div class="d-flex flex-wrap gap-1">
                                <c:if test="${event.statut == 'PUBLIE'}">
                                    <button class="btn btn-warning btn-action" onclick="hideEvent('${event.id}')">
                                        <i class="fas fa-eye-slash me-1"></i>Masquer
                                    </button>
                                </c:if>
                                <c:if test="${event.statut == 'CACHE' || event.statut == 'BROUILLON'}">
                                    <button class="btn btn-success btn-action" onclick="publishEvent('${event.id}')">
                                        <i class="fas fa-check me-1"></i>Publier
                                    </button>
                                </c:if>
                                <button class="btn btn-danger btn-action" onclick="deleteEvent('${event.id}')">
                                    <i class="fas fa-trash me-1"></i>Supprimer
                                </button>
                                <a href="${pageContext.request.contextPath}/events/${event.id}" 
                                   class="btn btn-info btn-action" target="_blank">
                                    <i class="fas fa-eye me-1"></i>Voir
                                </a>
                            </div>
                        </div>
                        <c:if test="${not empty event.description}">
                            <div class="mt-2">
                                <small class="text-dark">
                                    ${event.description.length() > 100 ? event.description.substring(0, 100).concat('...') : event.description}
                                </small>
                            </div>
                        </c:if>
                    </div>
                </div>
            </c:forEach>
            
            <c:if test="${empty events}">
                <div class="col-12">
                    <div class="alert alert-info text-center">
                        <i class="fas fa-info-circle me-2"></i>Aucun événement trouvé avec ces critères.
                    </div>
                </div>
            </c:if>
        </div>
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        function hideEvent(eventId) {
            if (!confirm('Voulez-vous vraiment masquer cet événement ?')) return;
            
            performAction('hide', eventId);
        }
        
        function publishEvent(eventId) {
            if (!confirm('Voulez-vous vraiment publier cet événement ?')) return;
            
            performAction('publish', eventId);
        }
        
        function deleteEvent(eventId) {
            if (!confirm('Voulez-vous vraiment supprimer cet événement ? Cette action est irréversible.')) return;
            
            performAction('delete', eventId);
        }
        
        function performAction(action, eventId) {
            console.log('performAction called with:', { action, eventId });
            
            // Validate parameters
            if (!action || !eventId) {
                console.error('Missing parameters:', { action, eventId });
                alert('Erreur: Paramètres manquants');
                return;
            }
            
            // Use URLSearchParams instead of FormData for better compatibility
            const params = new URLSearchParams();
            params.append('action', action);
            params.append('eventId', eventId.toString());
            
            const url = '${pageContext.request.contextPath}/admin/events';
            console.log('Sending POST to:', url);
            
            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                },
                body: params.toString()
            })
            .then(response => {
                console.log('Response status:', response.status);
                if (!response.ok) {
                    throw new Error('HTTP error! status: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log('Response data:', data);
                if (data.success) {
                    alert(data.message);
                    location.reload();
                } else {
                    alert('Erreur: ' + (data.message || 'Opération échouée'));
                }
            })
            .catch(error => {
                console.error('Fetch error:', error);
                alert('Erreur lors de l\'opération. Veuillez vérifier la console pour plus de détails.');
            });
        }
    </script>
</body>
</html>
