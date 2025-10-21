<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="${event.titre}" scope="request"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/messages.jsp"/>

<!-- Event Header with Image -->
<section class="position-relative">
    <c:choose>
        <c:when test="${not empty event.imageUrl}">
            <div class="position-relative" style="height: 400px; overflow: hidden;">
                <img src="${event.imageUrl}" 
                     class="w-100 h-100" 
                     alt="<c:out value='${event.titre}'/>"
                     style="object-fit: cover; filter: brightness(0.7);">
                <div class="position-absolute top-0 start-0 w-100 h-100 d-flex align-items-center" 
                     style="background: linear-gradient(to bottom, rgba(0,0,0,0.3), rgba(0,0,0,0.7));">
                    <div class="container text-white">
                        <h1 class="display-4 fw-bold mb-3"><c:out value="${event.titre}"/></h1>
                        <p class="lead">
                            <i class="fas fa-map-marker-alt"></i> <c:out value="${event.lieu}"/>
                        </p>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="bg-primary text-white py-5">
                <div class="container">
                    <h1 class="display-4 fw-bold mb-3"><c:out value="${event.titre}"/></h1>
                    <p class="lead">
                        <i class="fas fa-map-marker-alt"></i> <c:out value="${event.lieu}"/>
                    </p>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<!-- Event Details Section -->
