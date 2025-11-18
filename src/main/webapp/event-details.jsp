<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${evenement.titre} - EventHub</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <!-- Leaflet CSS for Map -->
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
    <%@ include file="/WEB-INF/jspf/theme-head.jspf" %>
    
    <style>
        /* Event Details Page Styles */
        
        /* ========== HERO BANNER ========== */
        .hero-banner {
            width: 100%;
            height: 400px;
            position: relative;
            overflow: hidden;
            margin-bottom: 3rem;
        }
        
        .hero-banner img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        
        .hero-overlay {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(to bottom, rgba(0,0,0,0.3), rgba(0,0,0,0.7));
        }
        
        .hero-content {
            position: absolute;
            bottom: 2rem;
            left: 0;
            right: 0;
            color: white;
            padding: 0 2rem;
        }
        
        .hero-title {
            font-size: 3rem;
            font-weight: 800;
            margin-bottom: 1rem;
        }
        
        .btn-retour {
            position: absolute;
            top: 2rem;
            left: 2rem;
            background: var(--primary-color);
            color: white;
            border: none;
            padding: 0.7rem 1.5rem;
            border-radius: 8px;
            font-weight: 600;
            text-decoration: none;
            display: flex;
            align-items: center;
            gap: 0.5rem;
            transition: all 0.3s;
        }
        
        .btn-retour:hover {
            background: var(--primary-hover);
            color: white;
            transform: translateX(-5px);
        }
        
        /* ========== MAIN CONTENT ========== */
        .event-details-section {
            padding: 2rem 0;
            background: var(--bg-primary);
            transition: background-color 0.3s ease;
        }
        
        .event-info-card {
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 16px;
            padding: 2rem;
            margin-bottom: 2rem;
            transition: background-color 0.3s ease, border-color 0.3s ease;
        }
        
        .event-title {
            font-size: 2.5rem;
            font-weight: 700;
            color: var(--text-primary);
            margin-bottom: 1rem;
        }
        
        .event-description {
            color: var(--text-secondary);
            font-size: 1.1rem;
            line-height: 1.8;
            margin-bottom: 2rem;
        }
        
        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2rem;
        }
        
        .info-card {
            background: var(--bg-secondary);
            border: 1px solid var(--card-border);
            border-radius: 12px;
            padding: 1.5rem;
            transition: all 0.3s;
        }
        
        .info-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 16px rgba(0,0,0,0.1);
        }
        
        .info-icon {
            font-size: 2rem;
            color: var(--primary-color);
            margin-bottom: 0.5rem;
        }
        
        .info-label {
            font-size: 0.9rem;
            color: var(--text-secondary);
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 0.5rem;
        }
        
        .info-value {
            font-size: 1.2rem;
            color: var(--text-primary);
            font-weight: 600;
        }
        
        /* ========== MAP SECTION ========== */
        .map-container {
            margin: 2rem 0;
        }
        
        .map-card {
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 16px;
            overflow: hidden;
            transition: background-color 0.3s ease, border-color 0.3s ease;
        }
        
        .map-header {
            padding: 1.5rem;
            border-bottom: 1px solid var(--card-border);
            background: var(--bg-secondary);
        }
        
        .map-header h3 {
            color: var(--text-primary);
            font-weight: 700;
            margin: 0;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        #eventMap {
            width: 100%;
            height: 400px;
            z-index: 1;
        }
        
        /* ========== ORGANIZER SECTION ========== */
        .organizer-card {
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 16px;
            padding: 2rem;
            margin-bottom: 2rem;
            transition: background-color 0.3s ease, border-color 0.3s ease;
        }
        
        .organizer-header {
            display: flex;
            align-items: center;
            gap: 1rem;
            margin-bottom: 1rem;
        }
        
        .organizer-icon {
            width: 60px;
            height: 60px;
            background: linear-gradient(135deg, var(--primary-color), var(--primary-hover));
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            color: white;
        }
        
        .organizer-name {
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--text-primary);
            margin: 0;
        }
        
        .organizer-email {
            color: var(--primary-color);
            font-size: 1.1rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
            text-decoration: none;
            transition: color 0.3s;
        }
        
        .organizer-email:hover {
            color: var(--primary-hover);
        }
        
        /* ========== SIDEBAR ========== */
        .sidebar {
            position: sticky;
            top: 2rem;
        }
        
        .register-card {
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 16px;
            padding: 2rem;
            text-align: center;
            transition: background-color 0.3s ease, border-color 0.3s ease;
        }
        
        .price-label {
            color: var(--text-secondary);
            font-size: 1rem;
            margin-bottom: 0.5rem;
        }
        
        .price-value {
            font-size: 3rem;
            font-weight: 800;
            color: var(--primary-color);
            margin-bottom: 1.5rem;
        }
        
        .btn-inscrire {
            width: 100%;
            background: linear-gradient(135deg, var(--primary-color), var(--primary-hover));
            color: white;
            border: none;
            padding: 1rem 2rem;
            border-radius: 12px;
            font-size: 1.2rem;
            font-weight: 700;
            cursor: pointer;
            transition: all 0.3s;
            text-decoration: none;
            display: block;
            margin-bottom: 1.5rem;
        }
        
        .btn-inscrire:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 20px rgba(168, 85, 247, 0.4);
            color: white;
        }
        
        .benefits-list {
            list-style: none;
            padding: 0;
            margin: 0;
            text-align: left;
        }
        
        .benefits-list li {
            color: var(--text-secondary);
            font-size: 0.95rem;
            margin-bottom: 0.8rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .benefits-list li:before {
            content: "✓";
            color: var(--primary-color);
            font-weight: bold;
            font-size: 1.2rem;
        }
        
        /* ========== COMMENTS & REVIEWS SECTION ========== */
        .reviews-section {
            padding: 3rem 0;
            background: var(--bg-secondary);
            transition: background-color 0.3s ease;
            margin-top: 3rem;
        }
        
        .section-header {
            text-align: center;
            margin-bottom: 3rem;
        }
        
        .section-title {
            font-size: 2.5rem;
            font-weight: 800;
            color: var(--text-primary);
            margin-bottom: 0.5rem;
        }
        
        .section-subtitle {
            color: var(--text-secondary);
            font-size: 1.1rem;
        }
        
        .reviews-container {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 2rem;
            margin-bottom: 3rem;
        }
        
        @media (max-width: 992px) {
            .reviews-container {
                grid-template-columns: 1fr;
            }
        }
        
        .reviews-column {
            display: flex;
            flex-direction: column;
            gap: 1.5rem;
        }
        
        .review-card {
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 16px;
            padding: 1.5rem;
            transition: all 0.3s;
        }
        
        .review-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 16px rgba(0,0,0,0.1);
        }
        
        .review-header {
            display: flex;
            align-items: center;
            gap: 1rem;
            margin-bottom: 1rem;
        }
        
        .review-avatar {
            width: 50px;
            height: 50px;
            background: linear-gradient(135deg, var(--primary-color), var(--primary-hover));
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: 700;
            font-size: 1.2rem;
        }
        
        .review-author {
            flex: 1;
        }
        
        .review-author-name {
            font-weight: 700;
            color: var(--text-primary);
            margin-bottom: 0.2rem;
        }
        
        .review-date {
            font-size: 0.85rem;
            color: var(--text-secondary);
        }
        
        .review-rating {
            display: flex;
            gap: 0.2rem;
            font-size: 1.2rem;
            color: #fbbf24;
        }
        
        .review-text {
            color: var(--text-secondary);
            line-height: 1.6;
            margin-top: 0.5rem;
        }
        
        .comment-card {
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 16px;
            padding: 1.5rem;
            transition: all 0.3s;
        }
        
        .comment-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 16px rgba(0,0,0,0.1);
        }
        
        .comment-header {
            display: flex;
            align-items: center;
            gap: 1rem;
            margin-bottom: 1rem;
        }
        
        .comment-avatar {
            width: 50px;
            height: 50px;
            background: linear-gradient(135deg, var(--primary-color), var(--primary-hover));
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: 700;
            font-size: 1.2rem;
        }
        
        .comment-author {
            flex: 1;
        }
        
        .comment-author-name {
            font-weight: 700;
            color: var(--text-primary);
            margin-bottom: 0.2rem;
        }
        
        .comment-date {
            font-size: 0.85rem;
            color: var(--text-secondary);
        }
        
        .comment-text {
            color: var(--text-secondary);
            line-height: 1.6;
        }
        
        .empty-state {
            text-align: center;
            padding: 3rem;
            color: var(--text-secondary);
        }
        
        .empty-state i {
            font-size: 4rem;
            margin-bottom: 1rem;
            opacity: 0.5;
        }
        
        .empty-state h4 {
            color: var(--text-primary);
            margin-bottom: 0.5rem;
        }
        
        /* ========== SHARE & INVITE SECTION ========== */
        .share-invite-card {
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 16px;
            padding: 2rem;
            margin-bottom: 2rem;
            transition: background-color 0.3s ease, border-color 0.3s ease;
        }
        
        .share-buttons {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
            gap: 1rem;
            margin-bottom: 1rem;
        }
        
        .share-btn {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
            padding: 1rem;
            border: 2px solid var(--card-border);
            border-radius: 12px;
            background: var(--bg-secondary);
            color: var(--text-primary);
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            text-decoration: none;
        }
        
        .share-btn:hover {
            transform: translateY(-3px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        
        .share-btn i {
            font-size: 1.5rem;
        }
        
        .share-btn span {
            font-size: 0.9rem;
        }
        
        .share-facebook {
            border-color: #1877f2;
            color: #1877f2;
        }
        
        .share-facebook:hover {
            background: #1877f2;
            color: white;
        }
        
        .share-twitter {
            border-color: #1da1f2;
            color: #1da1f2;
        }
        
        .share-twitter:hover {
            background: #1da1f2;
            color: white;
        }
        
        .share-linkedin {
            border-color: #0077b5;
            color: #0077b5;
        }
        
        .share-linkedin:hover {
            background: #0077b5;
            color: white;
        }
        
        .share-whatsapp {
            border-color: #25d366;
            color: #25d366;
        }
        
        .share-whatsapp:hover {
            background: #25d366;
            color: white;
        }
        
        .invite-section {
            margin-top: 2rem;
            padding-top: 2rem;
            border-top: 1px solid var(--card-border);
        }
        
        .invite-link-container {
            margin-top: 1rem;
        }
        
        .invite-link-container .input-group {
            margin-bottom: 0.5rem;
        }
        
        .invite-link-container .form-control {
            border-radius: 8px 0 0 8px;
            font-size: 0.9rem;
            background: var(--bg-secondary);
            border-color: var(--card-border);
            color: var(--text-primary);
        }
        
        .invite-link-container .btn {
            border-radius: 0 8px 8px 0;
        }
        
        .referral-code-container {
            margin-top: 1.5rem;
            animation: fadeIn 0.3s ease-in;
        }
        
        .referral-code {
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--primary-color);
            font-family: 'Courier New', monospace;
            letter-spacing: 2px;
            margin: 0.5rem 0;
            padding: 0.5rem;
            background: var(--bg-secondary);
            border-radius: 8px;
            display: inline-block;
        }
        
        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
    </style>
</head>
<body>
    <!-- NAVBAR -->
    <nav class="navbar navbar-expand-lg">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/catalogue">
                <i class="fas fa-calendar-alt"></i>
                EventHub
            </a>
            
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav mx-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/catalogue">Événements</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/catalogue#categories">Catégories</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/catalogue#about">À propos</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/catalogue#contact">Contact</a>
                    </li>
                </ul>
                
                <div class="d-flex gap-2 align-items-center">
                    <button class="theme-toggle" id="themeToggle" title="Basculer le thème">
                        <i class="fas fa-moon"></i>
                    </button>
                    <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-outline-primary">
                        Se connecter
                    </a>
                    <a href="${pageContext.request.contextPath}/register.jsp" class="btn btn-primary">
                        Ouvrir un compte
                    </a>
                </div>
            </div>
        </div>
    </nav>
    
    <c:choose>
        <c:when test="${empty evenement}">
            <div class="container my-5">
                <div class="alert alert-danger" role="alert">
                    <h4 class="alert-heading">Événement introuvable</h4>
                    <p>L'événement que vous recherchez n'existe pas ou n'est plus disponible.</p>
                    <hr>
                    <a href="${pageContext.request.contextPath}/catalogue" class="btn btn-primary">Retour au catalogue</a>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <!-- HERO BANNER -->
            <div class="hero-banner">
                <c:set var="imageUrl" value="${evenement.imageUrl}"/>
                <c:set var="finalImageUrl" value="${fn:startsWith(imageUrl, 'http') ? imageUrl : pageContext.request.contextPath.concat('/').concat(imageUrl)}"/>
                <img src="${not empty finalImageUrl ? finalImageUrl : 'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=1200'}" 
                     alt="${evenement.titre}"
                     onerror="this.src='https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=1200';">
                <div class="hero-overlay"></div>
                <a href="${pageContext.request.contextPath}/catalogue" class="btn-retour">
                    <i class="fas fa-arrow-left"></i>
                    Retour
                </a>
                <div class="hero-content">
                    <h1 class="hero-title">${evenement.titre}</h1>
                </div>
            </div>
            
            <!-- MAIN CONTENT -->
            <div class="container">
                <div class="row">
                    <!-- LEFT COLUMN: EVENT DETAILS -->
                    <div class="col-lg-8">
                        <section class="event-details-section">
                            <div class="event-info-card">
                                <h1 class="event-title">${evenement.titre}</h1>
                                
                                <c:if test="${not empty evenement.description}">
                                    <div class="event-description">
                                        ${evenement.description}
                                    </div>
                                </c:if>
                                
                                <div class="info-grid">
                                    <div class="info-card">
                                        <div class="info-icon">
                                            <i class="far fa-calendar-alt"></i>
                                        </div>
                                        <div class="info-label">Date</div>
                                        <div class="info-value">${evenement.dateDebutJour}</div>
                                    </div>
                                    
                                    <div class="info-card">
                                        <div class="info-icon">
                                            <i class="far fa-clock"></i>
                                        </div>
                                        <div class="info-label">Heure</div>
                                        <div class="info-value">${evenement.dateDebutHeure}</div>
                                    </div>
                                    
                                    <div class="info-card">
                                        <div class="info-icon">
                                            <i class="fas fa-map-marker-alt"></i>
                                        </div>
                                        <div class="info-label">Lieu</div>
                                        <div class="info-value">${evenement.lieu}</div>
                                    </div>
                                    
                                    <div class="info-card">
                                        <div class="info-icon">
                                            <i class="fas fa-users"></i>
                                        </div>
                                        <div class="info-label">Participants</div>
                                        <div class="info-value">
                                            ${evenement.nombreInscrits != null ? evenement.nombreInscrits : 0}
                                            <c:if test="${evenement.capacite != null}">
                                                / ${evenement.capacite}
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- MAP SECTION -->
                            <div class="map-container">
                                <div class="map-card">
                                    <div class="map-header">
                                        <h3>
                                            <i class="fas fa-map-marked-alt"></i>
                                            Localisation de l'événement
                                        </h3>
                                    </div>
                                    <div id="eventMap"></div>
                                </div>
                            </div>
                            
                            <!-- ORGANIZER SECTION -->
                            <div class="organizer-card">
                                <h3 style="color: var(--text-primary); font-weight: 700; margin-bottom: 1.5rem;">
                                    Organisateur
                                </h3>
                                <div class="organizer-header">
                                    <div class="organizer-icon">
                                        <i class="fas fa-user-tie"></i>
                                    </div>
                                    <div>
                                        <h4 class="organizer-name">${evenement.organisateurNom}</h4>
                                    </div>
                                </div>
                                <c:if test="${not empty evenement.organisateurEmail}">
                                    <a href="mailto:${evenement.organisateurEmail}" class="organizer-email">
                                        <i class="fas fa-envelope"></i>
                                        ${evenement.organisateurEmail}
                                    </a>
                                </c:if>
                            </div>
                            
                            <!-- SHARE & INVITE SECTION -->
                            <div class="share-invite-card">
                                <h3 style="color: var(--text-primary); font-weight: 700; margin-bottom: 1.5rem; display: flex; align-items: center; gap: 0.5rem;">
                                    <i class="fas fa-share-alt" style="color: var(--primary-color);"></i>
                                    Partager cet événement
                                </h3>
                                
                                <!-- Social Share Buttons -->
                                <div class="share-buttons">
                                    <button class="share-btn share-facebook" onclick="shareOnFacebook()" title="Partager sur Facebook">
                                        <i class="fab fa-facebook-f"></i>
                                        <span>Facebook</span>
                                    </button>
                                    <button class="share-btn share-twitter" onclick="shareOnTwitter()" title="Partager sur Twitter">
                                        <i class="fab fa-twitter"></i>
                                        <span>Twitter</span>
                                    </button>
                                    <button class="share-btn share-linkedin" onclick="shareOnLinkedIn()" title="Partager sur LinkedIn">
                                        <i class="fab fa-linkedin-in"></i>
                                        <span>LinkedIn</span>
                                    </button>
                                    <button class="share-btn share-whatsapp" onclick="shareOnWhatsApp()" title="Partager sur WhatsApp">
                                        <i class="fab fa-whatsapp"></i>
                                        <span>WhatsApp</span>
                                    </button>
                                </div>
                                
                                <!-- Invite Friend Section -->
                                <div class="invite-section">
                                    <h4 style="color: var(--text-primary); font-weight: 600; margin: 2rem 0 1rem 0; display: flex; align-items: center; gap: 0.5rem;">
                                        <i class="fas fa-user-plus" style="color: var(--primary-color);"></i>
                                        Inviter un ami
                                    </h4>
                                    <p style="color: var(--text-secondary); font-size: 0.9rem; margin-bottom: 1rem;">
                                        Partagez un lien d'invitation unique avec un code de parrainage pour inviter vos amis à cet événement.
                                    </p>
                                    
                                    <div class="invite-link-container">
                                        <div class="input-group">
                                            <input type="text" 
                                                   class="form-control" 
                                                   id="inviteLink" 
                                                   readonly 
                                                   value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/event-details?id=${evenement.evenementId}">
                                            <button class="btn btn-primary" onclick="copyInviteLink()" title="Copier le lien">
                                                <i class="fas fa-copy"></i>
                                            </button>
                                        </div>
                                        <button class="btn btn-outline-primary mt-2" onclick="generateReferralCode()" id="btnGenerateReferral">
                                            <i class="fas fa-gift me-2"></i>
                                            Générer un code de parrainage
                                        </button>
                                    </div>
                                    
                                    <!-- Referral Code Display -->
                                    <div id="referralCodeContainer" class="referral-code-container" style="display: none;">
                                        <div class="alert alert-info" role="alert">
                                            <div class="d-flex justify-content-between align-items-center">
                                                <div>
                                                    <strong><i class="fas fa-tag me-2"></i>Votre code de parrainage:</strong>
                                                    <div class="referral-code" id="referralCodeDisplay"></div>
                                                    <small class="text-muted">Partagez ce code avec vos amis pour qu'ils puissent s'inscrire avec votre parrainage.</small>
                                                </div>
                                                <button class="btn btn-sm btn-outline-secondary" onclick="copyReferralCode()" title="Copier le code">
                                                    <i class="fas fa-copy"></i>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </section>
                    </div>
                    
                    <!-- RIGHT COLUMN: REGISTRATION SIDEBAR -->
                    <div class="col-lg-4">
                        <div class="sidebar">
                            <div class="register-card">
                                <div class="price-label">Événement gratuit</div>
                                <div class="price-value">Gratuit</div>
                                <a href="${pageContext.request.contextPath}/login.jsp" class="btn-inscrire">
                                    <i class="fas fa-user-plus me-2"></i>
                                    S'inscrire à cet événement
                                </a>
                                <ul class="benefits-list">
                                    <li>Billet personnalisé envoyé par email</li>
                                    <li>Rappel automatique 24h avant l'événement</li>
                                    <li>Support client 7j/7 </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- COMMENTS & REVIEWS SECTION -->
            <section class="reviews-section">
                <div class="container">
                    <div class="section-header">
                        <h2 class="section-title">Avis et Commentaires</h2>
                        <p class="section-subtitle">
                            Découvrez ce que les participants pensent de cet événement
                        </p>
                    </div>
                    
                    <div class="reviews-container">
                        <!-- EVALUATIONS COLUMN -->
                        <div class="reviews-column">
                            <h3 style="color: var(--text-primary); font-weight: 700; margin-bottom: 1.5rem; display: flex; align-items: center; gap: 0.5rem;">
                                <i class="fas fa-star" style="color: #fbbf24;"></i>
                                Évaluations
                                <c:if test="${not empty evaluations}">
                                    <span style="font-size: 1rem; color: var(--text-secondary); font-weight: 500;">
                                        (${evaluations.size()})
                                    </span>
                                </c:if>
                            </h3>
                            
                            <c:choose>
                                <c:when test="${not empty evaluations}">
                                    <c:forEach items="${evaluations}" var="evaluation">
                                        <div class="review-card">
                                            <div class="review-header">
                                                <div class="review-avatar">
                                                    ${fn:substring(fn:toUpperCase(evaluation.participantNom), 0, 1)}
                                                </div>
                                                <div class="review-author">
                                                    <div class="review-author-name">${evaluation.participantNom}</div>
                                                    <div class="review-date">
                                                        <fmt:formatDate value="${evaluation.horodatageAsDate}" pattern="dd MMMM yyyy à HH:mm" />
                                                    </div>
                                                </div>
                                                <div class="review-rating">
                                                    ${evaluation.etoiles}
                                                </div>
                                            </div>
                                            <c:if test="${evaluation.hasTexte()}">
                                                <div class="review-text">
                                                    ${fn:escapeXml(evaluation.texte)}
                                                </div>
                                            </c:if>
                                        </div>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <div class="empty-state">
                                        <i class="fas fa-star"></i>
                                        <h4>Aucune évaluation pour le moment</h4>
                                        <p>Soyez le premier à évaluer cet événement !</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        
                        <!-- COMMENTS COLUMN -->
                        <div class="reviews-column">
                            <h3 style="color: var(--text-primary); font-weight: 700; margin-bottom: 1.5rem; display: flex; align-items: center; gap: 0.5rem;">
                                <i class="fas fa-comments" style="color: var(--primary-color);"></i>
                                Commentaires
                                <c:if test="${not empty commentaires}">
                                    <span style="font-size: 1rem; color: var(--text-secondary); font-weight: 500;">
                                        (${commentaires.size()})
                                    </span>
                                </c:if>
                            </h3>
                            
                            <c:choose>
                                <c:when test="${not empty commentaires}">
                                    <c:forEach items="${commentaires}" var="commentaire">
                                        <div class="comment-card">
                                            <div class="comment-header">
                                                <div class="comment-avatar">
                                                    ${fn:substring(fn:toUpperCase(commentaire.participantNom), 0, 1)}
                                                </div>
                                                <div class="comment-author">
                                                    <div class="comment-author-name">${commentaire.participantNom}</div>
                                                    <div class="comment-date">
                                                        <fmt:formatDate value="${commentaire.horodatageAsDate}" pattern="dd MMMM yyyy à HH:mm" />
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="comment-text">
                                                ${fn:escapeXml(commentaire.texte)}
                                            </div>
                                        </div>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <div class="empty-state">
                                        <i class="fas fa-comments"></i>
                                        <h4>Aucun commentaire pour le moment</h4>
                                        <p>Soyez le premier à commenter cet événement !</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </section>
        </c:otherwise>
    </c:choose>
    
    <!-- FOOTER -->
    <footer class="footer" style="background: var(--text-dark); color: white; padding: 2rem 0; text-align: center; margin-top: 4rem;">
        <div class="container">
            <p>© 2025 EventHub. Tous droits réservés.</p>
        </div>
    </footer>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Leaflet JS for Map -->
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <!-- Global Theme Manager -->
    <script src="${pageContext.request.contextPath}/js/theme-manager.js"></script>
    <!-- Social Share & Invite -->
    <script src="${pageContext.request.contextPath}/js/social-share.js"></script>
    
    <c:if test="${not empty evenement}">
        <div id="eventData"
             data-lat="${evenement.latitude != null ? evenement.latitude : ''}"
             data-lng="${evenement.longitude != null ? evenement.longitude : ''}"
             data-lieu="${fn:escapeXml(evenement.lieu)}"
             data-titre="${fn:escapeXml(evenement.titre)}"
             hidden></div>

        <script>
        // ========== MAP INITIALIZATION ==========
        var dataElement = document.getElementById('eventData');
        if (dataElement) {
            var eventLatitude = parseFloat(dataElement.dataset.lat) || 48.8566;
            var eventLongitude = parseFloat(dataElement.dataset.lng) || 2.3522;
            var eventLieu = dataElement.dataset.lieu || '';
            var eventTitre = dataElement.dataset.titre || '';
        
        // Initialize map only if coordinates are available
        if (eventLatitude != null && eventLongitude != null) {
            // Initialize the map
            var map = L.map('eventMap').setView([eventLatitude, eventLongitude], 13);
            
            // Add OpenStreetMap tiles
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
                maxZoom: 19
            }).addTo(map);
            
            // Add marker for event location
            var marker = L.marker([eventLatitude, eventLongitude]).addTo(map);
            marker.bindPopup('<b>' + eventTitre + '</b><br>' + eventLieu).openPopup();
        } else {
            // If no coordinates, show a message
            document.getElementById('eventMap').innerHTML = 
                '<div style="display: flex; align-items: center; justify-content: center; height: 100%; color: var(--text-secondary);">' +
                '<div style="text-align: center;"><i class="fas fa-map-marked-alt" style="font-size: 3rem; margin-bottom: 1rem;"></i><br>Carte non disponible</div>' +
                '</div>';
        }
        }
        </script>
    </c:if>
</body>
</html>

