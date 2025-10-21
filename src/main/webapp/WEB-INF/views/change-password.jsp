<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Changer le mot de passe" />
<jsp:include page="layout/header.jsp" />
<jsp:include page="layout/messages.jsp" />

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-6 col-md-8">
            <div class="card shadow">
                <div class="card-header bg-warning text-dark">
                    <h4 class="mb-0">
                        <i class="fas fa-lock"></i> Changer le mot de passe
                    </h4>
                </div>
                <div class="card-body p-4">
                    <p class="text-muted mb-4">
                        Pour votre sécurité, veuillez choisir un mot de passe fort contenant au moins 6 caractères.
                    </p>

                    <form action="${pageContext.request.contextPath}/profile/change-password" method="post">
                        <!-- Current Password -->
                        <div class="mb-3">
                            <label for="currentPassword" class="form-label">
                                <i class="fas fa-key"></i> Mot de passe actuel *
                            </label>
                            <input type="password" 
                                   class="form-control ${not empty errors.currentPassword ? 'is-invalid' : ''}" 
                                   id="currentPassword" 
                                   name="currentPassword" 
                                   required 
                                   autocomplete="current-password">
                            <c:if test="${not empty errors.currentPassword}">
                                <div class="invalid-feedback">${errors.currentPassword}</div>
                            </c:if>
                        </div>

                        <!-- New Password -->
                        <div class="mb-3">
                            <label for="newPassword" class="form-label">
                                <i class="fas fa-lock"></i> Nouveau mot de passe *
                            </label>
                            <div class="input-group">
                                <input type="password" 
                                       class="form-control ${not empty errors.newPassword ? 'is-invalid' : ''}" 
                                       id="newPassword" 
                                       name="newPassword" 
                                       required 
                                       minlength="6"
                                       autocomplete="new-password">
                                <button class="btn btn-outline-secondary" 
                                        type="button" 
                                        onclick="togglePasswordVisibility('newPassword', this)">
                                    <i class="fas fa-eye"></i>
                                </button>
                                <c:if test="${not empty errors.newPassword}">
                                    <div class="invalid-feedback">${errors.newPassword}</div>
                                </c:if>
                            </div>
                            <small class="text-muted">Minimum 6 caractères</small>
                        </div>

                        <!-- Confirm Password -->
                        <div class="mb-4">
                            <label for="confirmPassword" class="form-label">
                                <i class="fas fa-check-circle"></i> Confirmer le nouveau mot de passe *
                            </label>
                            <div class="input-group">
                                <input type="password" 
                                       class="form-control ${not empty errors.confirmPassword ? 'is-invalid' : ''}" 
                                       id="confirmPassword" 
                                       name="confirmPassword" 
                                       required 
                                       minlength="6"
                                       autocomplete="new-password">
                                <button class="btn btn-outline-secondary" 
                                        type="button" 
                                        onclick="togglePasswordVisibility('confirmPassword', this)">
                                    <i class="fas fa-eye"></i>
                                </button>
                                <c:if test="${not empty errors.confirmPassword}">
                                    <div class="invalid-feedback">${errors.confirmPassword}</div>
                                </c:if>
                            </div>
                        </div>

                        <!-- Password Strength Indicator -->
                        <div class="mb-4">
                            <div class="progress" style="height: 5px;">
                                <div id="passwordStrength" 
                                     class="progress-bar" 
                                     role="progressbar" 
                                     style="width: 0%"></div>
                            </div>
                            <small id="strengthText" class="text-muted"></small>
                        </div>

                        <!-- Buttons -->
                        <div class="d-flex justify-content-between">
                            <a href="${pageContext.request.contextPath}/profile" 
                               class="btn btn-outline-secondary">
                                <i class="fas fa-arrow-left"></i> Annuler
                            </a>
                            <button type="submit" class="btn btn-warning">
                                <i class="fas fa-save"></i> Changer le mot de passe
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Security Tips -->
            <div class="card mt-4 border-info">
                <div class="card-body">
                    <h6 class="card-title text-info">
                        <i class="fas fa-shield-alt"></i> Conseils de sécurité
                    </h6>
                    <ul class="mb-0 small">
                        <li>Utilisez au moins 8 caractères</li>
                        <li>Mélangez majuscules, minuscules, chiffres et symboles</li>
                        <li>Évitez les mots du dictionnaire</li>
                        <li>N'utilisez pas d'informations personnelles</li>
                        <li>Ne réutilisez pas de mots de passe d'autres sites</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
// Toggle password visibility
function togglePasswordVisibility(fieldId, button) {
    const field = document.getElementById(fieldId);
    const icon = button.querySelector('i');
    
    if (field.type === 'password') {
        field.type = 'text';
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
    } else {
        field.type = 'password';
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
    }
}

// Password strength checker
$(document).ready(function() {
    const newPasswordField = $('#newPassword');
    const confirmPasswordField = $('#confirmPassword');
    const strengthBar = $('#passwordStrength');
    const strengthText = $('#strengthText');
    
    newPasswordField.on('input', function() {
        const password = $(this).val();
        const strength = calculatePasswordStrength(password);
        
        updateStrengthBar(strength);
    });
    
    // Match validation
    confirmPasswordField.on('input', function() {
        const newPassword = newPasswordField.val();
        const confirmPassword = $(this).val();
        
        if (confirmPassword.length > 0) {
            if (newPassword === confirmPassword) {
                $(this).removeClass('is-invalid').addClass('is-valid');
            } else {
                $(this).removeClass('is-valid').addClass('is-invalid');
            }
        }
    });
    
    function calculatePasswordStrength(password) {
        let strength = 0;
        
        if (password.length >= 6) strength += 20;
        if (password.length >= 8) strength += 20;
        if (password.length >= 12) strength += 10;
        if (/[a-z]/.test(password)) strength += 15;
        if (/[A-Z]/.test(password)) strength += 15;
        if (/[0-9]/.test(password)) strength += 10;
        if (/[^a-zA-Z0-9]/.test(password)) strength += 10;
        
        return Math.min(strength, 100);
    }
    
    function updateStrengthBar(strength) {
        strengthBar.css('width', strength + '%');
        
        if (strength < 30) {
            strengthBar.removeClass().addClass('progress-bar bg-danger');
            strengthText.text('Faible').css('color', '#dc3545');
        } else if (strength < 60) {
            strengthBar.removeClass().addClass('progress-bar bg-warning');
            strengthText.text('Moyen').css('color', '#ffc107');
        } else if (strength < 80) {
            strengthBar.removeClass().addClass('progress-bar bg-info');
            strengthText.text('Bon').css('color', '#0dcaf0');
        } else {
            strengthBar.removeClass().addClass('progress-bar bg-success');
            strengthText.text('Excellent').css('color', '#198754');
        }
    }
});
</script>

<jsp:include page="layout/footer.jsp" />
