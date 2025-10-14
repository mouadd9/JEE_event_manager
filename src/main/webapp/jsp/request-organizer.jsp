<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Demander le statut d'organisateur</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">Event Management</a>
            <div class="navbar-nav ms-auto">
                <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/profile">Mon Profil</a>
            </div>
        </div>
    </nav>

    <div class="container mt-5">
        <div class="row">
            <div class="col-md-8 offset-md-2">
                <h2>Demander le statut d'organisateur</h2>

                <c:if test="${not empty success}">
                    <div class="alert alert-success">${success}</div>
                </c:if>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <div class="card mt-3">
                    <div class="card-body">
                        <p class="text-muted">
                            Pour devenir organisateur, veuillez remplir ce formulaire.
                            Votre demande sera examinée par un administrateur.
                        </p>

                        <form method="post" action="${pageContext.request.contextPath}/request-organizer">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                            <div class="mb-3">
                                <label class="form-label">Nom de l'organisation *</label>
                                <input type="text" class="form-control" name="nomOrganisation"
                                       value="${nomOrganisation}" required>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Description de votre organisation *</label>
                                <textarea class="form-control" name="description" rows="5"
                                          required>${description}</textarea>
                                <small class="text-muted">Minimum 50 caractères</small>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Site Web (optionnel)</label>
                                <input type="url" class="form-control" name="siteWeb" value="${siteWeb}"
                                       placeholder="https://www.exemple.com">
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Numéro SIRET (optionnel)</label>
                                <input type="text" class="form-control" name="numSiret" value="${numSiret}"
                                       placeholder="12345678901234">
                                <small class="text-muted">14 chiffres</small>
                            </div>

                            <button type="submit" class="btn btn-primary">Soumettre la demande</button>
                            <a href="${pageContext.request.contextPath}/profile" class="btn btn-secondary">Annuler</a>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
