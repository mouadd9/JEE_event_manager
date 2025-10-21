<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Parcourir les événements" scope="request"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/messages.jsp"/>

<!-- Page Header -->
<section class="bg-primary text-white py-4">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-8">
                <h1 class="mb-0">
                    <i class="fas fa-calendar-alt"></i> Parcourir les événements
                </h1>
                <p class="mb-0 mt-2">Découvrez tous les événements disponibles</p>
            </div>
            <div class="col-md-4 text-md-end">
                <span class="badge bg-light text-dark fs-6">
                    ${totalEvents} événement${totalEvents > 1 ? 's' : ''} trouvé${totalEvents > 1 ? 's' : ''}
                </span>
            </div>
        </div>
    </div>
</section>

<!-- Search and Filters Section -->
<section class="py-4 bg-light border-bottom">
    <div class="container">
        <form action="${pageContext.request.contextPath}/events/browse" method="get" id="searchForm">
            <div class="row g-3">
                <!-- Keyword Search -->
                <div class="col-md-4">
                    <div class="input-group">
                        <span class="input-group-text bg-white">
                            <i class="fas fa-search text-muted"></i>
                        </span>
                        <input type="text" 
                               class="form-control" 
                               name="keyword" 
                               placeholder="Rechercher un événement..."
                               value="${fn:escapeXml(keyword)}">
                    </div>
                </div>

                <!-- Category Filter -->
                <div class="col-md-3">
                    <select class="form-select" name="categorieId" id="categorieFilter">
                        <option value="">Toutes les catégories</option>
                        <c:forEach items="${categories}" var="categorie">
                            <option value="${categorie.id}" 
                                    ${categorie.id == selectedCategorieId ? 'selected' : ''}>
                                <c:out value="${categorie.nom}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <!-- Date Range -->
                <div class="col-md-2">
                    <input type="date" 
                           class="form-control" 
                           name="dateDebut" 
                           placeholder="Date début"
                           value="${dateDebut}">
                </div>
                <div class="col-md-2">
                    <input type="date" 
                           class="form-control" 
                           name="dateFin" 
                           placeholder="Date fin"
                           value="${dateFin}">
                </div>

                <!-- Search Button -->
                <div class="col-md-1">
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="fas fa-search"></i>
                    </button>
                </div>
            </div>

            <!-- Active Filters Display -->
            <c:if test="${not empty keyword or not empty selectedCategorieId or not empty dateDebut or not empty dateFin}">
                <div class="mt-3">
                    <span class="text-muted me-2">Filtres actifs:</span>
                    <c:if test="${not empty keyword}">
                        <span class="badge bg-secondary me-1">
                            <i class="fas fa-search"></i> "${fn:escapeXml(keyword)}"
                            <a href="?keyword=" class="text-white text-decoration-none ms-1">×</a>
                        </span>
                    </c:if>
                    <c:if test="${not empty selectedCategorieId}">
                        <c:forEach items="${categories}" var="cat">
                            <c:if test="${cat.id == selectedCategorieId}">
                                <span class="badge bg-secondary me-1">
                                    <i class="fas fa-tag"></i> ${cat.nom}
                                    <a href="?categorieId=" class="text-white text-decoration-none ms-1">×</a>
                                </span>
                            </c:if>
                        </c:forEach>
                    </c:if>
                    <c:if test="${not empty dateDebut}">
                        <span class="badge bg-secondary me-1">
                            <i class="fas fa-calendar"></i> Du ${dateDebut}
                            <a href="?dateDebut=" class="text-white text-decoration-none ms-1">×</a>
                        </span>
                    </c:if>
                    <c:if test="${not empty dateFin}">
                        <span class="badge bg-secondary me-1">
                            <i class="fas fa-calendar"></i> Au ${dateFin}
                            <a href="?dateFin=" class="text-white text-decoration-none ms-1">×</a>
                        </span>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/events/browse" class="btn btn-sm btn-outline-secondary">
                        <i class="fas fa-times"></i> Réinitialiser
                    </a>
                </div>
            </c:if>
        </form>
    </div>
</section>

