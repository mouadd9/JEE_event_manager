<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="pageTitle" value="Tableau de Bord Organisateur" />
<jsp:include page="../layout/header.jsp" />

<div class="container-fluid py-4">
    <div class="row">
        <!-- Sidebar -->
        <div class="col-md-3 col-lg-2">
            <div class="card border-0 shadow-sm">
                <div class="card-body">
                    <div class="text-center mb-4">
                        <i class="fas fa-user-circle fa-4x text-primary"></i>
                        <h5 class="mt-3 mb-1">${sessionScope.user.nom} ${sessionScope.user.prenom}</h5>
                        <c:if test="${not empty organisateur && not empty organisateur.organisation}">
                            <p class="text-muted small mb-0">
                                <i class="fas fa-building me-1"></i>${organisateur.organisation}
                            </p>
                        </c:if>
                    </div>
                    
                    <div class="list-group list-group-flush">
                        <a href="${pageContext.request.contextPath}/dashboard/organizer" 
                           class="list-group-item list-group-item-action active">
                            <i class="fas fa-home me-2"></i>Tableau de bord
                        </a>
                        <a href="${pageContext.request.contextPath}/organizer/events" 
                           class="list-group-item list-group-item-action">
                            <i class="fas fa-calendar-alt me-2"></i>Mes événements
                        </a>
                        <a href="${pageContext.request.contextPath}/events/create" 
                           class="list-group-item list-group-item-action">
                            <i class="fas fa-plus-circle me-2"></i>Créer événement
                        </a>
                        <a href="${pageContext.request.contextPath}/profile" 
                           class="list-group-item list-group-item-action">
                            <i class="fas fa-user-cog me-2"></i>Mon profil
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Main Content -->
        <div class="col-md-9 col-lg-10">
            <!-- Welcome Section -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="mb-1">
                        <i class="fas fa-chart-line text-primary me-2"></i>Tableau de Bord
                    </h2>
                    <p class="text-muted mb-0">
                        Bienvenue ${sessionScope.user.prenom}, voici un aperçu de vos événements
                    </p>
                </div>
                <a href="${pageContext.request.contextPath}/events/create" 
                   class="btn btn-primary">
                    <i class="fas fa-plus-circle me-2"></i>Nouvel événement
                </a>
            </div>

            <!-- Messages -->
            <jsp:include page="../layout/messages.jsp" />

            <!-- Statistics Cards -->
            <div class="row g-3 mb-4">
                <!-- Total Events -->
                <div class="col-md-6 col-xl-3">
                    <div class="card border-0 shadow-sm h-100 bg-primary bg-gradient text-white">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-center">
                                <div>
                                    <p class="mb-2 text-white-50">Total Événements</p>
                                    <h2 class="mb-0 fw-bold">${stats.totalEvents != null ? stats.totalEvents : 0}</h2>
                                </div>
                                <div class="fs-1 opacity-50">
                                    <i class="fas fa-calendar-check"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Active Events -->
                <div class="col-md-6 col-xl-3">
                    <div class="card border-0 shadow-sm h-100 bg-success bg-gradient text-white">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-center">
                                <div>
                                    <p class="mb-2 text-white-50">Événements Actifs</p>
                                    <h2 class="mb-0 fw-bold">${stats.activeEvents != null ? stats.activeEvents : 0}</h2>
                                </div>
                                <div class="fs-1 opacity-50">
                                    <i class="fas fa-check-circle"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Total Participants -->
                <div class="col-md-6 col-xl-3">
                    <div class="card border-0 shadow-sm h-100 bg-info bg-gradient text-white">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-center">
                                <div>
                                    <p class="mb-2 text-white-50">Total Participants</p>
                                    <h2 class="mb-0 fw-bold">${stats.totalParticipants != null ? stats.totalParticipants : 0}</h2>
                                </div>
                                <div class="fs-1 opacity-50">
                                    <i class="fas fa-users"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Pending Inscriptions -->
                <div class="col-md-6 col-xl-3">
                    <div class="card border-0 shadow-sm h-100 bg-warning bg-gradient text-white">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-center">
                                <div>
                                    <p class="mb-2 text-white-50">En Attente</p>
                                    <h2 class="mb-0 fw-bold">${stats.pendingInscriptions != null ? stats.pendingInscriptions : 0}</h2>
                                </div>
                                <div class="fs-1 opacity-50">
                                    <i class="fas fa-hourglass-half"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Recent Events -->
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-header bg-white py-3">
                    <div class="d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">
                            <i class="fas fa-calendar-alt text-primary me-2"></i>Mes Événements Récents
                        </h5>
                        <a href="${pageContext.request.contextPath}/organizer/events" 
                           class="btn btn-sm btn-outline-primary">
                            Voir tout
                        </a>
                    </div>
                </div>
                <div class="card-body p-0">
                    <c:choose>
                        <c:when test="${not empty recentEvents}">
                            <div class="table-responsive">
                                <table class="table table-hover mb-0">
                                    <thead class="table-light">
                                        <tr>
                                            <th>Événement</th>
                                            <th>Date</th>
                                            <th>Statut</th>
                                            <th>Participants</th>
                                            <th>Vues</th>
                                            <th class="text-center">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${recentEvents}" var="event">
                                            <tr>
                                                <td>
                                                    <div class="d-flex align-items-center">
                                                        <c:choose>
                                                            <c:when test="${not empty event.imageUrl}">
                                                                <img src="${event.imageUrl}" 
                                                                     alt="${event.titre}"
                                                                     class="rounded me-2"
                                                                     style="width: 50px; height: 50px; object-fit: cover;">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <div class="bg-light rounded me-2 d-flex align-items-center justify-content-center"
                                                                     style="width: 50px; height: 50px;">
                                                                    <i class="fas fa-image text-muted"></i>
                                                                </div>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <div>
                                                            <strong>${event.titre}</strong>
                                                            <br>
                                                            <small class="text-muted">
                                                                <i class="fas fa-map-marker-alt me-1"></i>${event.lieu}
                                                            </small>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>
                                                    ${event.dateDebut.dayOfMonth} 
                                                    <c:choose>
                                                        <c:when test="${event.dateDebut.monthValue == 1}">Jan</c:when>
                                                        <c:when test="${event.dateDebut.monthValue == 2}">Fév</c:when>
                                                        <c:when test="${event.dateDebut.monthValue == 3}">Mar</c:when>
                                                        <c:when test="${event.dateDebut.monthValue == 4}">Avr</c:when>
                                                        <c:when test="${event.dateDebut.monthValue == 5}">Mai</c:when>
                                                        <c:when test="${event.dateDebut.monthValue == 6}">Juin</c:when>
                                                        <c:when test="${event.dateDebut.monthValue == 7}">Juil</c:when>
                                                        <c:when test="${event.dateDebut.monthValue == 8}">Aoû</c:when>
                                                        <c:when test="${event.dateDebut.monthValue == 9}">Sep</c:when>
                                                        <c:when test="${event.dateDebut.monthValue == 10}">Oct</c:when>
                                                        <c:when test="${event.dateDebut.monthValue == 11}">Nov</c:when>
                                                        <c:when test="${event.dateDebut.monthValue == 12}">Déc</c:when>
                                                    </c:choose>
                                                    ${event.dateDebut.year}
                                                    <br>
                                                    <small class="text-muted">
                                                        ${String.format("%02d:%02d", event.dateDebut.hour, event.dateDebut.minute)}
                                                    </small>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${event.statut eq 'PUBLIE'}">
                                                            <span class="badge bg-success">
                                                                <i class="fas fa-check-circle me-1"></i>Publié
                                                            </span>
                                                        </c:when>
                                                        <c:when test="${event.statut eq 'BROUILLON'}">
                                                            <span class="badge bg-secondary">
                                                                <i class="fas fa-file me-1"></i>Brouillon
                                                            </span>
                                                        </c:when>
                                                        <c:when test="${event.statut eq 'ANNULE'}">
                                                            <span class="badge bg-danger">
                                                                <i class="fas fa-times-circle me-1"></i>Annulé
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-warning">
                                                                ${event.statut}
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <span class="badge bg-info">
                                                        ${event.nombreInscriptions} / ${event.capacite}
                                                    </span>
                                                    <c:if test="${event.nombreInscriptions >= event.capacite}">
                                                        <br>
                                                        <small class="text-danger">Complet</small>
                                                    </c:if>
                                                </td>
                                                <td>
                                                    <i class="fas fa-eye text-muted me-1"></i>
                                                    ${event.nombreVues}
                                                </td>
                                                <td class="text-center">
                                                    <div class="btn-group btn-group-sm" role="group">
                                                        <a href="${pageContext.request.contextPath}/organizer/events/view?id=${event.id}"
                                                           class="btn btn-outline-primary"
                                                           title="Voir">
                                                            <i class="fas fa-eye"></i>
                                                        </a>
                                                        <a href="${pageContext.request.contextPath}/events/edit?id=${event.id}"
                                                           class="btn btn-outline-warning"
                                                           title="Modifier">
                                                            <i class="fas fa-edit"></i>
                                                        </a>
                                                        <a href="${pageContext.request.contextPath}/events/${event.id}/inscriptions"
                                                           class="btn btn-outline-info"
                                                           title="Participants">
                                                            <i class="fas fa-users"></i>
                                                        </a>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-5">
                                <i class="fas fa-calendar-times fa-3x text-muted mb-3"></i>
                                <h5 class="text-muted">Aucun événement</h5>
                                <p class="text-muted mb-3">Vous n'avez pas encore créé d'événements</p>
                                <a href="${pageContext.request.contextPath}/events/create" 
                                   class="btn btn-primary">
                                    <i class="fas fa-plus-circle me-2"></i>Créer mon premier événement
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Quick Actions -->
            <div class="row g-3">
                <div class="col-md-6">
                    <div class="card border-0 shadow-sm h-100">
                        <div class="card-body text-center py-5">
                            <i class="fas fa-calendar-plus fa-3x text-primary mb-3"></i>
                            <h5>Créer un Événement</h5>
                            <p class="text-muted mb-3">Organisez un nouvel événement et partagez-le avec la communauté</p>
                            <a href="${pageContext.request.contextPath}/events/create" 
                               class="btn btn-primary">
                                Commencer
                            </a>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="card border-0 shadow-sm h-100">
                        <div class="card-body text-center py-5">
                            <i class="fas fa-chart-bar fa-3x text-success mb-3"></i>
                            <h5>Voir les Statistiques</h5>
                            <p class="text-muted mb-3">Analysez les performances de vos événements en détail</p>
                            <a href="${pageContext.request.contextPath}/organizer/statistics" 
                               class="btn btn-success">
                                Voir les stats
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
