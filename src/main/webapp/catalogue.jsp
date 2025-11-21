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
    
    <style>
        :root {
            --primary-color: #a855f7;
            --primary-hover: #9333ea;
            --text-dark: #1f2937;
            --text-light: #6b7280;
            --bg-light: #f9fafb;
            --border-color: #e5e7eb;
        }
        
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Inter', sans-serif;
            color: var(--text-dark);
            background-color: #fff;
        }
        
        /* ========== NAVBAR ========== */
        .navbar {
            background: #fff;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            padding: 1rem 0;
        }
        
        .navbar-brand {
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--primary-color) !important;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .navbar-brand i {
            font-size: 1.8rem;
        }
        
        .nav-link {
            color: var(--text-dark) !important;
            font-weight: 500;
            margin: 0 0.8rem;
            transition: color 0.3s;
        }
        
        .nav-link:hover {
            color: var(--primary-color) !important;
        }
        
        .btn-outline-primary {
            border: 2px solid var(--primary-color);
            color: var(--primary-color);
            padding: 0.5rem 1.2rem;
            border-radius: 8px;
            font-weight: 600;
            transition: all 0.3s;
        }
        
        .btn-outline-primary:hover {
            background: var(--primary-color);
            color: white;
        }
        
        .btn-primary {
            background: var(--primary-color);
            border: none;
            padding: 0.5rem 1.2rem;
            border-radius: 8px;
            font-weight: 600;
            transition: all 0.3s;
        }
        
        .btn-primary:hover {
            background: var(--primary-hover);
            transform: translateY(-2px);
        }
        
        /* ========== HERO SECTION ========== */
        .hero-section {
            background: linear-gradient(180deg, #faf5ff 0%, #ffffff 100%);
            padding: 5rem 0 4rem;
            text-align: center;
        }
        
        .hero-title {
            font-size: 3.5rem;
            font-weight: 800;
            color: var(--text-dark);
            margin-bottom: 1rem;
            line-height: 1.2;
        }
        
        .hero-title .highlight {
            color: var(--primary-color);
        }
        
        .hero-subtitle {
            font-size: 1.1rem;
            color: var(--text-light);
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
            border: 2px solid var(--border-color);
            border-radius: 12px;
            font-size: 1rem;
            transition: all 0.3s;
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
            background: white;
            border-bottom: 1px solid var(--border-color);
        }
        
        .filter-pills {
            display: flex;
            gap: 0.8rem;
            flex-wrap: wrap;
            justify-content: center;
        }
        
        .filter-pill {
            padding: 0.7rem 1.5rem;
            border: 2px solid var(--border-color);
            border-radius: 50px;
            background: white;
            color: var(--text-dark);
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
            background: var(--bg-light);
        }
        
        .events-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
            gap: 2rem;
        }
        
        .event-card {
            background: white;
            border-radius: 16px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            transition: all 0.3s;
            position: relative;
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
            color: var(--text-dark);
            margin-bottom: 0.8rem;
            line-height: 1.3;
        }
        
        .event-description {
            color: var(--text-light);
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
            color: var(--text-light);
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
            border-top: 1px solid var(--border-color);
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

        /* ========== ABOUT SECTION ========== */
        .about-section {
            padding: 5rem 0;
            background: var(--bg-light);
        }

        .about-section h2 {
            font-size: 2.5rem;
            font-weight: 700;
            color: var(--text-dark);
            margin-bottom: 1.5rem;
            text-align: center;
        }

        .about-section .highlight {
            color: var(--primary-color);
        }

        .about-content {
            max-width: 800px;
            margin: 0 auto;
            text-align: center;
            line-height: 1.8;
            color: var(--text-light);
            font-size: 1.1rem;
        }

        .about-features {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 2rem;
            margin-top: 3rem;
        }

        .feature-card {
            background: white;
            padding: 2rem;
            border-radius: 12px;
            text-align: center;
            transition: transform 0.3s;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .feature-card:hover {
            transform: translateY(-5px);
        }

        .feature-card i {
            font-size: 3rem;
            color: var(--primary-color);
            margin-bottom: 1rem;
        }

        .feature-card h4 {
            font-weight: 600;
            color: var(--text-dark);
            margin-bottom: 0.8rem;
        }

        .feature-card p {
            color: var(--text-light);
            margin: 0;
        }

        /* ========== CONTACT SECTION ========== */
        .contact-section {
            padding: 5rem 0;
            background: white;
        }

        .contact-section h2 {
            font-size: 2.5rem;
            font-weight: 700;
            color: var(--text-dark);
            margin-bottom: 1rem;
            text-align: center;
        }

        .contact-section .subtitle {
            text-align: center;
            color: var(--text-light);
            margin-bottom: 3rem;
        }

        .contact-form {
            max-width: 600px;
            margin: 0 auto;
            background: var(--bg-light);
            padding: 2.5rem;
            border-radius: 12px;
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
            color: var(--text-dark);
        }

        .form-group input,
        .form-group textarea {
            width: 100%;
            padding: 0.8rem;
            border: 1px solid var(--border-color);
            border-radius: 8px;
            font-family: inherit;
            transition: border-color 0.3s;
        }

        .form-group input:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: var(--primary-color);
        }

        .form-group textarea {
            resize: vertical;
            min-height: 120px;
        }

        .btn-submit {
            width: 100%;
            background: var(--primary-color);
            color: white;
            border: none;
            padding: 1rem;
            border-radius: 8px;
            font-weight: 600;
            font-size: 1rem;
            cursor: pointer;
            transition: all 0.3s;
        }

        .btn-submit:hover {
            background: var(--primary-hover);
            transform: translateY(-2px);
        }

        /* ========== FOOTER ========== */
        .footer {
            background: var(--text-dark);
            color: white;
            padding: 2rem 0;
            text-align: center;
            margin-top: 0;
        }

        .footer p {
            margin: 0;
            opacity: 0.8;
        }

        /* ========== SMOOTH SCROLL ========== */
        html {
            scroll-behavior: smooth;
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
                        <a class="nav-link" href="#apropos">À propos</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#contact">Contact</a>
                    </li>
                </ul>

                <div class="d-flex gap-2">
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
    <section class="category-filters">
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
                            <div class="event-card">
                                <div style="position: relative;">
                                    <img src="${not empty evenement.imageUrl ? pageContext.request.contextPath.concat('/').concat(evenement.imageUrl) : 'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=800'}" 
                                         alt="${evenement.titre}" 
                                         class="event-image">
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
                                            <span>${evenement.dateDebut}</span>
                                        </div>
                                        <div class="event-meta-item">
                                            <i class="far fa-clock"></i>
                                            <span>19h30</span>
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
                                        <button class="btn-reserve view-details"
                                                data-bs-toggle="modal"
                                                data-bs-target="#eventModal"
                                                data-id="${evenement.evenementId}"
                                                data-titre="${fn:escapeXml(evenement.titre)}"
                                                data-description="${fn:escapeXml(evenement.description)}"
                                                data-datedebut="${evenement.dateDebut}"
                                                data-datefin="${evenement.dateFin}"
                                                data-lieu="${fn:escapeXml(evenement.lieu)}"
                                                data-capacite="${evenement.capacite}">
                                            Détails
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>
    
    <!-- MODAL DETAILS -->
    <div class="modal fade" id="eventModal" tabindex="-1" aria-labelledby="eventModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-lg">
            <div class="modal-content">
                <div class="modal-header" style="border-bottom: 2px solid var(--primary-color);">
                    <h5 class="modal-title" id="eventModalLabel" style="color: var(--primary-color); font-weight: 700;">Détails de l'événement</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body" style="padding: 2rem;">
                    <h4 id="modalEventTitle" style="color: var(--text-dark); margin-bottom: 1.5rem;"></h4>
                    
                    <div style="margin-bottom: 1.5rem;">
                        <h6 style="color: var(--primary-color); font-weight: 600; margin-bottom: 0.5rem;">
                            <i class="fas fa-info-circle me-2"></i>Description
                        </h6>
                        <p id="modalEventDescription" style="color: var(--text-light); line-height: 1.6;"></p>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6" style="margin-bottom: 1rem;">
                            <h6 style="color: var(--primary-color); font-weight: 600; margin-bottom: 0.5rem;">
                                <i class="far fa-calendar-alt me-2"></i>Date et heure
                            </h6>
                            <p style="color: var(--text-light);">
                                <span id="modalEventDateDebut"></span> - <span id="modalEventDateFin"></span>
                            </p>
                        </div>
                        <div class="col-md-6" style="margin-bottom: 1rem;">
                            <h6 style="color: var(--primary-color); font-weight: 600; margin-bottom: 0.5rem;">
                                <i class="fas fa-map-marker-alt me-2"></i>Lieu
                            </h6>
                            <p id="modalEventLieu" style="color: var(--text-light);"></p>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6">
                            <h6 style="color: var(--primary-color); font-weight: 600; margin-bottom: 0.5rem;">
                                <i class="fas fa-users me-2"></i>Capacité
                            </h6>
                            <p id="modalEventCapacite" style="color: var(--text-light);"></p>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" data-bs-dismiss="modal">
                        <i class="fas fa-times me-1"></i>Fermer
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- À PROPOS SECTION -->
    <section id="apropos" class="about-section">
        <div class="container">
            <h2>À propos <span class="highlight">EventHub</span></h2>

            <div class="about-content">
                <p>
                    EventHub est votre plateforme de gestion d'événements qui simplifie la découverte,
                    l'organisation et la participation à des événements. Notre mission est de connecter
                    les organisateurs avec les participants et de créer des expériences mémorables.
                </p>
            </div>

            <div class="about-features">
                <div class="feature-card">
                    <i class="fas fa-search"></i>
                    <h4>Découverte facile</h4>
                    <p>Trouvez rapidement les événements qui vous intéressent grâce à nos filtres de recherche intuitifs</p>
                </div>

                <div class="feature-card">
                    <i class="fas fa-calendar-check"></i>
                    <h4>Inscription simplifiée</h4>
                    <p>Réservez votre place en quelques clics et gérez toutes vos inscriptions au même endroit</p>
                </div>

                <div class="feature-card">
                    <i class="fas fa-users"></i>
                    <h4>Communauté active</h4>
                    <p>Partagez vos avis et commentaires pour enrichir l'expérience de tous les participants</p>
                </div>
            </div>
        </div>
    </section>

    <!-- CONTACT SECTION -->
    <section id="contact" class="contact-section">
        <div class="container">
            <h2>Contactez-nous</h2>
            <p class="subtitle">Une question ? Une suggestion ? N'hésitez pas à nous contacter</p>

            <form class="contact-form" method="POST" action="${pageContext.request.contextPath}/contact">
                <div class="form-group">
                    <label for="contactName">Nom complet</label>
                    <input type="text" id="contactName" name="nom" required placeholder="Votre nom">
                </div>

                <div class="form-group">
                    <label for="contactEmail">Email</label>
                    <input type="email" id="contactEmail" name="email" required placeholder="votre.email@exemple.com">
                </div>

                <div class="form-group">
                    <label for="contactSubject">Sujet</label>
                    <input type="text" id="contactSubject" name="sujet" required placeholder="Objet de votre message">
                </div>

                <div class="form-group">
                    <label for="contactMessage">Message</label>
                    <textarea id="contactMessage" name="message" required placeholder="Votre message..."></textarea>
                </div>

                <button type="submit" class="btn-submit">
                    <i class="fas fa-paper-plane me-2"></i>Envoyer le message
                </button>
            </form>
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
    
    <script>
        // Modal details
        document.addEventListener('DOMContentLoaded', function() {
            document.querySelectorAll('.view-details').forEach(button => {
                button.addEventListener('click', function() {
                    const eventData = {
                        titre: this.getAttribute('data-titre'),
                        description: this.getAttribute('data-description'),
                        dateDebut: this.getAttribute('data-datedebut'),
                        dateFin: this.getAttribute('data-datefin'),
                        lieu: this.getAttribute('data-lieu'),
                        capacite: this.getAttribute('data-capacite')
                    };
                    
                    document.getElementById('modalEventTitle').textContent = eventData.titre;
                    document.getElementById('modalEventDescription').textContent = eventData.description || 'Aucune description disponible';
                    document.getElementById('modalEventDateDebut').textContent = eventData.dateDebut;
                    document.getElementById('modalEventDateFin').textContent = eventData.dateFin;
                    document.getElementById('modalEventLieu').textContent = eventData.lieu;
                    document.getElementById('modalEventCapacite').textContent = eventData.capacite + ' personnes';
                });
            });
        });
    </script>
</body>
</html>
