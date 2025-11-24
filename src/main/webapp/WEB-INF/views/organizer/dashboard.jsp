<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tableau de Bord Organisateur - EventHub</title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <%@ include file="/WEB-INF/jspf/theme-head.jspf" %>
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/organizer-theme.css">
    
    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f8f9fa;
            color: #1f2937;
        }
    </style>
</head>
<body>
    <!-- Header -->
    <nav class="navbar navbar-expand-lg organizer-navbar sticky-top shadow-sm">
        <div class="container-fluid">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/organizer/dashboard">
                <i class="bi bi-calendar-event"></i>EventHub Organisateur
            </a>
            
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto align-items-center">
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/organizer/dashboard">
                            <i class="bi bi-speedometer2 me-1"></i>Tableau de Bord
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/organizer/events/new">
                            <i class="bi bi-plus-circle me-1"></i>Nouvel Événement
                        </a>
                    </li>
                    <li class="nav-item dropdown ms-3">
                        <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" id="userDropdown" 
                           role="button" data-bs-toggle="dropdown">
                            <div class="user-avatar me-2">
                                <i class="bi bi-person-fill"></i>
                            </div>
                            <span><c:out value="${organizer.nom}"/></span>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li><a class="dropdown-item" href="#">
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
                <div class="organizer-welcome-banner">
                    <h1 class="h3 mb-2">Bienvenue, <c:out value="${organizer.nom}"/>! 👋</h1>
                    <p class="mb-0 opacity-90">Gérez vos événements et suivez les inscriptions</p>
                </div>
            </div>
        </div>

        <!-- Statistics Cards -->
        <div class="row g-3 mb-4">
            <div class="col-md-3 col-sm-6">
                <div class="organizer-stat-card">
                    <div class="d-flex align-items-center">
                        <div class="organizer-stat-icon primary">
                            <i class="bi bi-calendar-event"></i>
                        </div>
                        <div>
                            <div class="fs-3 fw-bold"><c:out value="${events.size()}"/></div>
                            <div class="text-muted small">Événements créés</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3 col-sm-6">
                <div class="organizer-stat-card">
                    <div class="d-flex align-items-center">
                        <div class="organizer-stat-icon success">
                            <i class="bi bi-check-circle"></i>
                        </div>
                        <div>
                            <div class="fs-3 fw-bold"><c:out value="${publishedEventsCount}"/></div>
                            <div class="text-muted small">Événements publiés</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3 col-sm-6">
                <div class="organizer-stat-card">
                    <div class="d-flex align-items-center">
                        <div class="organizer-stat-icon warning">
                            <i class="bi bi-people"></i>
                        </div>
                        <div>
                            <div class="fs-3 fw-bold"><c:out value="${totalParticipants}"/></div>
                            <div class="text-muted small">Participants total</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3 col-sm-6">
                <div class="organizer-stat-card">
                    <div class="d-flex align-items-center">
                        <div class="organizer-stat-icon info">
                            <i class="bi bi-star"></i>
                        </div>
                        <div>
                            <div class="fs-3 fw-bold"><fmt:formatNumber value="${avgRating}" pattern="#0.0"/></div>
                            <div class="text-muted small">Note moyenne</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Events Section -->
        <div class="row">
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2 class="h4 mb-0">
                        <i class="bi bi-calendar-event text-primary me-2"></i>Vos Événements
                    </h2>
                </div>

                <div class="row g-3">
                    <c:forEach var="event" items="${events}">
                        <div class="col-lg-4 col-md-6">
                            <a href="${pageContext.request.contextPath}/organizer/events/detail?id=${event.id}"
                               class="organizer-event-card status-${fn:toLowerCase(event.statut)}">
                                <div class="p-3">
                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                        <h5 class="mb-0 fw-bold"><c:out value="${event.titre}"/></h5>
                                        <c:choose>
                                            <c:when test="${event.statut == 'PUBLIE'}">
                                                <span class="organizer-status-badge status-publie">Publié</span>
                                            </c:when>
                                            <c:when test="${event.statut == 'BROUILLON'}">
                                                <span class="organizer-status-badge status-brouillon">Brouillon</span>
                                            </c:when>
                                            <c:when test="${event.statut == 'ANNULE'}">
                                                <span class="organizer-status-badge status-annule">Annulé</span>
                                            </c:when>
                                            <c:when test="${event.statut == 'CACHE'}">
                                                <span class="organizer-status-badge status-cache">Caché</span>
                                            </c:when>
                                        </c:choose>
                                    </div>
                                    <p class="text-muted mb-2">
                                        <i class="bi bi-calendar3 me-1"></i>
                                        <fmt:formatDate value="${event.dateDebutAsDate}" pattern="dd MMM yyyy, HH:mm"/>
                                    </p>
                                    <p class="text-muted mb-2">
                                        <i class="bi bi-geo-alt me-1"></i>
                                        <c:out value="${event.lieu}"/>
                                    </p>
                                    <div class="d-flex justify-content-between align-items-center">
                                        <span class="text-muted small">
                                            <i class="bi bi-people me-1"></i>
                                            <c:out value="${event.nombreInscrits != null ? event.nombreInscrits : 0}"/> inscrits
                                        </span>
                                        <span class="text-muted small">
                                            <c:out value="${event.capacite != null ? event.capacite : 0}"/> places
                                        </span>
                                    </div>
                                </div>
                            </a>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>