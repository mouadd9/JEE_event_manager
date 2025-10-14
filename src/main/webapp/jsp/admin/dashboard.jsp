<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Event Management Platform</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container-fluid">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">
                <i class="bi bi-calendar-event"></i> Event Management
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/admin/dashboard">
                            <i class="bi bi-speedometer2"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/profile">
                            <i class="bi bi-person"></i> ${sessionScope.userEmail}
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                            <i class="bi bi-box-arrow-right"></i> Déconnexion
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div class="row">
            <div class="col-12">
                <h1 class="mb-4">
                    <i class="bi bi-shield-check"></i> Administration
                </h1>

                <!-- Success/Error Messages -->
                <c:if test="${not empty sessionScope.successMessage}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="bi bi-check-circle"></i> ${sessionScope.successMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <c:remove var="successMessage" scope="session" />
                </c:if>

                <c:if test="${not empty sessionScope.errorMessage}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="bi bi-exclamation-triangle"></i> ${sessionScope.errorMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <c:remove var="errorMessage" scope="session" />
                </c:if>

                <!-- Pending Role Upgrade Requests -->
                <div class="card">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0">
                            <i class="bi bi-arrow-up-circle"></i> Demandes d'upgrade de rôle en attente
                            <span class="badge bg-light text-primary ms-2">${pendingRequests.size()}</span>
                        </h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${empty pendingRequests}">
                                <div class="text-center py-5 text-muted">
                                    <i class="bi bi-inbox" style="font-size: 3rem;"></i>
                                    <p class="mt-3">Aucune demande en attente</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th>Date</th>
                                                <th>Email du participant</th>
                                                <th>Nom organisation</th>
                                                <th>Description</th>
                                                <th>SIRET</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="req" items="${pendingRequests}">
                                                <tr>
                                                    <td>
                                                        <fmt:formatDate value="${req.dateDemande}" pattern="dd/MM/yyyy HH:mm" />
                                                    </td>
                                                    <td>
                                                        ${participantEmails[req.participantId]}
                                                    </td>
                                                    <td><strong>${req.nomOrganisation}</strong></td>
                                                    <td>
                                                        <div style="max-width: 300px; overflow: hidden; text-overflow: ellipsis;">
                                                            ${req.description}
                                                        </div>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty req.numSiret}">
                                                                ${req.numSiret}
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="text-muted">-</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <button type="button" class="btn btn-sm btn-success"
                                                                data-bs-toggle="modal"
                                                                data-bs-target="#approveModal${req.id}">
                                                            <i class="bi bi-check-lg"></i> Approuver
                                                        </button>
                                                        <button type="button" class="btn btn-sm btn-danger"
                                                                data-bs-toggle="modal"
                                                                data-bs-target="#rejectModal${req.id}">
                                                            <i class="bi bi-x-lg"></i> Refuser
                                                        </button>
                                                    </td>
                                                </tr>

                                                <!-- Approve Modal -->
                                                <div class="modal fade" id="approveModal${req.id}" tabindex="-1">
                                                    <div class="modal-dialog">
                                                        <div class="modal-content">
                                                            <div class="modal-header bg-success text-white">
                                                                <h5 class="modal-title">Approuver la demande</h5>
                                                                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                                                            </div>
                                                            <form method="post" action="${pageContext.request.contextPath}/admin/dashboard">
                                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                                <input type="hidden" name="action" value="approve">
                                                                <input type="hidden" name="requestId" value="${req.id}">

                                                                <div class="modal-body">
                                                                    <p>Êtes-vous sûr de vouloir approuver cette demande de <strong>${req.nomOrganisation}</strong> ?</p>

                                                                    <div class="mb-3">
                                                                        <label for="approveComment${req.id}" class="form-label">Commentaire (optionnel)</label>
                                                                        <textarea class="form-control" id="approveComment${req.id}"
                                                                                name="comment" rows="3"
                                                                                placeholder="Message de félicitations..."></textarea>
                                                                    </div>
                                                                </div>
                                                                <div class="modal-footer">
                                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                                                                    <button type="submit" class="btn btn-success">
                                                                        <i class="bi bi-check-lg"></i> Confirmer l'approbation
                                                                    </button>
                                                                </div>
                                                            </form>
                                                        </div>
                                                    </div>
                                                </div>

                                                <!-- Reject Modal -->
                                                <div class="modal fade" id="rejectModal${req.id}" tabindex="-1">
                                                    <div class="modal-dialog">
                                                        <div class="modal-content">
                                                            <div class="modal-header bg-danger text-white">
                                                                <h5 class="modal-title">Refuser la demande</h5>
                                                                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                                                            </div>
                                                            <form method="post" action="${pageContext.request.contextPath}/admin/dashboard">
                                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                                <input type="hidden" name="action" value="reject">
                                                                <input type="hidden" name="requestId" value="${req.id}">

                                                                <div class="modal-body">
                                                                    <p>Vous êtes sur le point de refuser la demande de <strong>${req.nomOrganisation}</strong>.</p>

                                                                    <div class="mb-3">
                                                                        <label for="rejectComment${req.id}" class="form-label">Raison du refus <span class="text-danger">*</span></label>
                                                                        <textarea class="form-control" id="rejectComment${req.id}"
                                                                                name="comment" rows="3" required
                                                                                placeholder="Expliquez pourquoi cette demande est refusée..."></textarea>
                                                                    </div>
                                                                </div>
                                                                <div class="modal-footer">
                                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                                                                    <button type="submit" class="btn btn-danger">
                                                                        <i class="bi bi-x-lg"></i> Confirmer le refus
                                                                    </button>
                                                                </div>
                                                            </form>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Quick Stats -->
                <div class="row mt-4">
                    <div class="col-md-4">
                        <div class="card text-white bg-info">
                            <div class="card-body">
                                <h5 class="card-title">
                                    <i class="bi bi-people"></i> Utilisateurs
                                </h5>
                                <p class="card-text fs-3">Géré via module Event</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card text-white bg-warning">
                            <div class="card-body">
                                <h5 class="card-title">
                                    <i class="bi bi-calendar-event"></i> Événements
                                </h5>
                                <p class="card-text fs-3">Géré via module Event</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card text-white bg-success">
                            <div class="card-body">
                                <h5 class="card-title">
                                    <i class="bi bi-arrow-up-circle"></i> Demandes
                                </h5>
                                <p class="card-text fs-3">${pendingRequests.size()}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