<!-- Events Grid -->
<section class="py-5">
    <div class="container">
        <c:choose>
            <c:when test="${not empty events}">
                <div class="row g-4">
                    <c:forEach items="${events}" var="event">
                        <div class="col-md-6 col-lg-4">
                            <div class="card h-100 shadow-sm border-0 hover-shadow">
                                <!-- Event Image -->
                                <c:choose>
                                    <c:when test="${not empty event.imageUrl}">
                                        <img src="${event.imageUrl}" 
                                             class="card-img-top" 
                                             alt="<c:out value='${event.titre}'/>"
                                             style="height: 200px; object-fit: cover;">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="card-img-top bg-gradient d-flex align-items-center justify-content-center" 
                                             style="height: 200px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                                            <i class="fas fa-calendar-alt fa-4x text-white opacity-75"></i>
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                                <div class="card-body">
                                    <!-- Title -->
                                    <h5 class="card-title mb-3">
                                        <c:out value="${event.titre}"/>
                                    </h5>
                                    
                                    <!-- Location -->
                                    <p class="card-text text-muted small mb-2">
                                        <i class="fas fa-map-marker-alt text-danger"></i>
                                        <c:out value="${event.lieu}"/>
                                    </p>
                                    
                                    <!-- Date -->
                                    <p class="card-text small mb-2">
                                        <i class="fas fa-calendar text-primary"></i>
                                        ${event.dateDebut.dayOfMonth < 10 ? '0' : ''}${event.dateDebut.dayOfMonth}/${event.dateDebut.monthValue < 10 ? '0' : ''}${event.dateDebut.monthValue}/${event.dateDebut.year}
                                        à
                                        ${event.dateDebut.hour < 10 ? '0' : ''}${event.dateDebut.hour}:${event.dateDebut.minute < 10 ? '0' : ''}${event.dateDebut.minute}
                                    </p>

                                    <!-- Price -->
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

                                <!-- Card Footer -->
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

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <nav aria-label="Pagination des événements" class="mt-5">
                        <ul class="pagination justify-content-center">
                            <!-- Previous Button -->
                            <li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
                                <a class="page-link" 
                                   href="?page=${currentPage - 1}&keyword=${fn:escapeXml(keyword)}&categorieId=${selectedCategorieId}&dateDebut=${dateDebut}&dateFin=${dateFin}"
                                   aria-label="Précédent">
                                    <span aria-hidden="true">&laquo;</span>
                                </a>
                            </li>

                            <!-- Page Numbers -->
                            <c:forEach begin="0" end="${totalPages - 1}" var="pageNum">
                                <c:if test="${pageNum < 10 || (pageNum >= currentPage - 2 && pageNum <= currentPage + 2) || pageNum >= totalPages - 2}">
                                    <li class="page-item ${pageNum == currentPage ? 'active' : ''}">
                                        <a class="page-link" 
                                           href="?page=${pageNum}&keyword=${fn:escapeXml(keyword)}&categorieId=${selectedCategorieId}&dateDebut=${dateDebut}&dateFin=${dateFin}">
                                            ${pageNum + 1}
                                        </a>
                                    </li>
                                </c:if>
                            </c:forEach>

                            <!-- Next Button -->
                            <li class="page-item ${currentPage >= totalPages - 1 ? 'disabled' : ''}">
                                <a class="page-link" 
                                   href="?page=${currentPage + 1}&keyword=${fn:escapeXml(keyword)}&categorieId=${selectedCategorieId}&dateDebut=${dateDebut}&dateFin=${dateFin}"
                                   aria-label="Suivant">
                                    <span aria-hidden="true">&raquo;</span>
                                </a>
                            </li>
                        </ul>
                    </nav>
                </c:if>

            </c:when>
            <c:otherwise>
                <!-- No Events Found -->
                <div class="text-center py-5">
                    <i class="fas fa-calendar-times fa-5x text-muted mb-4"></i>
                    <h3 class="text-muted mb-3">Aucun événement trouvé</h3>
                    <p class="text-muted mb-4">
                        <c:choose>
                            <c:when test="${not empty keyword or not empty selectedCategorieId or not empty dateDebut or not empty dateFin}">
                                Essayez de modifier vos critères de recherche pour voir plus de résultats.
                            </c:when>
                            <c:otherwise>
                                Il n'y a actuellement aucun événement disponible. Revenez bientôt !
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <a href="${pageContext.request.contextPath}/events/browse" 
                       class="btn btn-primary">
                        <i class="fas fa-redo"></i> Réinitialiser la recherche
                    </a>
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
    
    .card-img-top {
        transition: transform 0.3s ease;
        overflow: hidden;
    }
    
    .card:hover .card-img-top {
        transform: scale(1.05);
    }

    .page-link {
        color: #667eea;
    }

    .page-item.active .page-link {
        background-color: #667eea;
        border-color: #667eea;
    }

    .badge {
        font-weight: 500;
    }
</style>

<jsp:include page="../layout/footer.jsp"/>
