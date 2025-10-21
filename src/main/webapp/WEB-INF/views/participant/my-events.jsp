<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Mes événements" scope="request"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/messages.jsp"/>

<!-- Page Header -->
<section class="bg-primary text-white py-4">
    <div class="container">
        <h1 class="mb-0">
            <i class="fas fa-calendar-check"></i> Mes événements
        </h1>
        <p class="mb-0 mt-2">Gérez vos inscriptions et participations</p>
    </div>
</section>

<!-- Stats Section -->
<section class="py-4 bg-light border-bottom">
    <div class="container">
        <div class="row text-center g-3">
            <div class="col-md-4">
                <div class="bg-white p-3 rounded shadow-sm">
                    <i class="fas fa-ticket-alt fa-2x text-primary mb-2"></i>
                    <h4 class="mb-1">${totalInscriptions}</h4>
                    <p class="text-muted mb-0">Total inscriptions</p>
                </div>
            </div>
            <div class="col-md-4">
                <div class="bg-white p-3 rounded shadow-sm">
                    <i class="fas fa-check-circle fa-2x text-success mb-2"></i>
                    <h4 class="mb-1">${acceptedCount}</h4>
                    <p class="text-muted mb-0">Confirmées</p>
                </div>
            </div>
            <div class="col-md-4">
                <div class="bg-white p-3 rounded shadow-sm">
                    <i class="fas fa-clock fa-2x text-warning mb-2"></i>
                    <h4 class="mb-1">${waitingCount}</h4>
                    <p class="text-muted mb-0">En attente</p>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Upcoming Events Section -->
