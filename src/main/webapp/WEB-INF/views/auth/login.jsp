<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Connexion" scope="request"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/messages.jsp"/>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-5 col-lg-4">
            <!-- Login Card -->
            <div class="card shadow-lg mt-5">
                <div class="card-body p-5">
                    <!-- Logo/Icon -->
                    <div class="text-center mb-4">
                        <i class="fas fa-calendar-alt fa-3x text-primary"></i>
                        <h3 class="mt-3 fw-bold">Connexion</h3>
                        <p class="text-muted">Accédez à votre compte</p>
                    </div>

                    <!-- Login Form -->
                    <form action="${pageContext.request.contextPath}/login" method="post" id="loginForm">
                        <!-- Email Field -->
                        <div class="mb-3">
                            <label for="email" class="form-label">
                                <i class="fas fa-envelope"></i> Adresse Email
                            </label>
                            <input type="email"
                                   class="form-control"
                                   id="email"
                                   name="email"
                                   value="${email}"
                                   placeholder="votre@email.com"
                                   required
                                   autofocus>
                            <div class="invalid-feedback">
                                Veuillez entrer une adresse email valide.
                            </div>
                        </div>

                        <!-- Password Field -->
                        <div class="mb-3">
                            <label for="password" class="form-label">
                                <i class="fas fa-lock"></i> Mot de passe
                            </label>
                            <div class="input-group">
                                <input type="password"
                                       class="form-control"
                                       id="password"
                                       name="password"
                                       placeholder="••••••••"
                                       required>
                                <button class="btn btn-outline-secondary"
                                        type="button"
                                        id="togglePassword"
                                        title="Afficher/Masquer le mot de passe">
                                    <i class="fas fa-eye"></i>
                                </button>
                            </div>
                            <div class="invalid-feedback">
                                Le mot de passe est obligatoire.
                            </div>
                        </div>

                        <!-- Remember Me -->
                        <div class="mb-3 form-check">
                            <input type="checkbox"
                                   class="form-check-input"
                                   id="rememberMe"
                                   name="rememberMe">
                            <label class="form-check-label" for="rememberMe">
                                Se souvenir de moi
                            </label>
                        </div>

                        <!-- Submit Button -->
                        <div class="d-grid mb-3">
                            <button type="submit" class="btn btn-primary btn-lg">
                                <i class="fas fa-sign-in-alt"></i> Se connecter
                            </button>
                        </div>

                        <!-- Forgot Password Link -->
                        <div class="text-center">
                            <a href="${pageContext.request.contextPath}/forgot-password" class="text-decoration-none small">
                                Mot de passe oublié ?
                            </a>
                        </div>
                    </form>
                </div>

                <!-- Register Link -->
                <div class="card-footer text-center bg-light">
                    <p class="mb-0">
                        Pas encore de compte ?
                        <a href="${pageContext.request.contextPath}/register" class="fw-bold text-decoration-none">
                            S'inscrire maintenant
                        </a>
                    </p>
                </div>
            </div>

            <!-- Info Card -->
            <div class="card mt-3 border-info">
                <div class="card-body">
                    <h6 class="card-title text-info">
                        <i class="fas fa-info-circle"></i> Bienvenue !
                    </h6>
                    <p class="card-text small mb-0">
                        Connectez-vous pour découvrir des événements passionnants,
                        vous inscrire et participer à la vie communautaire.
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Custom JavaScript -->
<script>
    $(document).ready(function() {
        // Toggle password visibility
        $('#togglePassword').on('click', function() {
            const passwordField = $('#password');
            const icon = $(this).find('i');

            if (passwordField.attr('type') === 'password') {
                passwordField.attr('type', 'text');
                icon.removeClass('fa-eye').addClass('fa-eye-slash');
            } else {
                passwordField.attr('type', 'password');
                icon.removeClass('fa-eye-slash').addClass('fa-eye');
            }
        });

        // Form validation
        $('#loginForm').on('submit', function(e) {
            let isValid = true;

            // Validate email
            const email = $('#email').val();
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                $('#email').addClass('is-invalid');
                isValid = false;
            } else {
                $('#email').removeClass('is-invalid').addClass('is-valid');
            }

            // Validate password
            const password = $('#password').val();
            if (password.length < 1) {
                $('#password').addClass('is-invalid');
                isValid = false;
            } else {
                $('#password').removeClass('is-invalid').addClass('is-valid');
            }

            if (!isValid) {
                e.preventDefault();
            }
        });

        // Clear validation on input
        $('#email, #password').on('input', function() {
            $(this).removeClass('is-invalid is-valid');
        });
    });
</script>

<jsp:include page="../layout/footer.jsp"/>
