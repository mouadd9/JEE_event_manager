<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription - Event Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body class="bg-light">
    <jsp:include page="../layout/header.jsp"/>
    <jsp:include page="../layout/messages.jsp"/>

    <div class="container my-5">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-7">
                <div class="card shadow">
                    <div class="card-header bg-primary text-white">
                        <h3 class="mb-0">
                            <i class="fas fa-user-plus"></i> Créer un compte
                        </h3>
                    </div>
                    <div class="card-body p-4">
                        <form method="post" action="${pageContext.request.contextPath}/register" id="registerForm">

                            <!-- User Type Selection -->
                            <div class="mb-4">
                                <label class="form-label fw-bold">
                                    <i class="fas fa-id-badge"></i> Type de compte *
                                </label>
                                <div class="btn-group w-100" role="group">
                                    <input type="radio" class="btn-check" name="userType" id="typeParticipant"
                                           value="PARTICIPANT" ${empty param.userType or param.userType == 'PARTICIPANT' ? 'checked' : ''}>
                                    <label class="btn btn-outline-primary" for="typeParticipant">
                                        <i class="fas fa-user"></i> Participant
                                        <br><small>Découvrir et participer aux événements</small>
                                    </label>

                                    <input type="radio" class="btn-check" name="userType" id="typeOrganisateur"
                                           value="ORGANISATEUR" ${param.userType == 'ORGANISATEUR' ? 'checked' : ''}>
                                    <label class="btn btn-outline-primary" for="typeOrganisateur">
                                        <i class="fas fa-users-cog"></i> Organisateur
                                        <br><small>Créer et gérer des événements</small>
                                    </label>
                                </div>
                            </div>

                            <div class="row">
                                <!-- Common Fields -->
                                <div class="col-md-6 mb-3">
                                    <label for="nom" class="form-label">
                                        <i class="fas fa-user"></i> Nom complet *
                                    </label>
                                    <input type="text" class="form-control" id="nom" name="nom"
                                           value="<c:out value='${param.nom}'/>"
                                           required minlength="2" maxlength="100"
                                           placeholder="John Doe">
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label for="email" class="form-label">
                                        <i class="fas fa-envelope"></i> Email *
                                    </label>
                                    <input type="email" class="form-control" id="email" name="email"
                                           value="<c:out value='${param.email}'/>"
                                           required
                                           placeholder="email@example.com">
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label for="password" class="form-label">
                                        <i class="fas fa-lock"></i> Mot de passe *
                                    </label>
                                    <div class="input-group">
                                        <input type="password" class="form-control" id="password"
                                               name="password" required minlength="8"
                                               placeholder="••••••••">
                                        <button class="btn btn-outline-secondary" type="button" id="togglePassword">
                                            <i class="fas fa-eye"></i>
                                        </button>
                                    </div>
                                    <div class="progress mt-2" style="height: 5px;">
                                        <div class="progress-bar" id="passwordStrength" role="progressbar"></div>
                                    </div>
                                    <small class="text-muted">
                                        Min 8 caractères avec majuscule, chiffre et caractère spécial
                                    </small>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label for="confirmPassword" class="form-label">
                                        <i class="fas fa-lock"></i> Confirmer mot de passe *
                                    </label>
                                    <input type="password" class="form-control" id="confirmPassword"
                                           name="confirmPassword" required
                                           placeholder="••••••••">
                                    <div class="invalid-feedback">
                                        Les mots de passe ne correspondent pas
                                    </div>
                                </div>
                            </div>

                            <!-- Organizer-specific fields (hidden by default) -->
                            <div id="organisateurFields" style="display: ${param.userType == 'ORGANISATEUR' ? 'block' : 'none'};">
                                <div class="card bg-light mb-3">
                                    <div class="card-body">
                                        <h6 class="card-title text-primary">
                                            <i class="fas fa-building"></i> Informations Organisateur
                                        </h6>
                                        <div class="row">
                                            <div class="col-md-6 mb-3">
                                                <label for="organisation" class="form-label">Organisation *</label>
                                                <input type="text" class="form-control" id="organisation"
                                                       name="organisation"
                                                       value="<c:out value='${param.organisation}'/>"
                                                       placeholder="Nom de votre organisation">
                                            </div>

                                            <div class="col-md-6 mb-3">
                                                <label for="telephone" class="form-label">Téléphone</label>
                                                <input type="tel" class="form-control" id="telephone"
                                                       name="telephone"
                                                       value="<c:out value='${param.telephone}'/>"
                                                       placeholder="+212 6XX XXXXXX"
                                                       pattern="^(\+212|0)[5-7][0-9]{8}$">
                                                <small class="text-muted">Format: +212 6XX XXX XXX</small>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Participant-specific fields (shown by default) -->
                            <div id="participantFields" style="display: ${param.userType == 'ORGANISATEUR' ? 'none' : 'block'};">
                                <div class="mb-3">
                                    <label for="preferences" class="form-label">
                                        <i class="fas fa-heart"></i> Préférences (optionnel)
                                    </label>
                                    <textarea class="form-control" id="preferences" name="preferences"
                                              rows="2" maxlength="500"
                                              placeholder="Vos centres d'intérêt (sport, culture, technologie...)"
                                    ><c:out value='${param.preferences}'/></textarea>
                                    <small class="text-muted">Nous vous suggérerons des événements adaptés</small>
                                </div>
                            </div>


                                                        <!-- Terms and Conditions with Modal Popups -->
                                                        <div class="mb-3 form-check">
                                                                <input type="checkbox" class="form-check-input" id="acceptTerms" required>
                                                                <label class="form-check-label" for="acceptTerms">
                                                                        J'accepte les <a href="#" id="showLicense" data-bs-toggle="modal" data-bs-target="#licenseModal">conditions d'utilisation</a>
                                                                        et la <a href="#" id="showPrivacy" data-bs-toggle="modal" data-bs-target="#privacyModal">politique de confidentialité</a>
                                                                </label>
                                                        </div>

                                                        <!-- License Modal -->
                                                        <div class="modal fade" id="licenseModal" tabindex="-1" aria-labelledby="licenseModalLabel" aria-hidden="true">
                                                            <div class="modal-dialog modal-lg modal-dialog-centered">
                                                                <div class="modal-content">
                                                                    <div class="modal-header">
                                                                        <h5 class="modal-title" id="licenseModalLabel">Conditions d'utilisation</h5>
                                                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fermer"></button>
                                                                    </div>
                                                                    <div class="modal-body">
                                                                        <p>Bienvenue sur Event Management. En créant un compte, vous acceptez de respecter les règles suivantes :</p>
                                                                        <ul>
                                                                            <li>Utiliser la plateforme de manière légale et respectueuse.</li>
                                                                            <li>Ne pas publier de contenu inapproprié ou offensant.</li>
                                                                            <li>Respecter la vie privée des autres utilisateurs.</li>
                                                                            <li>Ne pas tenter de contourner la sécurité du site.</li>
                                                                            <li>Les organisateurs sont responsables des informations de leurs événements.</li>
                                                                        </ul>
                                                                        <p>Pour plus de détails, contactez l'administrateur.</p>
                                                                    </div>
                                                                    <div class="modal-footer">
                                                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fermer</button>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>

                                                        <!-- Privacy Modal -->
                                                        <div class="modal fade" id="privacyModal" tabindex="-1" aria-labelledby="privacyModalLabel" aria-hidden="true">
                                                            <div class="modal-dialog modal-lg modal-dialog-centered">
                                                                <div class="modal-content">
                                                                    <div class="modal-header">
                                                                        <h5 class="modal-title" id="privacyModalLabel">Politique de confidentialité</h5>
                                                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fermer"></button>
                                                                    </div>
                                                                    <div class="modal-body">
                                                                        <p>Nous attachons une grande importance à la confidentialité de vos données :</p>
                                                                        <ul>
                                                                            <li>Vos informations personnelles sont utilisées uniquement pour la gestion de votre compte et des événements.</li>
                                                                            <li>Nous ne partageons pas vos données avec des tiers sans votre consentement.</li>
                                                                            <li>Vous pouvez demander la suppression de votre compte à tout moment.</li>
                                                                            <li>Des mesures de sécurité sont mises en place pour protéger vos données.</li>
                                                                        </ul>
                                                                        <p>Pour toute question, contactez l'équipe support.</p>
                                                                    </div>
                                                                    <div class="modal-footer">
                                                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fermer</button>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>

                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary btn-lg">
                                    <i class="fas fa-user-plus"></i> Créer mon compte
                                </button>
                            </div>
                        </form>

                        <div class="text-center mt-4">
                            <p class="mb-0">
                                Vous avez déjà un compte?
                                <a href="${pageContext.request.contextPath}/login" class="fw-bold">
                                    Se connecter
                                </a>
                            </p>
                        </div>
                    </div>
                </div>

                <!-- Info Card -->
                <div class="card mt-3 border-info">
                    <div class="card-body">
                        <h6 class="card-title text-info">
                            <i class="fas fa-info-circle"></i> Pourquoi s'inscrire ?
                        </h6>
                        <ul class="mb-0 small">
                            <li>Découvrez des événements passionnants</li>
                            <li>Inscrivez-vous en quelques clics</li>
                            <li>Recevez des recommandations personnalisées</li>
                            <li>Gérez vos inscriptions facilement</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="../layout/footer.jsp"/>

    <!-- Bootstrap JS for modal support -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Toggle password visibility
        document.getElementById('togglePassword').addEventListener('click', function() {
            const password = document.getElementById('password');
            const icon = this.querySelector('i');
            if (password.type === 'password') {
                password.type = 'text';
                icon.classList.replace('fa-eye', 'fa-eye-slash');
            } else {
                password.type = 'password';
                icon.classList.replace('fa-eye-slash', 'fa-eye');
            }
        });

        // Show/hide fields based on user type
        document.querySelectorAll('input[name="userType"]').forEach(radio => {
            radio.addEventListener('change', function() {
                const organisateurFields = document.getElementById('organisateurFields');
                const participantFields = document.getElementById('participantFields');
                const organisation = document.getElementById('organisation');

                if (this.value === 'ORGANISATEUR') {
                    organisateurFields.style.display = 'block';
                    participantFields.style.display = 'none';
                    organisation.required = true;
                } else {
                    organisateurFields.style.display = 'none';
                    participantFields.style.display = 'block';
                    organisation.required = false;
                }
            });
        });

        // Password strength indicator
        document.getElementById('password').addEventListener('input', function() {
            const password = this.value;
            const strengthBar = document.getElementById('passwordStrength');
            let strength = 0;

            if (password.length >= 8) strength += 25;
            if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength += 25;
            if (/\d/.test(password)) strength += 25;
            if (/[^a-zA-Z0-9]/.test(password)) strength += 25;

            strengthBar.style.width = strength + '%';
            strengthBar.className = 'progress-bar ';
            if (strength <= 25) strengthBar.classList.add('bg-danger');
            else if (strength <= 50) strengthBar.classList.add('bg-warning');
            else if (strength <= 75) strengthBar.classList.add('bg-info');
            else strengthBar.classList.add('bg-success');
        });

        // Validate password match on submit
        document.getElementById('registerForm').addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const confirm = document.getElementById('confirmPassword').value;
            const confirmField = document.getElementById('confirmPassword');

            if (password !== confirm) {
                e.preventDefault();
                confirmField.classList.add('is-invalid');
                alert('Les mots de passe ne correspondent pas');
                return false;
            }

            // Validate password strength
            const strongRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$/;
            if (!strongRegex.test(password)) {
                e.preventDefault();
                alert('Le mot de passe doit contenir au moins 8 caractères avec majuscules, minuscules, chiffres et caractères spéciaux');
                return false;
            }

            confirmField.classList.remove('is-invalid');
        });

        // Clear validation on input
        document.getElementById('confirmPassword').addEventListener('input', function() {
            this.classList.remove('is-invalid');
        });
    </script>
</body>
</html>