<section class="py-5">
    <div class="container">
        <h2 class="mb-4">
            <i class="fas fa-calendar-alt text-primary"></i> Événements à venir
        </h2>

        <c:choose>
            <c:when test="${not empty upcomingEvents}">
                <div class="row g-4">
                    <c:forEach items="${upcomingEvents}" var="event">
                        <c:set var="inscription" value="${inscriptionMap[event.id]}"/>
                        <div class="col-md-6 col-lg-4">
                            <div class="card h-100 shadow-sm hover-shadow">
                                <!-- Event Image -->
                                <c:choose>
                                    <c:when test="${not empty event.imageUrl}">
                                        <img src="${event.imageUrl}" 
                                             class="card-img-top" 
                                             alt="<c:out value='${event.titre}'/>"
                                             style="height: 180px; object-fit: cover;">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="card-img-top bg-gradient d-flex align-items-center justify-content-center" 
                                             style="height: 180px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                                            <i class="fas fa-calendar-alt fa-3x text-white opacity-75"></i>
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                                <!-- Status Badge -->
                                <c:if test="${not empty inscription}">
                                    <div class="position-absolute top-0 end-0 m-2">
                                        <c:choose>
                                            <c:when test="${inscription.statut == 'EN_ATTENTE'}">
                                                <span class="badge bg-warning">
                                                    <i class="fas fa-clock"></i> En attente
                                                </span>
                                            </c:when>
                                            <c:when test="${inscription.statut == 'ACCEPTEE'}">
                                                <span class="badge bg-success">
                                                    <i class="fas fa-check-circle"></i> Confirmée
                                                </span>
                                            </c:when>
                                            <c:when test="${inscription.statut == 'REFUSEE'}">
                                                <span class="badge bg-danger">
                                                    <i class="fas fa-times-circle"></i> Refusée
                                                </span>
                                            </c:when>
                                        </c:choose>
                                    </div>
                                </c:if>

                                <div class="card-body">
                                    <h5 class="card-title mb-3">
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

                                    <p class="card-text small mb-3">
                                        <c:choose>
                                            <c:when test="${event.gratuit}">
                                                <span class="badge bg-success">
                                                    <i class="fas fa-check-circle"></i> Gratuit
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-info">
                                                    <i class="fas fa-ticket-alt"></i>
                                                    <fmt:formatNumber value="${event.prix}" minFractionDigits="2" maxFractionDigits="2"/> DH
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                </div>

                                <div class="card-footer bg-white border-0">
                                    <div class="d-grid gap-2">
                                        <a href="${pageContext.request.contextPath}/events/details?id=${event.id}" 
                                           class="btn btn-outline-primary btn-sm">
                                            <i class="fas fa-info-circle"></i> Voir les détails
                                        </a>
                                        <c:if test="${inscription.statut == 'EN_ATTENTE'}">
                                            <form action="${pageContext.request.contextPath}/events/cancel-registration" 
                                                  method="post"
                                                  onsubmit="return confirm('Êtes-vous sûr de vouloir annuler cette inscription ?');">
                                                <input type="hidden" name="inscriptionId" value="${inscription.id}">
                                                <button type="submit" class="btn btn-outline-danger btn-sm w-100">
                                                    <i class="fas fa-times"></i> Annuler l'inscription
                                                </button>
                                            </form>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="text-center py-5 bg-light rounded">
                    <i class="fas fa-calendar-plus fa-5x text-muted mb-3"></i>
                    <h4 class="text-muted mb-3">Aucun événement à venir</h4>
                    <p class="text-muted mb-4">Vous n'êtes inscrit à aucun événement futur pour le moment.</p>
                    <a href="${pageContext.request.contextPath}/events/browse" 
                       class="btn btn-primary">
                        <i class="fas fa-search"></i> Parcourir les événements
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<!-- Past Events Section -->
<section class="py-5 bg-light">
    <div class="container">
        <h2 class="mb-4">
            <i class="fas fa-history text-secondary"></i> Événements passés
        </h2>

        <c:choose>
            <c:when test="${not empty pastEvents}">
                <div class="row g-4">
                    <c:forEach items="${pastEvents}" var="event">
                        <c:set var="inscription" value="${inscriptionMap[event.id]}"/>
                        <div class="col-md-6 col-lg-4">
                            <div class="card h-100 shadow-sm">
                                <!-- Event Image -->
                                <c:choose>
                                    <c:when test="${not empty event.imageUrl}">
                                        <img src="${event.imageUrl}" 
                                             class="card-img-top" 
                                             alt="<c:out value='${event.titre}'/>"
                                             style="height: 180px; object-fit: cover; filter: grayscale(50%);">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="card-img-top bg-secondary d-flex align-items-center justify-content-center" 
                                             style="height: 180px;">
                                            <i class="fas fa-calendar-alt fa-3x text-white opacity-50"></i>
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                                <div class="card-body">
                                    <h5 class="card-title mb-3 text-muted">
                                        <c:out value="${event.titre}"/>
                                    </h5>

                                    <p class="card-text text-muted small mb-2">
                                        <i class="fas fa-map-marker-alt"></i>
                                        <c:out value="${event.lieu}"/>
                                    </p>

                                    <p class="card-text small mb-2">
                                        <i class="fas fa-calendar"></i>
                                        ${event.dateDebut.dayOfMonth < 10 ? '0' : ''}${event.dateDebut.dayOfMonth}/${event.dateDebut.monthValue < 10 ? '0' : ''}${event.dateDebut.monthValue}/${event.dateDebut.year}
                                    </p>
                                </div>

                                <div class="card-footer bg-white border-0">
                                    <a href="${pageContext.request.contextPath}/events/details?id=${event.id}" 
                                       class="btn btn-outline-secondary btn-sm w-100">
                                        <i class="fas fa-eye"></i> Voir l'événement
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="text-center py-5 bg-white rounded">
                    <i class="fas fa-calendar-times fa-5x text-muted mb-3"></i>
                    <h4 class="text-muted">Aucun événement passé</h4>
                    <p class="text-muted mb-0">Vous n'avez participé à aucun événement pour le moment.</p>
                </div>
            </c:otherwise>
        </c:choose>
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
</style>

<jsp:include page="../layout/footer.jsp"/>
