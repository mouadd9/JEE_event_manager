<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Accueil" scope="request"/>

<jsp:include page="layout/header.jsp"/>
<jsp:include page="layout/messages.jsp"/>

<!-- Hero Section -->
<section class="bg-primary text-white py-5">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-lg-7">
                <h1 class="display-4 fw-bold mb-3">Découvrez les Meilleurs Événements</h1>
                <p class="lead mb-4">
                    Participez à des événements incroyables, rencontrez de nouvelles personnes 
                    et créez des souvenirs inoubliables.
                </p>

                <!-- Search Form -->
                <form action="${pageContext.request.contextPath}/events/browse" method="get" class="mb-4">
                    <div class="input-group input-group-lg shadow">
                        <input type="text" class="form-control" name="keyword" 
                               placeholder="Rechercher un événement..." 
                               aria-label="Rechercher">
                        <button class="btn btn-light" type="submit">
                            <i class="fas fa-search"></i> Rechercher
                        </button>
                    </div>
                </form>

                <div class="d-flex gap-3">
                    <a href="${pageContext.request.contextPath}/events/browse" 
                       class="btn btn-light btn-lg">
                        <i class="fas fa-calendar-alt"></i> Parcourir les événements
                    </a>
                    <c:if test="${empty sessionScope.currentUser}">
                        <a href="${pageContext.request.contextPath}/register" 
                           class="btn btn-outline-light btn-lg">
                            <i class="fas fa-user-plus"></i> S'inscrire
                        </a>
                    </c:if>
                </div>
            </div>
            <div class="col-lg-5 text-center d-none d-lg-block">
                <i class="fas fa-calendar-check" style="font-size: 15rem; opacity: 0.2;"></i>
            </div>
        </div>
    </div>
</section>

<!-- Popular Events Section -->
<section class="py-5">
    <div class="container">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="mb-0">
                <i class="fas fa-fire text-danger"></i> Événements Populaires
            </h2>
            <a href="${pageContext.request.contextPath}/events/browse" 
               class="btn btn-outline-primary">
                Voir tout <i class="fas fa-arrow-right"></i>
            </a>
        </div>

        <c:choose>
            <c:when test="${not empty popularEvents}">
                <div class="row g-4">
                    <c:forEach items="${popularEvents}" var="event" begin="0" end="5">
                        <div class="col-md-6 col-lg-4">
                            <div class="card h-100 shadow-sm border-0 hover-shadow">
                                <!-- Event Image/Icon -->
                                <c:choose>
                                    <c:when test="${not empty event.imageUrl}">
                                        <img src="${event.imageUrl}" 
                                             class="card-img-top" 
                                             alt="<c:out value='${event.titre}'/>"
                                             style="height: 200px; object-fit: cover;">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="card-img-top bg-gradient-primary d-flex align-items-center justify-content-center" 
                                             style="height: 200px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                                            <i class="fas fa-calendar-alt fa-4x text-white opacity-75"></i>
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                                <div class="card-body">
                                    <h5 class="card-title">
                                        <c:out value="${event.titre}"/>
                                    </h5>
                                    
                                    <p class="card-text text-muted small mb-2">
                                        <i class="fas fa-map-marker-alt text-danger"></i>
                                        <c:out value="${event.lieu}"/>
                                    </p>
                                    
                                    <p class="card-text small mb-2">
                                        <i class="fas fa-calendar text-primary"></i>
                                        ${event.dateDebut.dayOfMonth < 10 ? '0' : ''}${event.dateDebut.dayOfMonth}/${event.dateDebut.monthValue < 10 ? '0' : ''}${event.dateDebut.monthValue}/${event.dateDebut.year}
                                        à
                                        ${event.dateDebut.hour < 10 ? '0' : ''}${event.dateDebut.hour}:${event.dateDebut.minute < 10 ? '0' : ''}${event.dateDebut.minute}
                                    </p>

                                    <!-- Categories -->
                                    <c:if test="${not empty event.categories}">
                                        <div class="mb-3">
                                            <c:forEach items="${event.categories}" var="categorie" varStatus="status">
                                                <c:if test="${status.index < 2}">
                                                    <span class="badge bg-secondary me-1">
                                                        <c:out value="${categorie.nom}"/>
                                                    </span>
                                                </c:if>
                                            </c:forEach>
                                            <c:if test="${fn:length(event.categories) > 2}">
                                                <span class="badge bg-light text-dark">+${fn:length(event.categories) - 2}</span>
                                            </c:if>
                                        </div>
                                    </c:if>

                                    <!-- Capacity Bar -->
                                    <div class="mb-3">
                                        <div class="d-flex justify-content-between mb-1">
                                            <small class="text-muted">Places disponibles</small>
                                            <small class="fw-bold">
                                                ${event.placesDisponibles} / ${event.capacite}
                                            </small>
                                        </div>
                                        <div class="progress" style="height: 8px;">
                                            <c:set var="fillRate" value="${((event.capacite - event.placesDisponibles) * 100) / event.capacite}"/>
                                            <div class="progress-bar ${fillRate >= 80 ? 'bg-danger' : fillRate >= 50 ? 'bg-warning' : 'bg-success'}" 
                                                 role="progressbar"
                                                 style="width: ${fillRate}%"
                                                 aria-valuenow="${fillRate}" 
                                                 aria-valuemin="0" 
                                                 aria-valuemax="100">
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Rating -->
                                    <c:if test="${not empty event.noteMoyenne and event.noteMoyenne > 0}">
                                        <div class="mb-2">
                                            <c:forEach begin="1" end="5" var="i">
                                                <i class="fas fa-star ${i <= event.noteMoyenne ? 'text-warning' : 'text-muted'}"></i>
                                            </c:forEach>
                                            <span class="text-muted small ms-1">
                                                (<fmt:formatNumber value="${event.noteMoyenne}" maxFractionDigits="1"/>)
                                            </span>
                                        </div>
                                    </c:if>
                                </div>

                                <div class="card-footer bg-white border-0">
                                    <a href="${pageContext.request.contextPath}/events/details?id=${event.id}" 
                                       class="btn btn-primary btn-sm w-100">
                                        <i class="fas fa-info-circle"></i> Voir les détails
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="text-center py-5">
                    <i class="fas fa-calendar-times fa-5x text-muted mb-3"></i>
                    <h4 class="text-muted">Aucun événement disponible pour le moment</h4>
                    <p class="text-muted">Revenez bientôt pour découvrir de nouveaux événements !</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<!-- Upcoming Events Section -->
