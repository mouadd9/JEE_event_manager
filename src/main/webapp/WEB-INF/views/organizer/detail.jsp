<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Détails: <c:out value="${event.titre}"/> - EventHub</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin=""/>
    <%@ include file="/WEB-INF/jspf/theme-head.jspf" %>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/organizer-theme.css">

  <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f8f9fa;
            color: #1f2937;
        }
        
        .detail-card { /* Changed from detail-container */
            background: white;
            border-radius: 0.75rem; /* Slightly smaller radius */
            padding: 2rem;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08); /* Softer shadow */
            margin-top: 2rem;
            border: 1px solid #e5e7eb; /* Subtle border */
        }
        
        .detail-header {
      display: flex;
      justify-content: space-between;
            align-items: flex-start; /* Align items to the top */
            border-bottom: 1px solid #e5e7eb;
            padding-bottom: 1.5rem;
            margin-bottom: 2rem;
            gap: 1rem; /* Space between title/status and actions */
        }

        .header-info {
           flex-grow: 1; /* Allow title section to take available space */
        }
        
        .detail-title {
            font-size: 1.75rem; /* Slightly smaller */
            font-weight: 700;
            color: #111827; /* Darker title */
            margin-bottom: 0.5rem;
            line-height: 1.3;
        }
        
        .status-badge {
            font-size: 0.8rem;
      font-weight: 600;
            padding: 0.35rem 0.75rem;
            border-radius: 0.375rem; /* Bootstrap's default */
            vertical-align: middle; /* Align better with title if wrapped */
        }

    .detail-actions {
      display: flex;
            gap: 0.5rem; /* Smaller gap */
            flex-wrap: nowrap; /* Prevent wrapping if possible */
            align-items: center; /* Vertically align buttons */
            margin-top: 0; /* Removed top margin */
        }
        
    .detail-actions form {
            display: inline-block;
      margin: 0;
        }
        
        .btn-action {
            padding: 0.5rem 1rem; /* Smaller padding */
            border-radius: 0.375rem; /* Bootstrap default */
      font-weight: 600;
            font-size: 0.875rem; /* Smaller font */
      text-decoration: none;
            border: none;
      cursor: pointer;
      transition: all 0.2s;
            display: inline-flex; /* Align icon and text */
      align-items: center;
            gap: 0.35rem; /* Space between icon and text */
        }
        
        .btn-edit {
            background: linear-gradient(135deg, #a855f7 0%, #9333ea 100%);
            color: white;
        }
        .btn-edit:hover { opacity: 0.9; color: white; }
        
        .btn-cancel { background-color: #ef4444; color: white; }
        .btn-cancel:hover { background-color: #dc2626; color: white; }
        
        .btn-delete { background-color: #6b7280; color: white; }
        .btn-delete:hover { background-color: #4b5563; color: white; }
        
        /* Image Styles */
        .event-image-container {
            width: 100%;
            margin-bottom: 2rem; /* Space below image */
            border-radius: 0.5rem; /* Consistent radius */
            overflow: hidden;
            background-color: #f3f4f6; /* Lighter placeholder background */
            border: 1px solid #e5e7eb;
        }

        .event-image {
            width: 100%;
            height: 400px; 
            object-fit: cover;
            display: block;
        }

        .event-image-placeholder {
            width: 100%;
            height: 400px;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-direction: column;
            color: #9ca3af; /* Lighter placeholder text */
      font-weight: 500;
    }

        .event-image-placeholder i {
            font-size: 3rem;
            margin-bottom: 0.5rem;
        }

        /* Info Section - Using Bootstrap Grid */
        .info-section .row {
            margin-bottom: 1.5rem;
        }
        .info-section .info-item {
            background-color: #f9fafb;
            padding: 1rem;
            border-radius: 0.375rem;
            border: 1px solid #e5e7eb;
            height: 100%; /* Make columns equal height */
            display: flex;
            flex-direction: column; /* Stack icon/label and value */
        }
        .info-label {
      font-weight: 600;
            color: #374151;
            font-size: 0.85rem; /* Smaller label */
            display: flex;
            align-items: center;
            gap: 0.5rem;
            margin-bottom: 0.5rem; /* Space between label and value */
        }
        .info-label i {
             color: #a855f7; /* Accent color for icons */
        }
        .info-value {
            color: #111827; /* Darker value text */
            font-size: 1rem;
      font-weight: 500;
    }

        /* Section Headings */
         h2.section-title { /* Added class for consistency */
            font-size: 1.35rem; /* Slightly larger */
            font-weight: 600;
            color: #111827;
            margin-top: 2.5rem; /* More space above sections */
            margin-bottom: 1.25rem;
            padding-bottom: 0.6rem;
            border-bottom: 2px solid #a855f7; /* Accent border */
            display: inline-block; /* Make border fit content */
        }
         h2.section-title i {
            margin-right: 0.5rem; /* Space after icon */
            color: #a855f7; /* Accent color */
         }
        
        /* Description */
        .description {
            color: #374151;
            font-size: 1rem;
            line-height: 1.7;
            white-space: pre-wrap; /* Keep line breaks */
            margin-bottom: 2rem;
        }
        
        /* Map */
        #map {
            height: 400px;
            width: 100%;
            border-radius: 0.5rem;
            border: 1px solid #e5e7eb;
            margin-bottom: 2rem;
        }
        
        /* Comments Section */
        .comments-section {
            margin-top: 2rem;
        }
        
        .avatar-circle {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            font-size: 1.1rem;
        }
        
        .commentaire-item:last-child {
            border-bottom: none !important;
        }
        
        .commentaires-list {
            max-height: 500px;
            overflow-y: auto;
        }
        
        /* Ratings Section */
        .ratings-section {
            margin-top: 2rem;
        }
        
        .rating-summary {
            background: #f9fafb;
            border: 1px solid #e5e7eb;
            border-radius: 0.375rem;
            padding: 1rem;
        }
        
        .rating-stars {
            font-size: 1.2rem;
    }

  </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg organizer-navbar sticky-top shadow-sm">
        <div class="container-fluid">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/organizer/dashboard">
                <i class="bi bi-calendar-event"></i> EventHub Organisateur
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto align-items-center">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/organizer/dashboard">
                            <i class="bi bi-speedometer2 me-1"></i> Tableau de Bord
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/organizer/events/new">
                            <i class="bi bi-plus-circle me-1"></i> Nouvel Événement
                        </a>
                    </li>
                    <li class="nav-item dropdown ms-3">
                        <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                            <div class="user-avatar me-2"><i class="bi bi-person-fill"></i></div>
                            <span><c:out value="${organizer.nom}"/></span>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li><a class="dropdown-item" href="#"><i class="bi bi-person me-2"></i> Mon Profil</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout"><i class="bi bi-box-arrow-right me-2"></i> Déconnexion</a></li>
                        </ul>
                    </li>
                </ul>
  </div>
</div>
    </nav>

    <div class="container py-4">
        <div class="row justify-content-center">
            <div class="col-lg-10 col-xl-9"> 
                <div class="detail-card"> 

                    <%-- Header Section: Title, Status, Actions --%>
  <div class="detail-header">
                        <div class="header-info">
                            <h1 class="detail-title">
      <c:out value="${event.titre}"/>
                            </h1>
      <c:choose>
                                <c:when test="${event.statut == 'PUBLIE'}"><span class="badge bg-success status-badge">Publié</span></c:when>
                                <c:when test="${event.statut == 'BROUILLON'}"><span class="badge bg-warning status-badge">Brouillon</span></c:when>
                                <c:when test="${event.statut == 'ANNULE'}"><span class="badge bg-danger status-badge">Annulé</span></c:when>
                                <c:otherwise><span class="badge bg-secondary status-badge"><c:out value="${event.statut}"/></span></c:otherwise>
      </c:choose>
                        </div>                        
    <div class="detail-actions">
                            <a href="${pageContext.request.contextPath}/organizer/events/edit?id=${event.id}" class="btn-action btn-edit">
                                <i class="bi bi-pencil-square"></i> Modifier
                            </a>
      <c:if test="${event.statut != 'ANNULE'}">
                                <form action="${pageContext.request.contextPath}/organizer/events" method="POST" class="d-inline">
          <input type="hidden" name="action" value="cancel">
          <input type="hidden" name="eventId" value="${event.id}">
                                    <button type="submit" class="btn-action btn-cancel">
                                        <i class="bi bi-x-circle"></i> Annuler
                                    </button>
        </form>
      </c:if>
    </div>
  </div>

                    <%-- Image Section --%>
                    <div class="event-image-container">
                        <c:choose>
                            <c:when test="${not empty event.imageUrl}">
                                <c:set var="imgUrl" value="${event.imageUrl}"/>
                                <c:set var="finalUrl" value="${fn:startsWith(imgUrl, 'http') ? imgUrl : pageContext.request.contextPath.concat('/').concat(imgUrl)}"/>
                                <img src="${finalUrl}" 
                                     alt="${event.titre}" 
                                     class="event-image"
                                     onerror="this.src='https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=800';">
                            </c:when>
                            <c:otherwise>
                                <div class="event-image-placeholder">
                                    <i class="bi bi-image-alt"></i>
                                    <span>Aucune image pour cet événement</span>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <%-- Info Section (Using Bootstrap Grid) --%>
                    <div class="info-section">
                        <div class="row g-3"> <%-- g-3 adds gutters --%>
                            <div class="col-md-6">
                                <div class="info-item">
                                    <span class="info-label"><i class="bi bi-calendar-event"></i> Période</span>
                                    <span class="info-value">
                                        <fmt:formatDate value="${event.dateDebutAsDate}" pattern="dd/MM/yy HH:mm"/> - 
                                        <fmt:formatDate value="${event.dateFinAsDate}" pattern="dd/MM/yy HH:mm"/>
                                    </span>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="info-item">
                                    <span class="info-label"><i class="bi bi-geo-alt"></i> Lieu</span>
                                    <span class="info-value"><c:out value="${event.lieu}"/></span>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="info-item">
                                    <span class="info-label"><i class="bi bi-people"></i> Capacité</span>
                                    <span class="info-value"><c:out value="${event.capacite}"/> places</span>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="info-item">
                                    <span class="info-label"><i class="bi bi-person-check"></i> Inscrits</span>
                                    <span class="info-value">
                                        <c:out value="${event.nombreInscrits != null ? event.nombreInscrits : 0}"/>
                                        <c:if test="${event.capacite != null}">
                                            / <c:out value="${event.capacite}"/>
                                        </c:if>
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <%-- Description Section --%>
                    <h2 class="section-title"><i class="bi bi-card-text"></i> Description</h2>
    <p class="description"><c:out value="${event.description}"/></p>

                    <%-- Map Section --%>
                    <h2 class="section-title"><i class="bi bi-map"></i> Localisation</h2>
    <div id="map"></div>

                    <%-- Comments Section --%>
                    <div class="comments-section">
                         <h2 class="section-title"><i class="bi bi-chat-dots"></i> Commentaires des participants</h2>
                         <c:choose>
                             <c:when test="${not empty comments}">
                                 <div class="commentaires-list">
                                     <c:forEach var="comment" items="${comments}">
                                         <div class="commentaire-item border-bottom py-3">
                                             <div class="d-flex justify-content-between align-items-start mb-2">
                                                 <div class="d-flex align-items-center">
                                                     <div class="avatar-circle bg-primary text-white me-2">
                                                         <c:choose>
                                                             <c:when test="${not empty comment.participant.nom}">
                                                                 ${fn:substring(comment.participant.nom, 0, 1).toUpperCase()}
                                                             </c:when>
                                                             <c:otherwise>U</c:otherwise>
                                                         </c:choose>
                                                     </div>
                                                     <div>
                                                         <div class="fw-semibold">
                                                             <c:out value="${comment.participant.nom != null ? comment.participant.nom : 'Utilisateur'}"/>
                                                         </div>
                                                         <div class="text-muted small">
                                                             <fmt:formatDate value="${comment.horodatageAsDate}" pattern="dd/MM/yyyy à HH:mm"/>
                                                         </div>
                                                     </div>
                                                 </div>
                                             </div>
                                             <p class="mb-0"><c:out value="${comment.texte}"/></p>
                                         </div>
                                     </c:forEach>
                                 </div>
                             </c:when>
                             <c:otherwise>
                                 <div class="text-center py-4">
                                     <i class="bi bi-chat-square-dots text-muted" style="font-size: 3rem;"></i>
                                     <p class="text-muted mt-2">Aucun commentaire pour le moment</p>
                                 </div>
                             </c:otherwise>
                         </c:choose>
                    </div>
                    
                    <%-- Ratings Section --%>
                    <div class="ratings-section">
                         <h2 class="section-title"><i class="bi bi-star"></i> Évaluations des participants</h2>
                         <c:choose>
                             <c:when test="${event.nombreEvaluations != null && event.nombreEvaluations > 0}">
                                 <div class="rating-summary mb-3">
                                     <div class="d-flex align-items-center">
                                         <div class="rating-stars me-3">
                                             <c:forEach begin="1" end="5" var="star">
                                                 <i class="bi bi-star${star <= (event.noteMoyenne != null ? event.noteMoyenne : 0) ? '-fill' : ''} text-warning"></i>
                                             </c:forEach>
                                         </div>
                                         <div>
                                             <span class="fw-bold fs-5">
                                                 <fmt:formatNumber value="${event.noteMoyenne != null ? event.noteMoyenne : 0}" maxFractionDigits="1"/>
                                             </span>
                                             <span class="text-muted">/ 5</span>
                                             <span class="text-muted ms-2">(${event.nombreEvaluations} évaluation${event.nombreEvaluations > 1 ? 's' : ''})</span>
                                         </div>
                                     </div>
                                 </div>
                                 <div class="text-muted">
                                     <i class="bi bi-info-circle me-1"></i>
                                     Les évaluations individuelles ne sont pas affichées pour préserver l'anonymat des participants.
                                 </div>
                             </c:when>
                             <c:otherwise>
                                 <div class="text-muted text-center py-4">
                                     <i class="bi bi-star fs-1 mb-2"></i>
                                     <p>Aucune évaluation pour le moment</p>
                                 </div>
                             </c:otherwise>
                         </c:choose>
                    </div>

  </div>
</div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>

<c:if test="${event.latitude != null && event.longitude != null}">
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            try {
                const lat = parseFloat('${event.latitude}');
                const lon = parseFloat('${event.longitude}');
                const map = L.map('map').setView([lat, lon], 15);
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19, attribution: 'OpenStreetMap' }).addTo(map);
                L.marker([lat, lon]).addTo(map).bindPopup('<c:out value="${event.titre}"/>').openPopup();
            } catch (e) {
                console.error("Error initializing Leaflet map:", e);
                document.getElementById('map').innerHTML = "<p style='padding: 20px; color: red;'>Erreur lors de l'affichage de la carte.</p>";
            }
        });
    </script>
</c:if>
<c:if test="${event.latitude == null || event.longitude == null}">
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            document.getElementById('map').innerHTML = "<p style='padding: 20px; color: #6c757d; text-align: center;'>Aucune localisation GPS fournie.</p>";
        });
    </script>
</c:if>
</body>
</html>