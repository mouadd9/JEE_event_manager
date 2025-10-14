<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Réinitialiser le mot de passe - Event Management Platform</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="bg-light">
    <div class="container">
        <div class="row justify-content-center mt-5">
            <div class="col-md-6 col-lg-5">
                <div class="card shadow">
                    <div class="card-body p-5">
                        <h2 class="text-center mb-4">Nouveau mot de passe</h2>

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger" role="alert">
                                ${error}
                            </div>
                        </c:if>

                        <c:if test="${not empty token}">
                            <form method="post" action="${pageContext.request.contextPath}/reset-password">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="token" value="${token}">

                                <div class="mb-3">
                                    <label for="newPassword" class="form-label">Nouveau mot de passe</label>
                                    <input type="password" class="form-control" id="newPassword" name="newPassword"
                                           placeholder="Minimum 8 caractères" required autofocus>
                                    <div class="form-text">
                                        Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial.
                                    </div>
                                </div>

                                <div class="mb-4">
                                    <label for="confirmPassword" class="form-label">Confirmer le mot de passe</label>
                                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                                           placeholder="Retapez le mot de passe" required>
                                </div>

                                <div class="d-grid mb-3">
                                    <button type="submit" class="btn btn-primary btn-lg">Réinitialiser</button>
                                </div>
                            </form>
                        </c:if>

                        <c:if test="${empty token}">
                            <p class="text-center text-muted">
                                Le lien de réinitialisation est invalide ou a expiré.
                            </p>
                            <div class="text-center mt-4">
                                <a href="${pageContext.request.contextPath}/forgot-password" class="btn btn-outline-primary">
                                    Faire une nouvelle demande
                                </a>
                            </div>
                        </c:if>

                        <hr class="my-4">

                        <div class="text-center">
                            <a href="${pageContext.request.contextPath}/login">Retour à la connexion</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
