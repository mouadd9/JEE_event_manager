<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mon Profil - Event Management Platform</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">Event Management</a>
            <div class="navbar-nav ms-auto">
                <span class="navbar-text text-white me-3">
                    ${sessionScope.currentUser.nomComplet}
                </span>
                <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/logout">Déconnexion</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div class="row">
            <div class="col-md-8 offset-md-2">
                <h2>Mon Profil</h2>

                <c:if test="${not empty success}">
                    <div class="alert alert-success">${success}</div>
                </c:if>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <div class="card mt-3">
                    <div class="card-body">
                        <h5 class="card-title">Informations personnelles</h5>
                        <form method="post" action="${pageContext.request.contextPath}/profile">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Prénom</label>
                                    <input type="text" class="form-control" name="prenom" value="${user.prenom}">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Nom</label>
                                    <input type="text" class="form-control" name="nom" value="${user.nom}">
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Email</label>
                                <input type="email" class="form-control" value="${user.email}" disabled>
                                <small class="text-muted">L'email ne peut pas être modifié</small>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Téléphone</label>
                                <input type="tel" class="form-control" name="telephone" value="${user.telephone}">
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Adresse</label>
                                <input type="text" class="form-control" name="adresse" value="${user.adresse}">
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Ville</label>
                                    <input type="text" class="form-control" name="ville" value="${user.ville}">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Code Postal</label>
                                    <input type="text" class="form-control" name="codePostal" value="${user.codePostal}">
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Pays</label>
                                <input type="text" class="form-control" name="pays" value="${user.pays}">
                            </div>

                            <button type="submit" class="btn btn-primary">Mettre à jour</button>
                        </form>
                    </div>
                </div>

                <c:if test="${sessionScope.userType == 'participant'}">
                    <div class="card mt-3">
                        <div class="card-body">
                            <h5 class="card-title">Devenir Organisateur</h5>
                            <p>Vous souhaitez organiser vos propres événements?</p>
                            <a href="${pageContext.request.contextPath}/request-organizer" class="btn btn-success">
                                Demander le statut d'organisateur
                            </a>
                        </div>
                    </div>
                </c:if>

                <c:if test="${sessionScope.userType == 'organisateur'}">
                    <div class="card mt-3">
                        <div class="card-body">
                            <h5 class="card-title">Organisateur</h5>
                            <p class="text-success">Vous avez le statut d'organisateur</p>
                            <a href="${pageContext.request.contextPath}/organizer/dashboard" class="btn btn-primary">
                                Gérer mes événements
                            </a>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
