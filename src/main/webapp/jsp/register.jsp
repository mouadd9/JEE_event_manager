<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription - Event Management Platform</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="bg-light">
    <div class="container">
        <div class="row justify-content-center mt-5">
            <div class="col-md-8 col-lg-6">
                <div class="card shadow">
                    <div class="card-body p-5">
                        <h2 class="text-center mb-4">Créer un compte</h2>

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger" role="alert">
                                ${error}
                            </div>
                        </c:if>

                        <form method="post" action="${pageContext.request.contextPath}/register" id="registerForm">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="prenom" class="form-label">Prénom *</label>
                                    <input type="text" class="form-control" id="prenom" name="prenom"
                                           value="${prenom}" required>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label for="nom" class="form-label">Nom *</label>
                                    <input type="text" class="form-control" id="nom" name="nom"
                                           value="${nom}" required>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="email" class="form-label">Email *</label>
                                <input type="email" class="form-control" id="email" name="email"
                                       value="${email}" required>
                            </div>

                            <div class="mb-3">
                                <label for="password" class="form-label">Mot de passe *</label>
                                <input type="password" class="form-control" id="password" name="password" required>
                                <div class="form-text">
                                    Le mot de passe doit contenir au moins 8 caractères, une majuscule,
                                    une minuscule et un chiffre.
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="confirmPassword" class="form-label">Confirmer le mot de passe *</label>
                                <input type="password" class="form-control" id="confirmPassword"
                                       name="confirmPassword" required>
                            </div>

                            <div class="mb-3 form-check">
                                <input type="checkbox" class="form-check-input" id="terms" required>
                                <label class="form-check-label" for="terms">
                                    J'accepte les <a href="#" data-bs-toggle="modal" data-bs-target="#termsModal">conditions d'utilisation</a>
                                </label>
                            </div>

                            <div class="d-grid">
                                <button type="submit" class="btn btn-primary btn-lg">Créer mon compte</button>
                            </div>
                        </form>

                        <hr class="my-4">

                        <div class="text-center">
                            <span>Vous avez déjà un compte?</span>
                            <a href="${pageContext.request.contextPath}/login">Se connecter</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Terms and Conditions Modal -->
    <div class="modal fade" id="termsModal" tabindex="-1" aria-labelledby="termsModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-dialog-scrollable">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="termsModalLabel">Conditions d'utilisation</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <h6>1. Acceptation des conditions</h6>
                    <p>En créant un compte sur Event Management Platform, vous acceptez d'être lié par ces conditions d'utilisation.</p>

                    <h6>2. Utilisation du service</h6>
                    <p>Vous vous engagez à utiliser ce service de manière responsable et conforme aux lois en vigueur.</p>

                    <h6>3. Compte utilisateur</h6>
                    <p>Vous êtes responsable de maintenir la confidentialité de votre mot de passe et de toutes les activités effectuées sous votre compte.</p>

                    <h6>4. Contenu utilisateur</h6>
                    <p>Vous conservez tous les droits sur le contenu que vous publiez, mais vous accordez à Event Management Platform une licence pour l'utiliser dans le cadre du service.</p>

                    <h6>5. Protection des données</h6>
                    <p>Vos données personnelles sont traitées conformément à notre politique de confidentialité et au RGPD.</p>

                    <h6>6. Modification des conditions</h6>
                    <p>Nous nous réservons le droit de modifier ces conditions à tout moment. Les modifications seront effectives dès leur publication.</p>

                    <h6>7. Résiliation</h6>
                    <p>Nous pouvons suspendre ou résilier votre compte en cas de violation de ces conditions.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fermer</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Client-side password validation
        document.getElementById('registerForm').addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            if (password !== confirmPassword) {
                e.preventDefault();
                alert('Les mots de passe ne correspondent pas');
                return false;
            }

            if (password.length < 8) {
                e.preventDefault();
                alert('Le mot de passe doit contenir au moins 8 caractères');
                return false;
            }
        });
    </script>
</body>
</html>
