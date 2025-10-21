<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Mon Profil" />
<jsp:include page="../layout/header.jsp" />
<jsp:include page="../layout/messages.jsp" />

<div class="container py-4">
    <div class="row">
        <div class="col-lg-10 mx-auto">
            <h2 class="mb-4">
                <i class="fas fa-user-cog"></i> Mon Profil
            </h2>

            <div class="row">
                <!-- Profile Image & Basic Info -->
                <div class="col-md-4">
                    <div class="card mb-4">
                        <div class="card-body text-center">
                            <c:choose>
                                <c:when test="${not empty organisateur.photoProfil}">
                                    <img id="profileImagePreview" src="${organisateur.photoProfil}" 
                                         class="rounded-circle mb-3" 
                                         style="width: 150px; height: 150px; object-fit: cover;">
                                </c:when>
                                <c:otherwise>
                                    <i id="profileImagePreview" class="fas fa-user-circle fa-10x text-muted mb-3"></i>
                                </c:otherwise>
                            </c:choose>
                            
                            <h4 class="mb-1"><c:out value="${organisateur.nom}"/></h4>
                            <p class="text-muted mb-2">
                                <c:out value="${organisateur.email}"/>
                            </p>
                            <span class="badge bg-primary">Organisateur</span>
                            
                            <hr>
                            
                            <div class="text-start">
                                <p class="mb-2">
                                    <i class="fas fa-calendar-check text-success"></i>
                                    <strong>${stats.totalEvents}</strong> événements créés
                                </p>
                                <p class="mb-2">
                                    <i class="fas fa-users text-info"></i>
                                    <strong>${stats.totalParticipants}</strong> participants
                                </p>
                                <p class="mb-0">
                                    <i class="fas fa-clock text-warning"></i>
                                    Membre depuis
                                    <c:choose>
                                        <c:when test="${not empty organisateur.dateInscription}">
                                            ${organisateur.dateInscription.month.toString().substring(0, 1)}${organisateur.dateInscription.month.toString().substring(1).toLowerCase()} ${organisateur.dateInscription.year}
                                        </c:when>
                                        <c:otherwise>
                                            N/A
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                        </div>
                    </div>

                    <!-- Change Password Card -->
                    <div class="card">
                        <div class="card-header bg-warning text-dark">
                            <h5 class="mb-0">
                                <i class="fas fa-lock"></i> Sécurité
                            </h5>
                        </div>
                        <div class="card-body">
                            <a href="${pageContext.request.contextPath}/profile/change-password" 
                               class="btn btn-outline-dark w-100">
                                <i class="fas fa-key"></i> Changer le mot de passe
                            </a>
                        </div>
                    </div>
                </div>

                <!-- Profile Form -->
                <div class="col-md-8">
                    <form action="${pageContext.request.contextPath}/profile/update" method="post" enctype="multipart/form-data">
                        <!-- Personal Information -->
                        <div class="card mb-4">
                            <div class="card-header bg-primary text-white">
                                <h5 class="mb-0">
                                    <i class="fas fa-user"></i> Informations personnelles
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="mb-3">
                                    <label for="photoProfil" class="form-label">
                                        Photo de profil
                                    </label>
                                    <input type="file" class="form-control" id="photoProfil" name="photoProfil" 
                                           accept="image/*" onchange="previewProfileImage(this)">
                                    <small class="text-muted">JPG, PNG - Max 2MB</small>
                                </div>

                                <div class="mb-3">
                                    <label for="nom" class="form-label">
                                        Nom complet *
                                    </label>
                                    <input type="text" class="form-control ${not empty errors.nom ? 'is-invalid' : ''}" 
                                           id="nom" name="nom" value="${organisateur.nom}" 
                                           required maxlength="100">
                                    <c:if test="${not empty errors.nom}">
                                        <div class="invalid-feedback">${errors.nom}</div>
                                    </c:if>
                                </div>

                                <div class="mb-3">
                                    <label for="email" class="form-label">
                                        Email *
                                    </label>
                                    <input type="email" class="form-control ${not empty errors.email ? 'is-invalid' : ''}" 
                                           id="email" name="email" value="${organisateur.email}" 
                                           required>
                                    <c:if test="${not empty errors.email}">
                                        <div class="invalid-feedback">${errors.email}</div>
                                    </c:if>
                                </div>
                            </div>
                        </div>

                        <!-- Organization Information -->
                        <div class="card mb-4">
                            <div class="card-header bg-success text-white">
                                <h5 class="mb-0">
                                    <i class="fas fa-building"></i> Informations organisateur
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="mb-3">
                                    <label for="organisation" class="form-label">
                                        Organisation / Entreprise *
                                    </label>
                                    <input type="text" class="form-control ${not empty errors.organisation ? 'is-invalid' : ''}" 
                                           id="organisation" name="organisation" value="${organisateur.organisation}" 
                                           required maxlength="150">
                                    <c:if test="${not empty errors.organisation}">
                                        <div class="invalid-feedback">${errors.organisation}</div>
                                    </c:if>
                                </div>

                                <div class="mb-3">
                                    <label for="telephone" class="form-label">
                                        Téléphone
                                    </label>
                                    <input type="tel" class="form-control ${not empty errors.telephone ? 'is-invalid' : ''}" 
                                           id="telephone" name="telephone" value="${organisateur.telephone}" 
                                           placeholder="+212 6XX XXXXXX ou 06XX XXXXXX">
                                    <small class="text-muted">Format recommandé: +212 6XX XXXXXX ou 06XX XXXXXX</small>
                                    <c:if test="${not empty errors.telephone}">
                                        <div class="invalid-feedback">${errors.telephone}</div>
                                    </c:if>
                                </div>

                                <div class="mb-3">
                                    <label for="siteWeb" class="form-label">
                                        Site web
                                    </label>
                                    <input type="url" class="form-control ${not empty errors.siteWeb ? 'is-invalid' : ''}" 
                                           id="siteWeb" name="siteWeb" value="${organisateur.siteWeb}" 
                                           placeholder="https://example.com">
                                    <c:if test="${not empty errors.siteWeb}">
                                        <div class="invalid-feedback">${errors.siteWeb}</div>
                                    </c:if>
                                </div>

                                <div class="mb-3">
                                    <label for="adresse" class="form-label">
                                        Adresse
                                    </label>
                                    <input type="text" class="form-control ${not empty errors.adresse ? 'is-invalid' : ''}" 
                                           id="adresse" name="adresse" value="${organisateur.adresse}" 
                                           maxlength="255">
                                    <c:if test="${not empty errors.adresse}">
                                        <div class="invalid-feedback">${errors.adresse}</div>
                                    </c:if>
                                </div>

                                <div class="mb-3">
                                    <label for="description" class="form-label">
                                        Description / Présentation
                                    </label>
                                    <textarea class="form-control ${not empty errors.description ? 'is-invalid' : ''}" 
                                              id="description" name="description" rows="4" 
                                              maxlength="1000"
                                              placeholder="Présentez votre organisation...">${organisateur.description}</textarea>
                                    <div class="form-text">
                                        <span id="charCount">0</span>/1000 caractères
                                    </div>
                                    <c:if test="${not empty errors.description}">
                                        <div class="invalid-feedback">${errors.description}</div>
                                    </c:if>
                                </div>
                            </div>
                        </div>

                        <!-- Actions -->
                        <div class="card">
                            <div class="card-body">
                                <div class="d-flex justify-content-between">
                                    <button type="button" class="btn btn-outline-secondary" onclick="resetForm()">
                                        <i class="fas fa-undo"></i> Réinitialiser
                                    </button>
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-save"></i> Enregistrer les modifications
                                    </button>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
$(document).ready(function() {
    // Character counter
    const descField = $('#description');
    const charCount = $('#charCount');
    
    function updateCharCount() {
        charCount.text(descField.val().length);
    }
    
    descField.on('input', updateCharCount);
    updateCharCount();
});

function resetForm() {
    if (confirm('Annuler toutes les modifications ?')) {
        location.reload();
    }
}

function previewProfileImage(input) {
    const preview = $('#profileImagePreview');
    
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        
        reader.onload = function(e) {
            if (preview.is('i')) {
                preview.replaceWith('<img id="profileImagePreview" src="' + e.target.result + 
                    '" class="rounded-circle mb-3" style="width: 150px; height: 150px; object-fit: cover;">');
            } else {
                preview.attr('src', e.target.result);
            }
        };
        
        reader.readAsDataURL(input.files[0]);
    }
}
</script>

<jsp:include page="../layout/footer.jsp" />
