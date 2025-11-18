<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Découvrez les meilleurs événements - EventHub</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <%@ include file="/WEB-INF/jspf/theme-head.jspf" %>
    
    <style>
        /* Catalogue-specific styles */
        
        /* ========== HERO SECTION ========== */
        .hero-section {
            background: linear-gradient(180deg, #926fab 0%, var(--bg-primary) 100%);
            text-align: center;
            transition: background 0.3s ease;
            padding: 3.5rem 0 3rem;
        }
        
        .hero-title {
            font-size: 3.5rem;
            font-weight: 800;
            color: var(--text-primary);
            margin-bottom: 1rem;
            line-height: 1.2;
            margin-top: 1.5rem;
        }
        
        .hero-title .highlight {
            color: var(--primary-color);
        }
        
        .hero-subtitle {
            font-size: 1.1rem;
            color: var(--text-secondary);
            max-width: 650px;
            margin: 0 auto 2.5rem;
        }
        
        .search-bar {
            max-width: 700px;
            margin: 0 auto;
            position: relative;
        }
        
        .search-bar input {
            width: 100%;
            padding: 1rem 3.5rem 1rem 1.5rem;
            border: 2px solid var(--card-border);
            border-radius: 12px;
            font-size: 1rem;
            transition: all 0.3s;
            background: var(--card-bg);
            color: var(--text-primary);
        }
        
        .search-bar input::placeholder {
            color: var(--text-secondary);
        }
        
        .search-bar input:focus {
            outline: none;
            border-color: var(--primary-color);
            box-shadow: 0 0 0 3px rgba(168, 85, 247, 0.1);
        }
        
        .search-bar button {
            position: absolute;
            right: 8px;
            top: 50%;
            transform: translateY(-50%);
            background: var(--primary-color);
            border: none;
            padding: 0.7rem 1.5rem;
            border-radius: 8px;
            color: white;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .search-bar button:hover {
            background: var(--primary-hover);
        }
        
        /* ========== CATEGORY FILTERS ========== */
        .category-filters {
            padding: 2rem 0;
            background: var(--bg-primary);
            border-bottom: 1px solid var(--card-border);
            transition: background-color 0.3s ease;
        }
        
        .filter-pills {
            display: flex;
            gap: 0.8rem;
            flex-wrap: wrap;
            justify-content: center;
        }
        
        .filter-pill {
            padding: 0.7rem 1.5rem;
            border: 2px solid var(--card-border);
            border-radius: 50px;
            background: var(--card-bg);
            color: var(--text-primary);
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            text-decoration: none;
        }
        
        .filter-pill:hover {
            border-color: var(--primary-color);
            color: var(--primary-color);
            transform: translateY(-2px);
            text-decoration: none;
        }
        
        .filter-pill.active {
            background: var(--primary-color);
            border-color: var(--primary-color);
            color: white;
        }
        
        .filter-pill.active:hover {
            color: white;
        }
        
        .filter-pill i {
            font-size: 1.1rem;
        }
        
        /* ========== EVENT CARDS ========== */
        .events-section {
            padding: 3rem 0;
            background: var(--bg-primary);
            transition: background-color 0.3s ease;
        }
        
        .events-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
            gap: 2rem;
        }
        
        .event-card {
            background: var(--card-bg);
            border-radius: 16px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            transition: all 0.3s;
            position: relative;
            border: 1px solid var(--card-border);
        }
        
        .event-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 12px 24px rgba(0,0,0,0.15);
        }
        
        .event-image {
            width: 100%;
            height: 220px;
            object-fit: cover;
            position: relative;
        }
        
        .event-badge {
            position: absolute;
            top: 1rem;
            right: 1rem;
            background: var(--primary-color);
            color: white;
            padding: 0.4rem 1rem;
            border-radius: 50px;
            font-size: 0.85rem;
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 0.3rem;
        }
        
        .event-content {
            padding: 1.5rem;
        }
        
        .event-title {
            font-size: 1.3rem;
            font-weight: 700;
            color: var(--text-primary);
            margin-bottom: 0.8rem;
            line-height: 1.3;
        }
        
        .event-description {
            color: var(--text-secondary);
            font-size: 0.95rem;
            margin-bottom: 1rem;
            line-height: 1.5;
        }
        
        .event-meta {
            display: flex;
            flex-direction: column;
            gap: 0.5rem;
            margin-bottom: 1.2rem;
        }
        
        .event-meta-item {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            color: var(--text-secondary);
            font-size: 0.9rem;
        }
        
        .event-meta-item i {
            color: var(--primary-color);
            width: 16px;
        }
        
        .event-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding-top: 1rem;
            border-top: 1px solid var(--card-border);
        }
        
        .event-price {
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--primary-color);
        }
        
        .btn-reserve {
            background: var(--primary-color);
            color: white;
            border: none;
            padding: 0.7rem 1.5rem;
            border-radius: 8px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .btn-reserve:hover {
            background: var(--primary-hover);
            transform: scale(1.05);
        }
        
        /* ========== SERVICES SECTION ========== */
        .services-section {
            padding: 4rem 0;
            background: var(--bg-primary);
            transition: background-color 0.3s ease;
        }
        
        .services-section h2 {
            color: var(--text-primary);
            font-size: 2.5rem;
            font-weight: 800;
            margin-bottom: 0.5rem;
            text-align: center;
        }
        
        .services-section .highlight {
            color: var(--primary-color);
        }
        
        .services-subtitle {
            color: var(--text-secondary);
            font-size: 1.1rem;
            text-align: center;
            margin-bottom: 3rem;
        }
        
        .services-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 2rem;
        }
        
        .service-card {
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 16px;
            padding: 2rem;
            text-align: center;
            transition: all 0.3s;
        }
        
        .service-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 12px 24px rgba(168, 85, 247, 0.15);
            border-color: var(--primary-color);
        }
        
        .service-icon {
            width: 80px;
            height: 80px;
            background: linear-gradient(135deg, var(--primary-color), var(--primary-hover));
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 2rem;
            color: white;
            margin: 0 auto 1.5rem;
        }
        
        .service-card h3 {
            color: var(--text-primary);
            font-size: 1.3rem;
            font-weight: 700;
            margin-bottom: 1rem;
        }
        
        .service-card p {
            color: var(--text-secondary);
            font-size: 0.95rem;
            line-height: 1.6;
        }

        /* ========== ABOUT SECTION ========== */
        .about-section {
            padding: 4rem 0;
            background: var(--bg-secondary);
            text-align: center;
        }

        .about-section h2 {
            color: var(--text-primary);
            font-size: 2.5rem;
            font-weight: 800;
            margin-bottom: 0.5rem;
        }

        .about-section p.section-subtitle {
            color: var(--text-secondary);
            font-size: 1.1rem;
            margin-bottom: 3rem;
        }

        .about-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
            gap: 1.5rem;
        }

        .about-card {
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 16px;
            padding: 2rem;
            text-align: left;
            transition: all 0.3s;
        }

        .about-card:hover {
            transform: translateY(-6px);
            box-shadow: 0 10px 20px rgba(0,0,0,0.08);
            border-color: var(--primary-color);
        }

        .about-card i {
            font-size: 2rem;
            color: var(--primary-color);
            margin-bottom: 1rem;
        }

        .about-card h3 {
            color: var(--text-primary);
            font-size: 1.3rem;
            margin-bottom: 0.5rem;
        }

        .about-card p {
            color: var(--text-secondary);
            line-height: 1.5;
        }

        /* ========== CONTACT SECTION ========== */
        .contact-section {
            padding: 4rem 0;
            background: var(--bg-primary);
        }

        .contact-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 2rem;
        }

        .contact-card {
            background: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 16px;
            padding: 2rem;
            height: 100%;
        }

        .contact-card h3 {
            color: var(--text-primary);
            font-size: 1.4rem;
            margin-bottom: 1rem;
            font-weight: 700;
        }

        .contact-info-list {
            list-style: none;
            padding: 0;
            margin: 0;
            display: flex;
            flex-direction: column;
            gap: 1.2rem;
        }

        .contact-info-list li {
            display: flex;
            align-items: center;
            gap: 0.8rem;
            color: var(--text-secondary);
        }

        .contact-info-list i {
            width: 36px;
            height: 36px;
            border-radius: 50%;
            background: rgba(168, 85, 247, 0.1);
            color: var(--primary-color);
            display: inline-flex;
            align-items: center;
            justify-content: center;
        }

        .contact-form {
            display: flex;
            flex-direction: column;
            gap: 1rem;
        }

        .contact-form input,
        .contact-form textarea {
            border-radius: 10px;
            border: 1px solid var(--card-border);
            padding: 0.85rem 1rem;
            font-size: 1rem;
            background: var(--bg-secondary);
            color: var(--text-primary);
        }

        .contact-form textarea {
            min-height: 130px;
            resize: vertical;
        }

        .contact-form button {
            align-self: flex-start;
            background: linear-gradient(135deg, var(--primary-color), var(--primary-hover));
            color: #fff;
            border: none;
            border-radius: 10px;
            padding: 0.9rem 2rem;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.3s, box-shadow 0.3s;
        }

        .contact-form button:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 16px rgba(0,0,0,0.12);
        }
        
        /* ========== FOOTER ========== */
        .footer {
            background: var(--text-dark);
            color: white;
            padding: 2rem 0;
            text-align: center;
            margin-top: 4rem;
        }
        
        .footer p {
            margin: 0;
            opacity: 0.8;
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
    
    <!-- HERO SECTION -->
    <section class="hero-section">
        <div class="container">
            <h1 class="hero-title">
                Découvrez les Meilleurs<br>
                <span class="highlight">Événements</span>
            </h1>
            <p class="hero-subtitle">
                Concerts, expositions, théâtre et bien plus encore. Trouvez l'événement parfait pour vous.
            </p>
            
            <form action="${pageContext.request.contextPath}/catalogue" method="get" class="search-bar">
                <input type="text" name="search" placeholder="Rechercher un événement..." value="${param.search}">
                <button type="submit"><i class="fas fa-search"></i></button>
            </form>
        </div>
    </section>
    
    <!-- CATEGORY FILTERS -->
    <section id="categories" class="category-filters">
        <div class="container">
            <form action="${pageContext.request.contextPath}/catalogue" method="get" id="categoryForm">
                <div class="filter-pills">
                    <a href="${pageContext.request.contextPath}/catalogue" class="filter-pill ${empty param.categorie ? 'active' : ''}">
                        <span>Tous</span>
                    </a>
                    <c:forEach items="${categories}" var="categorie">
                        <a href="${pageContext.request.contextPath}/catalogue?categorie=${categorie.id}" 
                           class="filter-pill ${param.categorie == categorie.id ? 'active' : ''}">
                            <i class="fas fa-tag"></i>
                            <span>${categorie.nom}</span>
                        </a>
                    </c:forEach>
                </div>
            </form>
        </div>
    </section>
    
    <!-- EVENTS GRID -->
    <section class="events-section">
        <div class="container">
            <c:choose>
                <c:when test="${empty evenements}">
                    <div class="text-center py-5">
                        <i class="fas fa-calendar-times" style="font-size: 4rem; color: var(--text-light);"></i>
                        <h3 class="mt-3">Aucun événement trouvé</h3>
                        <p class="text-muted">Essayez de modifier vos critères de recherche</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="events-grid">
                        <c:forEach items="${evenements}" var="evenement">
                            <a href="${pageContext.request.contextPath}/event-details?id=${evenement.evenementId}" 
                               class="event-card" style="text-decoration: none; color: inherit; cursor: pointer;">
                                <div style="position: relative;">
                                    <c:set var="imageUrl" value="${evenement.imageUrl}"/>
                                    <c:set var="finalImageUrl" value="${fn:startsWith(imageUrl, 'http') ? imageUrl : pageContext.request.contextPath.concat('/').concat(imageUrl)}"/>
                                    <img src="${not empty finalImageUrl ? finalImageUrl : 'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=800'}" 
                                         alt="${evenement.titre}" 
                                         class="event-image"
                                         onerror="this.src='https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=800';">
                                    <c:if test="${not empty evenement.categories}">
                                        <c:forEach items="${evenement.categories}" var="cat" varStatus="status" begin="0" end="0">
                                            <div class="event-badge">
                                                <i class="fas fa-tag"></i>
                                                ${cat}
                                            </div>
                                        </c:forEach>
                                    </c:if>
                                </div>
                                
                                <div class="event-content">
                                    <h3 class="event-title">${evenement.titre}</h3>
                                    
                                    <c:if test="${not empty evenement.description}">
                                        <p class="event-description">
                                            <c:choose>
                                                <c:when test="${evenement.description.length() > 100}">
                                                    ${evenement.description.substring(0, 100)}...
                                                </c:when>
                                                <c:otherwise>
                                                    ${evenement.description}
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                    </c:if>
                                    
                                    <div class="event-meta">
                                        <div class="event-meta-item">
                                            <i class="far fa-calendar"></i>
                                            <span>${evenement.dateDebutJour}</span>
                                        </div>
                                        <div class="event-meta-item">
                                            <i class="far fa-clock"></i>
                                            <span>${evenement.dateDebutHeure}</span>
                                        </div>
                                        <div class="event-meta-item">
                                            <i class="fas fa-map-marker-alt"></i>
                                            <span>
                                                <c:choose>
                                                    <c:when test="${evenement.lieu.length() > 40}">
                                                        ${evenement.lieu.substring(0, 40)}...
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${evenement.lieu}
                                                    </c:otherwise>
                                                </c:choose>
                                            </span>
                                        </div>
                                    </div>
                                    
                                    <div class="event-footer">
                                        <div class="event-price">Gratuit</div>
                                        <span class="btn-reserve">
                                            Détails
                                        </span>
                                    </div>
                                </div>
                            </a>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>
    
    <!-- ABOUT SECTION -->
    <section id="about" class="about-section">
        <div class="container">
            <h2>À propos d'EventHub</h2>
            <p class="section-subtitle">
                Nous regroupons organisateurs, partenaires et participants pour offrir des expériences événementielles fluides et modernes.
            </p>
            <div class="about-grid">
                <div class="about-card">
                    <i class="fas fa-bolt"></i>
                    <h3>Découverte rapide</h3>
                    <p>Des filtres intelligents, une recherche instantanée et des recommandations personnalisées pour trouver l'événement idéal.</p>
                </div>
                <div class="about-card">
                    <i class="fas fa-shield-check"></i>
                    <h3>Organisation fiable</h3>
                    <p>Des organisateurs vérifiés, un suivi en temps réel et des tableaux de bord dédiés pour piloter vos programmations.</p>
                </div>
                <div class="about-card">
                    <i class="fas fa-people-group"></i>
                    <h3>Communauté engagée</h3>
                    <p>Notes, commentaires et partages sociaux pour dynamiser vos événements et impliquer votre audience.</p>
                </div>
            </div>
        </div>
    </section>
    
    <!-- SERVICES SECTION -->
    <section class="services-section">
        <div class="container">
            <h2>Pourquoi Choisir <span class="highlight">EventHub</span></h2>
            <p class="services-subtitle">Profitez d'une expérience complète pour découvrir et gérer vos événements préférés</p>
            
            <div class="services-grid">
                <div class="service-card">
                    <div class="service-icon">
                        <i class="fas fa-compass"></i>
                    </div>
                    <h3>Découverte Facilitée</h3>
                    <p>Explorez des milliers d'événements adaptés à vos goûts et préférences avec nos filtres intelligents.</p>
                </div>
                
                <div class="service-card">
                    <div class="service-icon">
                        <i class="fas fa-clock"></i>
                    </div>
                    <h3>Réservation Rapide</h3>
                    <p>Inscrivez-vous en quelques clics et recevez votre confirmation instantanément.</p>
                </div>
                
                <div class="service-card">
                    <div class="service-icon">
                        <i class="fas fa-shield-alt"></i>
                    </div>
                    <h3>Gestion Simplifiée</h3>
                    <p>Gérez toutes vos participations depuis votre tableau de bord personnalisé.</p>
                </div>
                
                <div class="service-card">
                    <div class="service-icon">
                        <i class="fas fa-bell"></i>
                    </div>
                    <h3>Notifications en Temps Réel</h3>
                    <p>Restez informé des nouveautés et des changements de dernière minute.</p>
                </div>
            </div>
        </div>
    </section>
    
    <!-- CONTACT SECTION -->
    <section id="contact" class="contact-section">
        <div class="container">
            <div class="section-header" style="text-align: center; margin-bottom: 2rem;">
                <h2 style="font-size: 2.5rem; font-weight: 800; color: var(--text-primary);">Contactez-nous</h2>
                <p style="color: var(--text-secondary); font-size: 1.1rem;">Besoin d'aide pour planifier un événement ou en savoir plus sur nos offres ? Nous sommes disponibles.</p>
            </div>
            <div class="contact-grid">
                <div class="contact-card">
                    <h3>Nos coordonnées</h3>
                    <ul class="contact-info-list">
                        <li>
                            <i class="fas fa-envelope"></i>
                            support@eventhub.com
                        </li>
                        <li>
                            <i class="fas fa-phone"></i>
                            +33 1 84 80 12 32
                        </li>
                        <li>
                            <i class="fas fa-location-dot"></i>
                            42 Rue des Innovateurs, 75010 Paris
                        </li>
                        <li>
                            <i class="fas fa-clock"></i>
                            Assistance 7j/7 de 8h à 20h
                        </li>
                    </ul>
                </div>
                <div class="contact-card">
                    <h3>Envoyer un message</h3>
                    <form class="contact-form">
                        <input type="text" placeholder="Votre nom" required>
                        <input type="email" placeholder="Votre email" required>
                        <textarea placeholder="Expliquez-nous votre besoin..." required></textarea>
                        <button type="submit">Envoyer</button>
                    </form>
                </div>
            </div>
        </div>
    </section>
    
    <!-- FOOTER -->
    <footer class="footer">
        <div class="container">
            <p>© 2025 EventHub. Tous droits réservés.</p>
        </div>
    </footer>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Global Theme Manager -->
    <script src="${pageContext.request.contextPath}/js/theme-manager.js"></script>
</body>
</html>