<section class="py-5 bg-light">
    <div class="container">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="mb-0">
                <i class="fas fa-calendar-day text-primary"></i> Événements à Venir
            </h2>
            <a href="${pageContext.request.contextPath}/events/browse" 
               class="btn btn-outline-primary">
                Voir tout <i class="fas fa-arrow-right"></i>
            </a>
        </div>

        <c:choose>
            <c:when test="${not empty upcomingEvents}">
                <div class="row g-4">
                    <c:forEach items="${upcomingEvents}" var="event" begin="0" end="5">
                        <div class="col-md-6 col-lg-4">
                            <div class="card h-100 shadow-sm border-0">
                                <div class="card-body">
                                    <!-- Date Badge -->
                                    <div class="d-flex justify-content-between align-items-start mb-3">
                                        <h5 class="card-title mb-0">
                                            <c:out value="${event.titre}"/>
                                        </h5>
                                        <span class="badge bg-info ms-2">
                                            ${event.dateDebut.dayOfMonth < 10 ? '0' : ''}${event.dateDebut.dayOfMonth} ${event.dateDebut.month.toString().substring(0, 3)}
                                        </span>
                                    </div>
                                    
                                    <p class="card-text text-muted small mb-2">
                                        <i class="fas fa-clock"></i>
                                        ${event.dateDebut.hour < 10 ? '0' : ''}${event.dateDebut.hour}:${event.dateDebut.minute < 10 ? '0' : ''}${event.dateDebut.minute}
                                        - ${event.dateFin.hour < 10 ? '0' : ''}${event.dateFin.hour}:${event.dateFin.minute < 10 ? '0' : ''}${event.dateFin.minute}
                                    </p>
                                    
                                    <p class="card-text text-muted small mb-3">
                                        <i class="fas fa-map-marker-alt"></i>
                                        <c:out value="${event.lieu}"/>
                                    </p>

                                    <!-- Description Preview -->
                                    <c:if test="${not empty event.description}">
                                        <p class="card-text small">
                                            <c:choose>
                                                <c:when test="${fn:length(event.description) > 100}">
                                                    <c:out value="${fn:substring(event.description, 0, 100)}"/>...
                                                </c:when>
                                                <c:otherwise>
                                                    <c:out value="${event.description}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                    </c:if>

                                    <!-- Organizer Info -->
                                    <c:if test="${not empty event.organisateur}">
                                        <p class="card-text small text-muted mb-0">
                                            <i class="fas fa-user-tie"></i>
                                            Par <c:out value="${event.organisateur.organisation}"/>
                                        </p>
                                    </c:if>
                                </div>
                                
                                <div class="card-footer bg-white">
                                    <a href="${pageContext.request.contextPath}/events/details?id=${event.id}" 
                                       class="btn btn-outline-primary btn-sm w-100">
                                        <i class="fas fa-arrow-right"></i> En savoir plus
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="text-center py-5">
                    <i class="fas fa-calendar-plus fa-5x text-muted mb-3"></i>
                    <h4 class="text-muted">Aucun événement à venir</h4>
                    <p class="text-muted">Consultez notre catalogue complet d'événements</p>
                    <a href="${pageContext.request.contextPath}/events/browse" 
                       class="btn btn-primary">
                        <i class="fas fa-search"></i> Parcourir les événements
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<!-- Call to Action Section -->
<c:if test="${empty sessionScope.currentUser}">
    <section class="py-5 bg-primary text-white">
        <div class="container text-center">
            <h2 class="display-5 fw-bold mb-3">Prêt à découvrir de nouveaux événements ?</h2>
            <p class="lead mb-4">
                Rejoignez notre communauté et ne manquez plus aucun événement !
            </p>
            <div class="d-flex gap-3 justify-content-center">
                <a href="${pageContext.request.contextPath}/register" 
                   class="btn btn-light btn-lg">
                    <i class="fas fa-user-plus"></i> Créer un compte
                </a>
                <a href="${pageContext.request.contextPath}/login" 
                   class="btn btn-outline-light btn-lg">
                    <i class="fas fa-sign-in-alt"></i> Se connecter
                </a>
            </div>
        </div>
    </section>
