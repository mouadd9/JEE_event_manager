<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="pageTitle" value="${evenement.titre}" />
<jsp:include page="../layout/header.jsp" />
<jsp:include page="../layout/messages.jsp" />

<div class="container py-4">
    <!-- Back Button -->
    <div class="mb-3">
        <a href="${pageContext.request.contextPath}/events/manage" class="btn btn-outline-secondary">
            <i class="fas fa-arrow-left"></i> Retour à la liste
        </a>
    </div>

    <div class="row">
        <!-- Main Content -->
        <div class="col-lg-8">
            <!-- Event Header -->
            <div class="card mb-4">
                <c:if test="${not empty evenement.imageUrl}">
                    <img src="${evenement.imageUrl}" class="card-img-top" alt="${evenement.titre}" 
                         style="max-height: 400px; object-fit: cover;">
                </c:if>
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start mb-3">
                        <h1 class="card-title mb-0">
                            <c:out value="${evenement.titre}"/>
                        </h1>
                        <c:choose>
                            <c:when test="${evenement.statut == 'BROUILLON'}">
                                <span class="badge bg-secondary fs-6">Brouillon</span>
                            </c:when>
                            <c:when test="${evenement.statut == 'PUBLIE'}">
                                <span class="badge bg-success fs-6">Publié</span>
                            </c:when>
                            <c:when test="${evenement.statut == 'ANNULE'}">
                                <span class="badge bg-danger fs-6">Annulé</span>
                            </c:when>
                            <c:when test="${evenement.statut == 'TERMINE'}">
                                <span class="badge bg-dark fs-6">Terminé</span>
                            </c:when>
                        </c:choose>
                    </div>

                    <!-- Categories -->
                    <div class="mb-3">
                        <c:forEach var="categorie" items="${evenement.categories}">
                            <span class="badge me-1" style="background-color: ${categorie.couleur}">
                                <i class="fas ${categorie.icone}"></i>
                                <c:out value="${categorie.nom}"/>
                            </span>
                        </c:forEach>
                    </div>

                    <!-- Date & Time -->
                    <div class="mb-3">
                        <h5><i class="fas fa-calendar-alt text-primary"></i> Date et heure</h5>
                        <p class="mb-1">
                            <strong>Début:</strong>
                            ${evenement.dateDebut.dayOfMonth} ${evenement.dateDebut.month} ${evenement.dateDebut.year} à ${evenement.dateDebut.hour < 10 ? '0' : ''}${evenement.dateDebut.hour}:${evenement.dateDebut.minute < 10 ? '0' : ''}${evenement.dateDebut.minute}
                        </p>
                        <p class="mb-0">
                            <strong>Fin:</strong>
                            ${evenement.dateFin.dayOfMonth} ${evenement.dateFin.month} ${evenement.dateFin.year} à ${evenement.dateFin.hour < 10 ? '0' : ''}${evenement.dateFin.hour}:${evenement.dateFin.minute < 10 ? '0' : ''}${evenement.dateFin.minute}
                        </p>
                    </div>

                    <!-- Location -->
                    <div class="mb-3">
                        <h5><i class="fas fa-map-marker-alt text-danger"></i> Lieu</h5>
                        <p class="mb-1">
                            <strong><c:out value="${evenement.lieu}"/></strong>
                        </p>
                        <p class="text-muted mb-0">
                            <c:out value="${evenement.adresseComplete}"/>
                        </p>
                    </div>

                    <!-- Description -->
                    <div class="mb-3">
                        <h5><i class="fas fa-align-left text-info"></i> Description</h5>
                        <p style="white-space: pre-line;">
                            <c:out value="${evenement.description}"/>
                        </p>
                    </div>

                    <!-- Statistics -->
                    <div class="row text-center mb-3">
                        <div class="col-md-3">
                            <div class="border rounded p-3">
                                <i class="fas fa-users fa-2x text-primary mb-2"></i>
                                <h4 class="mb-0">${evenement.nombreInscriptions}</h4>
                                <small class="text-muted">Inscrits</small>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="border rounded p-3">
                                <i class="fas fa-chair fa-2x text-success mb-2"></i>
                                <h4 class="mb-0">${evenement.placesDisponibles}</h4>
                                <small class="text-muted">Places restantes</small>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="border rounded p-3">
                                <i class="fas fa-eye fa-2x text-info mb-2"></i>
                                <h4 class="mb-0">${evenement.nombreVues}</h4>
                                <small class="text-muted">Vues</small>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="border rounded p-3">
                                <i class="fas fa-star fa-2x text-warning mb-2"></i>
                                <h4 class="mb-0">
                                    <c:choose>
                                        <c:when test="${not empty evenement.noteMoyenne}">
                                            ${evenement.noteMoyenne}
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </h4>
                                <small class="text-muted">Note moyenne</small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Participants List -->
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0">
                        <i class="fas fa-users"></i> Liste des participants (${fn:length(inscriptions)})
                    </h5>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${empty inscriptions}">
                            <div class="text-center py-4">
                                <i class="fas fa-user-slash fa-3x text-muted mb-3"></i>
                                <p class="text-muted">Aucun participant inscrit pour le moment</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <!-- Export Button -->
                            <div class="mb-3 text-end">
                                <a href="${pageContext.request.contextPath}/events/export-participants?id=${evenement.id}" 
                                   class="btn btn-outline-success btn-sm">
                                    <i class="fas fa-file-excel"></i> Exporter en CSV
                                </a>
                            </div>

                            <div class="table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                        <tr>
                                            <th>Participant</th>
                                            <th>Email</th>
                                            <th>Date d'inscription</th>
                                            <th>Statut</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="inscription" items="${inscriptions}">
                                            <tr>
                                                <td>
                                                    <i class="fas fa-user-circle fa-lg me-2 text-muted"></i>
                                                    <strong><c:out value="${inscription.participant.nom}"/></strong>
                                                </td>
                                                <td>
                                                    <c:out value="${inscription.participant.email}"/>
                                                </td>
                                                <td>
                                                    ${inscription.dateInscription.dayOfMonth < 10 ? '0' : ''}${inscription.dateInscription.dayOfMonth}/${inscription.dateInscription.monthValue < 10 ? '0' : ''}${inscription.dateInscription.monthValue}/${inscription.dateInscription.year} ${inscription.dateInscription.hour < 10 ? '0' : ''}${inscription.dateInscription.hour}:${inscription.dateInscription.minute < 10 ? '0' : ''}${inscription.dateInscription.minute}
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${inscription.statut == 'CONFIRMEE'}">
                                                            <span class="badge bg-success">Confirmée</span>
                                                        </c:when>
                                                        <c:when test="${inscription.statut == 'EN_ATTENTE'}">
                                                            <span class="badge bg-warning">En attente</span>
                                                        </c:when>
                                                        <c:when test="${inscription.statut == 'ANNULEE'}">
                                                            <span class="badge bg-danger">Annulée</span>
                                                        </c:when>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <c:if test="${inscription.statut == 'EN_ATTENTE'}">
                                                        <form action="${pageContext.request.contextPath}/events/confirm-inscription" 
                                                              method="post" class="d-inline">
                                                            <input type="hidden" name="inscriptionId" value="${inscription.id}">
                                                            <input type="hidden" name="eventId" value="${evenement.id}">
                                                            <button type="submit" class="btn btn-sm btn-outline-success" 
                                                                    title="Confirmer">
                                                                <i class="fas fa-check"></i>
                                                            </button>
                                                        </form>
                                                    </c:if>
                                                    <button type="button" class="btn btn-sm btn-outline-primary" 
                                                            title="Contacter" 
                                                            onclick="window.location.href='mailto:${inscription.participant.email}'">
                                                        <i class="fas fa-envelope"></i>
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Sidebar -->
        <div class="col-lg-4">
            <!-- Quick Actions -->
            <div class="card mb-4">
                <div class="card-header bg-primary text-white">
                    <h5 class="mb-0">
                        <i class="fas fa-bolt"></i> Actions rapides
                    </h5>
                </div>
                <div class="card-body">
                    <div class="d-grid gap-2">
                        <a href="${pageContext.request.contextPath}/events/edit?id=${evenement.id}" 
                           class="btn btn-outline-primary">
                            <i class="fas fa-edit"></i> Modifier l'événement
                        </a>

                        <c:if test="${evenement.statut == 'BROUILLON'}">
                            <form action="${pageContext.request.contextPath}/events/publish" method="post">
                                <input type="hidden" name="id" value="${evenement.id}">
                                <button type="submit" class="btn btn-outline-success w-100"
                                        onclick="return confirm('Publier cet événement ?')">
                                    <i class="fas fa-check-circle"></i> Publier l'événement
                                </button>
                            </form>
                        </c:if>


                        <c:if test="${evenement.statut == 'PUBLIE'}">
                            <form action="${pageContext.request.contextPath}/events/cancel" method="post">
                                <input type="hidden" name="id" value="${evenement.id}">
                                <button type="submit" class="btn btn-outline-warning w-100"
                                        onclick="return confirm('Annuler cet événement ?')">
                                    <i class="fas fa-ban"></i> Annuler l'événement
                                </button>
                            </form>
                        </c:if>



                        <hr>

                        <form action="${pageContext.request.contextPath}/events/delete" method="post">
                            <input type="hidden" name="id" value="${evenement.id}">
                            <button type="submit" class="btn btn-outline-danger w-100"
                                    onclick="return confirm('Êtes-vous sûr de vouloir supprimer cet événement ? Cette action est irréversible.')">
                                <i class="fas fa-trash"></i> Supprimer
                            </button>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Event Info -->
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0">
                        <i class="fas fa-info-circle"></i> Informations
                    </h5>
                </div>
                <div class="card-body">
                    <p class="mb-2">
                        <strong>Capacité:</strong><br>
                        ${evenement.capacite} personnes
                    </p>
                    <div class="progress mb-3" style="height: 25px;">
                        <div class="progress-bar ${evenement.nombreInscriptions >= evenement.capacite ? 'bg-danger' : 'bg-success'}" 
                             role="progressbar" 
                             style="width: ${(evenement.nombreInscriptions * 100) / evenement.capacite}%">
                            ${evenement.nombreInscriptions} / ${evenement.capacite}
                        </div>
                    </div>

                    <p class="mb-2">
                        <strong>Prix:</strong><br>
                        <c:choose>
                            <c:when test="${evenement.gratuit}">
                                <span class="badge bg-success">Gratuit</span>
                            </c:when>
                            <c:otherwise>
                                <fmt:formatNumber value="${evenement.prix}" type="currency" currencySymbol="MAD"/>
                            </c:otherwise>
                        </c:choose>
                    </p>

                    <hr>

                    <p class="mb-1 small text-muted">
                        <i class="fas fa-clock"></i> Créé le:
                        ${evenement.dateCreation.dayOfMonth}/${evenement.dateCreation.monthValue < 10 ? '0' : ''}${evenement.dateCreation.monthValue}/${evenement.dateCreation.year}
                    </p>
                    <c:if test="${not empty evenement.dateModification}">
                        <p class="mb-0 small text-muted">
                            <i class="fas fa-edit"></i> Modifié le:
                            ${evenement.dateModification.dayOfMonth}/${evenement.dateModification.monthValue < 10 ? '0' : ''}${evenement.dateModification.monthValue}/${evenement.dateModification.year}
                        </p>
                    </c:if>
                </div>
            </div>


        </div>
    </div>
</div>

<script>
const eventUrl = window.location.origin + '${pageContext.request.contextPath}/events/details?id=${evenement.id}';
const eventTitle = '${fn:escapeXml(evenement.titre)}';

function shareOnFacebook() {
    window.open('https://www.facebook.com/sharer/sharer.php?u=' + encodeURIComponent(eventUrl), '_blank');
}

function shareOnTwitter() {
    window.open('https://twitter.com/intent/tweet?text=' + encodeURIComponent(eventTitle) + '&url=' + encodeURIComponent(eventUrl), '_blank');
}

function shareOnWhatsApp() {
    window.open('https://wa.me/?text=' + encodeURIComponent(eventTitle + ' - ' + eventUrl), '_blank');
}

function copyLink() {
    navigator.clipboard.writeText(eventUrl).then(function() {
        alert('Lien copié dans le presse-papiers !');
    }, function() {
        alert('Erreur lors de la copie du lien');
    });
}
</script>

<jsp:include page="../layout/footer.jsp" />
