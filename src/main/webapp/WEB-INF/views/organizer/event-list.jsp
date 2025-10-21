<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="pageTitle" value="Mes Événements" />
<jsp:include page="../layout/header.jsp" />
<jsp:include page="../layout/messages.jsp" />

<div class="container py-4">
    <div class="row mb-4">
        <div class="col">
            <h2 class="mb-0">
                <i class="fas fa-calendar-alt"></i> Mes Événements
            </h2>
            <p class="text-muted">Gérez tous vos événements</p>
        </div>
        <div class="col-auto">
            <a href="${pageContext.request.contextPath}/events/create" class="btn btn-primary">
                <i class="fas fa-plus"></i> Créer un événement
            </a>
        </div>
    </div>

    <!-- Filters -->
    <div class="card mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/events/manage" method="get" class="row g-3">
                <div class="col-md-3">
                    <label for="statut" class="form-label">Statut</label>
                    <select name="statut" id="statut" class="form-select">
                        <option value="">Tous les statuts</option>
                        <option value="BROUILLON" ${param.statut == 'BROUILLON' ? 'selected' : ''}>Brouillon</option>
                        <option value="PUBLIE" ${param.statut == 'PUBLIE' ? 'selected' : ''}>Publié</option>
                        <option value="ANNULE" ${param.statut == 'ANNULE' ? 'selected' : ''}>Annulé</option>
                        <option value="TERMINE" ${param.statut == 'TERMINE' ? 'selected' : ''}>Terminé</option>
                    </select>
                </div>
                <div class="col-md-4">
                    <label for="keyword" class="form-label">Rechercher</label>
                    <input type="text" name="keyword" id="keyword" class="form-control" 
                           placeholder="Titre de l'événement..." value="${param.keyword}">
                </div>
                <div class="col-md-3">
                    <label for="sortBy" class="form-label">Trier par</label>
                    <select name="sortBy" id="sortBy" class="form-select">
                        <option value="dateDebut" ${param.sortBy == 'dateDebut' ? 'selected' : ''}>Date de début</option>
                        <option value="dateCreation" ${param.sortBy == 'dateCreation' ? 'selected' : ''}>Date de création</option>
                        <option value="titre" ${param.sortBy == 'titre' ? 'selected' : ''}>Titre</option>
                        <option value="nombreInscriptions" ${param.sortBy == 'nombreInscriptions' ? 'selected' : ''}>Inscriptions</option>
                    </select>
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button type="submit" class="btn btn-secondary w-100">
                        <i class="fas fa-filter"></i> Filtrer
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- Events Table -->
    <div class="card">
        <div class="card-body">
            <c:choose>
                <c:when test="${empty evenements}">
                    <div class="text-center py-5">
                        <i class="fas fa-calendar-times fa-5x text-muted mb-3"></i>
                        <h4 class="text-muted">Aucun événement trouvé</h4>
                        <p class="text-muted">Commencez par créer votre premier événement</p>
                        <a href="${pageContext.request.contextPath}/events/create" class="btn btn-primary">
                            <i class="fas fa-plus"></i> Créer un événement
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Image</th>
                                    <th>Titre</th>
                                    <th>Date</th>
                                    <th>Lieu</th>
                                    <th>Statut</th>
                                    <th>Inscriptions</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="event" items="${evenements}">
                                    <tr>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty event.imageUrl}">
                                                    <img src="${event.imageUrl}" alt="${event.titre}" 
                                                         class="rounded" style="width: 60px; height: 60px; object-fit: cover;">
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="bg-light rounded d-flex align-items-center justify-content-center" 
                                                         style="width: 60px; height: 60px;">
                                                        <i class="fas fa-calendar-alt fa-2x text-muted"></i>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <strong><c:out value="${event.titre}"/></strong><br>
                                            <small class="text-muted">
                                                <i class="fas fa-eye"></i> ${event.nombreVues} vues
                                            </small>
                                        </td>
                                        <td>
                                            <c:set var="dayOfMonth" value="${event.dateDebut.dayOfMonth}" />
                                            <c:set var="monthValue" value="${event.dateDebut.monthValue}" />
                                            <c:set var="year" value="${event.dateDebut.year}" />
                                            <c:set var="hour" value="${event.dateDebut.hour}" />
                                            <c:set var="minute" value="${event.dateDebut.minute}" />
                                            
                                            ${dayOfMonth < 10 ? '0' : ''}${dayOfMonth}/${monthValue < 10 ? '0' : ''}${monthValue}/${year}<br>
                                            <small class="text-muted">
                                                ${hour < 10 ? '0' : ''}${hour}:${minute < 10 ? '0' : ''}${minute}
                                            </small>
                                        </td>
                                        <td>
                                            <c:out value="${event.lieu}"/>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${event.statut == 'BROUILLON'}">
                                                    <span class="badge bg-secondary">Brouillon</span>
                                                </c:when>
                                                <c:when test="${event.statut == 'PUBLIE'}">
                                                    <span class="badge bg-success">Publié</span>
                                                </c:when>
                                                <c:when test="${event.statut == 'ANNULE'}">
                                                    <span class="badge bg-danger">Annulé</span>
                                                </c:when>
                                                <c:when test="${event.statut == 'TERMINE'}">
                                                    <span class="badge bg-dark">Terminé</span>
                                                </c:when>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div class="d-flex align-items-center">
                                                <span class="me-2">${event.nombreInscriptions}/${event.capacite}</span>
                                                <div class="progress flex-grow-1" style="height: 8px; width: 50px;">
                                                    <c:set var="percentFilled" value="${event.capacite > 0 ? (event.nombreInscriptions * 100) / event.capacite : 0}" />
                                                    <c:choose>
                                                        <c:when test="${event.nombreInscriptions >= event.capacite}">
                                                            <div class="progress-bar bg-danger" role="progressbar" style="width: ${percentFilled}%"></div>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="progress-bar bg-success" role="progressbar" style="width: ${percentFilled}%"></div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </td>
                                        <td>
                                            <div class="btn-group" role="group">
                                                <a href="${pageContext.request.contextPath}/organizer/events/view?id=${event.id}" 
                                                   class="btn btn-sm btn-outline-primary" title="Voir">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/events/edit?id=${event.id}" 
                                                   class="btn btn-sm btn-outline-secondary" title="Modifier">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <c:if test="${event.statut == 'BROUILLON'}">
                                                    <form action="${pageContext.request.contextPath}/events/publish" 
                                                          method="post" class="d-inline">
                                                        <input type="hidden" name="id" value="${event.id}">
                                                        <button type="submit" class="btn btn-sm btn-outline-success" title="Publier"
                                                                onclick="return confirm('Publier cet événement ?')">
                                                            <i class="fas fa-check"></i>
                                                        </button>
                                                    </form>
                                                </c:if>
                                                <c:if test="${event.statut == 'PUBLIE'}">
                                                    <form action="${pageContext.request.contextPath}/events/cancel" 
                                                          method="post" class="d-inline">
                                                        <input type="hidden" name="id" value="${event.id}">
                                                        <button type="submit" class="btn btn-sm btn-outline-warning" title="Annuler"
                                                                onclick="return confirm('Annuler cet événement ?')">
                                                            <i class="fas fa-ban"></i>
                                                        </button>
                                                    </form>
                                                </c:if>
                                                <form action="${pageContext.request.contextPath}/events/delete" 
                                                      method="post" class="d-inline">
                                                    <input type="hidden" name="id" value="${event.id}">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger" title="Supprimer"
                                                            onclick="return confirm('Êtes-vous sûr de vouloir supprimer cet événement ?')">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <nav aria-label="Page navigation" class="mt-4">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="?page=${currentPage - 1}&statut=${param.statut}&keyword=${param.keyword}&sortBy=${param.sortBy}">
                                        Précédent
                                    </a>
                                </li>
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link" href="?page=${i}&statut=${param.statut}&keyword=${param.keyword}&sortBy=${param.sortBy}">
                                            ${i}
                                        </a>
                                    </li>
                                </c:forEach>
                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="?page=${currentPage + 1}&statut=${param.statut}&keyword=${param.keyword}&sortBy=${param.sortBy}">
                                        Suivant
                                    </a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" />