</c:if>

<!-- Features Section -->
<section class="py-5">
    <div class="container">
        <div class="text-center mb-5">
            <h2 class="display-6 fw-bold">Pourquoi choisir EventManagement ?</h2>
            <p class="lead text-muted">Une plateforme complète pour tous vos événements</p>
        </div>
        
        <div class="row g-4">
            <div class="col-md-4">
                <div class="text-center p-4">
                    <div class="bg-primary bg-opacity-10 rounded-circle d-inline-flex p-4 mb-3">
                        <i class="fas fa-search fa-3x text-primary"></i>
                    </div>
                    <h4>Découvrez</h4>
                    <p class="text-muted">
                        Parcourez une large sélection d'événements adaptés à vos centres d'intérêt
                    </p>
                </div>
            </div>
            
            <div class="col-md-4">
                <div class="text-center p-4">
                    <div class="bg-success bg-opacity-10 rounded-circle d-inline-flex p-4 mb-3">
                        <i class="fas fa-ticket-alt fa-3x text-success"></i>
                    </div>
                    <h4>Inscrivez-vous</h4>
                    <p class="text-muted">
                        Réservez votre place en quelques clics et recevez votre confirmation instantanément
                    </p>
                </div>
            </div>
            
            <div class="col-md-4">
                <div class="text-center p-4">
                    <div class="bg-info bg-opacity-10 rounded-circle d-inline-flex p-4 mb-3">
                        <i class="fas fa-users fa-3x text-info"></i>
                    </div>
                    <h4>Participez</h4>
                    <p class="text-muted">
                        Rencontrez de nouvelles personnes et créez des souvenirs inoubliables
                    </p>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Organizer CTA -->
<section class="py-5 bg-light">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-lg-8">
                <h3 class="fw-bold mb-2">
                    <i class="fas fa-bullhorn text-primary"></i>
                    Vous organisez des événements ?
                </h3>
                <p class="text-muted mb-lg-0">
                    Créez un compte organisateur et commencez à gérer vos événements dès aujourd'hui.
                    Profitez d'outils puissants et d'une interface intuitive.
                </p>
            </div>
            <div class="col-lg-4 text-lg-end">
                <a href="${pageContext.request.contextPath}/register" 
                   class="btn btn-primary btn-lg">
                    <i class="fas fa-users-cog"></i> Devenir organisateur
                </a>
            </div>
        </div>
    </div>
</section>

<style>
    .hover-shadow {
        transition: all 0.3s ease;
    }
    
    .hover-shadow:hover {
        transform: translateY(-5px);
        box-shadow: 0 1rem 3rem rgba(0,0,0,.175) !important;
    }
    
    .card-img-top {
        transition: transform 0.3s ease;
    }
    
    .card:hover .card-img-top {
        transform: scale(1.05);
    }
</style>

<jsp:include page="layout/footer.jsp"/>