<section class="py-5">
    <div class="container">
        <div class="row g-4">
            <!-- Main Content -->
            <div class="col-lg-8">
                <!-- Event Info Card -->
                <div class="card shadow-sm mb-4">
                    <div class="card-body">
                        <h3 class="card-title mb-4">
                            <i class="fas fa-info-circle text-primary"></i> Détails de l'événement
                        </h3>

                        <!-- Description -->
                        <div class="mb-4">
                            <h5 class="fw-bold mb-3">Description</h5>
                            <p class="text-muted" style="white-space: pre-line;"><c:out value="${event.description}"/></p>
                        </div>

                        <!-- Categories -->
                        <c:if test="${not empty event.categories}">
                            <div class="mb-4">
                                <h5 class="fw-bold mb-3">Catégories</h5>
                                <c:forEach items="${event.categories}" var="categorie">
                                    <span class="badge bg-secondary me-2 mb-2 p-2">
                                        <c:if test="${not empty categorie.icone}">
                                            <i class="fas fa-${categorie.icone}"></i>
                                        </c:if>
                                        <c:out value="${categorie.nom}"/>
                                    </span>
                                </c:forEach>
                            </div>
                        </c:if>

                        <!-- Organizer Info -->
                        <c:if test="${not empty event.organisateur}">
                            <div class="mb-4">
                                <h5 class="fw-bold mb-3">Organisé par</h5>
                                <div class="d-flex align-items-center">
                                    <c:choose>
                                        <c:when test="${not empty event.organisateur.photoProfil}">
                                            <img src="${event.organisateur.photoProfil}" 
                                                 alt="<c:out value='${event.organisateur.organisation}'/>"
                                                 class="rounded-circle me-3"
                                                 style="width: 60px; height: 60px; object-fit: cover;">
                                        </c:when>
                                        <c:otherwise>
                                            <div class="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center me-3"
                                                 style="width: 60px; height: 60px;">
                                                <i class="fas fa-building fa-2x"></i>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    <div>
                                        <h6 class="mb-0"><c:out value="${event.organisateur.organisation}"/></h6>
                                        <small class="text-muted">
                                            <c:if test="${not empty event.organisateur.siteWeb}">
                                                <a href="${event.organisateur.siteWeb}" target="_blank" class="text-decoration-none">
                                                    <i class="fas fa-globe"></i> Site web
                                                </a>
                                            </c:if>
                                        </small>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <!-- Location -->
                        <c:if test="${not empty event.adresseComplete}">
                            <div class="mb-4">
                                <h5 class="fw-bold mb-3">Adresse</h5>
                                <p class="text-muted">
                                    <i class="fas fa-map-marker-alt text-danger"></i>
                                    <c:out value="${event.adresseComplete}"/>
                                </p>
                            </div>
                        </c:if>

                        <!-- Rating -->
                        <c:if test="${not empty averageRating and averageRating > 0}">
                            <div class="mb-4">
                                <h5 class="fw-bold mb-3">Note moyenne</h5>
                                <div class="d-flex align-items-center">
                                    <c:forEach begin="1" end="5" var="i">
                                        <i class="fas fa-star ${i <= averageRating ? 'text-warning' : 'text-muted'} fa-2x me-1"></i>
                                    </c:forEach>
                                    <span class="ms-3 fs-4">
                                        <fmt:formatNumber value="${averageRating}" maxFractionDigits="1"/> / 5
                                    </span>
                                    <span class="text-muted ms-2">(${fn:length(evaluations)} avis)</span>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>

                <!-- Comments Section -->
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h3 class="card-title mb-4">
                            <i class="fas fa-comments text-primary"></i> 
                            Commentaires (${fn:length(comments)})
                        </h3>

                        <c:choose>
                            <c:when test="${not empty comments}">
                                <c:forEach items="${comments}" var="comment">
                                    <div class="border-bottom pb-3 mb-3">
                                        <div class="d-flex align-items-start">
                                            <div class="flex-grow-1">
                                                <h6 class="mb-1">
                                                    <c:out value="${comment.participant.nom}"/>
                                                    <small class="text-muted">
                                                        - ${comment.dateCreation.dayOfMonth}/${comment.dateCreation.monthValue}/${comment.dateCreation.year}
                                                    </small>
                                                </h6>
                                                <p class="text-muted mb-0"><c:out value="${comment.contenu}"/></p>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted text-center py-4">
                                    <i class="fas fa-comment-slash fa-3x mb-3 d-block"></i>
                                    Aucun commentaire pour le moment
                                </p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- Sidebar -->
            <div class="col-lg-4">
                <!-- Event Info Card -->
                <div class="card shadow-sm mb-4 sticky-top" style="top: 20px;">
                    <div class="card-body">
                        <!-- Date & Time -->
                        <div class="mb-4">
                            <h6 class="text-muted mb-2">
                                <i class="fas fa-calendar text-primary"></i> Date et heure
                            </h6>
                            <p class="mb-1 fw-bold">
                                ${event.dateDebut.dayOfMonth < 10 ? '0' : ''}${event.dateDebut.dayOfMonth}/${event.dateDebut.monthValue < 10 ? '0' : ''}${event.dateDebut.monthValue}/${event.dateDebut.year}
                            </p>
                            <p class="text-muted mb-0">
                                ${event.dateDebut.hour < 10 ? '0' : ''}${event.dateDebut.hour}:${event.dateDebut.minute < 10 ? '0' : ''}${event.dateDebut.minute}
                                - 
                                ${event.dateFin.hour < 10 ? '0' : ''}${event.dateFin.hour}:${event.dateFin.minute < 10 ? '0' : ''}${event.dateFin.minute}
                            </p>
                        </div>

                        <!-- Price -->
                        <div class="mb-4">
                            <h6 class="text-muted mb-2">
                                <i class="fas fa-ticket-alt text-primary"></i> Tarif
                            </h6>
                            <c:choose>
                                <c:when test="${event.gratuit}">
                                    <span class="badge bg-success fs-6 p-2">
                                        <i class="fas fa-check-circle"></i> Gratuit
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <p class="mb-0 fw-bold fs-4">
                                        <fmt:formatNumber value="${event.prix}" minFractionDigits="2" maxFractionDigits="2"/> DH
                                    </p>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Capacity -->
                        <div class="mb-4">
                            <h6 class="text-muted mb-2">
                                <i class="fas fa-users text-primary"></i> Places disponibles
                            </h6>
                            <div class="d-flex justify-content-between mb-2">
                                <span>${availableSeats} / ${event.capacite}</span>
                                <c:set var="fillRate" value="${((event.capacite - availableSeats) * 100) / event.capacite}"/>
                                <span class="fw-bold">
                                    <fmt:formatNumber value="${fillRate}" maxFractionDigits="0"/>%
                                </span>
                            </div>
                            <div class="progress" style="height: 12px;">
                                <div class="progress-bar ${fillRate >= 80 ? 'bg-danger' : fillRate >= 50 ? 'bg-warning' : 'bg-success'}" 
                                     role="progressbar"
                                     style="width: ${fillRate}%">
                                </div>
                            </div>
                            <c:if test="${isFull}">
                                <p class="text-danger mt-2 mb-0">
                                    <i class="fas fa-exclamation-triangle"></i> Complet
                                </p>
                            </c:if>
                        </div>

                        <!-- Registration Button -->
                        <div class="d-grid gap-2">
                            <c:choose>
                                <c:when test="${empty sessionScope.currentUser}">
                                    <!-- Not logged in -->
                                    <a href="${pageContext.request.contextPath}/login?redirect=${pageContext.request.requestURI}%3Fid%3D${event.id}" 
                                       class="btn btn-primary btn-lg">
                                        <i class="fas fa-sign-in-alt"></i> Se connecter pour s'inscrire
                                    </a>
                                </c:when>
                                <c:when test="${alreadyRegistered}">
                                    <!-- Already registered -->
                                    <c:choose>
                                        <c:when test="${inscription.statut == 'EN_ATTENTE'}">
                                            <button class="btn btn-warning btn-lg" disabled>
                                                <i class="fas fa-clock"></i> Inscription en attente
                                            </button>
                                            <form action="${pageContext.request.contextPath}/events/cancel-registration" 
                                                  method="post"
                                                  onsubmit="return confirm('Êtes-vous sûr de vouloir annuler votre inscription ?');">
                                                <input type="hidden" name="inscriptionId" value="${inscription.id}">
                                                <button type="submit" class="btn btn-outline-danger btn-sm w-100">
                                                    <i class="fas fa-times"></i> Annuler l'inscription
                                                </button>
                                            </form>
                                        </c:when>
                                        <c:when test="${inscription.statut == 'ACCEPTEE'}">
                                            <button class="btn btn-success btn-lg" disabled>
                                                <i class="fas fa-check-circle"></i> Inscription confirmée
                                            </button>
                                        </c:when>
                                        <c:when test="${inscription.statut == 'REFUSEE'}">
                                            <button class="btn btn-danger btn-lg" disabled>
                                                <i class="fas fa-times-circle"></i> Inscription refusée
                                            </button>
                                        </c:when>
                                        <c:when test="${inscription.statut == 'ANNULEE'}">
                                            <button class="btn btn-secondary btn-lg" disabled>
                                                <i class="fas fa-ban"></i> Inscription annulée
                                            </button>
                                        </c:when>
                                    </c:choose>
                                </c:when>
                                <c:when test="${canRegister and not isFull}">
                                    <!-- Can register -->
                                    <form action="${pageContext.request.contextPath}/events/register" method="post">
                                        <input type="hidden" name="eventId" value="${event.id}">
                                        <button type="submit" class="btn btn-primary btn-lg w-100">
                                            <i class="fas fa-ticket-alt"></i> S'inscrire à l'événement
                                        </button>
                                    </form>
                                </c:when>
                                <c:when test="${isFull}">
                                    <button class="btn btn-danger btn-lg" disabled>
                                        <i class="fas fa-exclamation-triangle"></i> Événement complet
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn btn-secondary btn-lg" disabled>
                                        <i class="fas fa-ban"></i> Inscription non disponible
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Stats -->
                        <div class="mt-4 pt-3 border-top">
                            <div class="row text-center g-3">
                                <div class="col-6">
                                    <div class="p-2">
                                        <i class="fas fa-eye fa-2x text-muted mb-2 d-block"></i>
                                        <h5 class="mb-0">${event.nombreVues}</h5>
                                        <small class="text-muted">Vues</small>
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="p-2">
                                        <i class="fas fa-user-check fa-2x text-muted mb-2 d-block"></i>
                                        <h5 class="mb-0">${acceptedInscriptions}</h5>
                                        <small class="text-muted">Participants</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Back Button -->
                <a href="${pageContext.request.contextPath}/events/browse" class="btn btn-outline-secondary w-100">
                    <i class="fas fa-arrow-left"></i> Retour à la liste
                </a>
            </div>
        </div>
    </div>
</section>

<jsp:include page="../layout/footer.jsp"/>
