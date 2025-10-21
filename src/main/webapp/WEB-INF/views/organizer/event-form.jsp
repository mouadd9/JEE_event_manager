<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="isEdit" value="${not empty evenement.id}" />
<c:choose>
    <c:when test="${isEdit}">
        <c:set var="pageTitle" value="Modifier l'événement" />
    </c:when>
    <c:otherwise>
        <c:set var="pageTitle" value="Créer un événement" />
    </c:otherwise>
</c:choose>
<jsp:include page="../layout/header.jsp" />
<jsp:include page="../layout/messages.jsp" />

<div class="container py-4">
    <div class="row">
        <div class="col-lg-10 mx-auto">
            <!-- Header -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2>
                    <c:choose>
                        <c:when test="${isEdit}">
                            <i class="fas fa-edit"></i>
                            Modifier l'événement
                        </c:when>
                        <c:otherwise>
                            <i class="fas fa-plus-circle"></i>
                            Créer un événement
                        </c:otherwise>
                    </c:choose>
                </h2>
                <a href="${pageContext.request.contextPath}/events/manage" class="btn btn-outline-secondary">
                    <i class="fas fa-arrow-left"></i> Retour
                </a>
            </div>

            <!-- Form -->
            <c:choose>
                <c:when test="${isEdit}">
                    <c:set var="formAction" value="edit" />
                </c:when>
                <c:otherwise>
                    <c:set var="formAction" value="create" />
                </c:otherwise>
            </c:choose>
            <form action="${pageContext.request.contextPath}/events/${formAction}" 
                  method="post" id="eventForm" enctype="multipart/form-data">
                
                <c:if test="${isEdit}">
                    <input type="hidden" name="id" value="${evenement.id}">
                </c:if>

                <!-- Basic Information -->
                <div class="card mb-4">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-info-circle"></i> Informations de base
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-8">
                                <!-- Title -->
                                <div class="mb-3">
                                    <label for="titre" class="form-label">
                                        Titre de l'événement *
                                    </label>
                                    <input type="text" class="form-control ${not empty errors.titre ? 'is-invalid' : ''}" 
                                           id="titre" name="titre" value="${evenement.titre}" 
                                           required maxlength="200" placeholder="Ex: Conférence Tech 2025">
                                    <c:if test="${not empty errors.titre}">
                                        <div class="invalid-feedback">${errors.titre}</div>
                                    </c:if>
                                </div>

                                <!-- Categories -->
                                <div class="mb-3">
                                    <label class="form-label">
                                        Catégories *
                                    </label>
                                    <div class="row">
                                        <c:forEach var="categorie" items="${categories}">
                                            <div class="col-md-6 mb-2">
                                                <div class="form-check">
                                                    <input class="form-check-input" type="checkbox" 
                                                           name="categoriesIds" value="${categorie.id}" 
                                                           id="cat${categorie.id}"
                                                           <c:if test="${fn:contains(evenement.categories, categorie)}">checked</c:if>>
                                                    <label class="form-check-label" for="cat${categorie.id}">
                                                        <i class="fas ${categorie.icone} me-1" style="color: ${categorie.couleur}"></i>
                                                        <c:out value="${categorie.nom}"/>
                                                    </label>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                    <c:if test="${not empty errors.categories}">
                                        <div class="text-danger small">${errors.categories}</div>
                                    </c:if>
                                </div>
                            </div>

                            <div class="col-md-4">
                                <!-- Image Upload -->
                                <div class="mb-3">
                                    <label for="image" class="form-label">
                                        Image de l'événement
                                    </label>
                                    <div class="text-center mb-2">
                                        <c:choose>
                                            <c:when test="${not empty evenement.imageUrl}">
                                                <img id="imagePreview" src="${evenement.imageUrl}" 
                                                     class="img-fluid rounded" style="max-height: 200px;">
                                            </c:when>
                                            <c:otherwise>
                                                <div id="imagePreview" class="bg-light rounded p-4">
                                                    <i class="fas fa-image fa-4x text-muted"></i>
                                                    <p class="text-muted mt-2 mb-0">Aucune image</p>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <input type="file" class="form-control" id="image" name="image" 
                                           accept="image/*" onchange="previewImage(this)">
                                    <small class="text-muted">JPG, PNG ou GIF - Max 5MB</small>
                                </div>
                            </div>
                        </div>

                        <!-- Description -->
                        <div class="mb-3">
                            <label for="description" class="form-label">
                                Description *
                            </label>
                            <textarea class="form-control ${not empty errors.description ? 'is-invalid' : ''}" 
                                      id="description" name="description" rows="5" 
                                      required maxlength="2000" 
                                      placeholder="Décrivez votre événement en détail...">${evenement.description}</textarea>
                            <div class="form-text">
                                <span id="charCount">0</span>/2000 caractères
                            </div>
                            <c:if test="${not empty errors.description}">
                                <div class="invalid-feedback">${errors.description}</div>
                            </c:if>
                        </div>
                    </div>
                </div>

                <!-- Date & Time -->
                <div class="card mb-4">
                    <div class="card-header bg-success text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-calendar-alt"></i> Date et heure
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="dateDebut" class="form-label">
                                        Date et heure de début *
                                    </label>
                                    <c:if test="${not empty evenement.dateDebut}">
                                        <c:set var="dateDebutFormatted" value="${evenement.dateDebut.year}-${evenement.dateDebut.monthValue < 10 ? '0' : ''}${evenement.dateDebut.monthValue}-${evenement.dateDebut.dayOfMonth < 10 ? '0' : ''}${evenement.dateDebut.dayOfMonth}T${evenement.dateDebut.hour < 10 ? '0' : ''}${evenement.dateDebut.hour}:${evenement.dateDebut.minute < 10 ? '0' : ''}${evenement.dateDebut.minute}" />
                                    </c:if>
                                    <input type="datetime-local" 
                                           class="form-control ${not empty errors.dateDebut ? 'is-invalid' : ''}" 
                                           id="dateDebut" name="dateDebut" 
                                           value="${dateDebutFormatted}" 
                                           required>
                                    <c:if test="${not empty errors.dateDebut}">
                                        <div class="invalid-feedback">${errors.dateDebut}</div>
                                    </c:if>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="dateFin" class="form-label">
                                        Date et heure de fin *
                                    </label>
                                    <c:if test="${not empty evenement.dateFin}">
                                        <c:set var="dateFinFormatted" value="${evenement.dateFin.year}-${evenement.dateFin.monthValue < 10 ? '0' : ''}${evenement.dateFin.monthValue}-${evenement.dateFin.dayOfMonth < 10 ? '0' : ''}${evenement.dateFin.dayOfMonth}T${evenement.dateFin.hour < 10 ? '0' : ''}${evenement.dateFin.hour}:${evenement.dateFin.minute < 10 ? '0' : ''}${evenement.dateFin.minute}" />
                                    </c:if>
                                    <input type="datetime-local" 
                                           class="form-control ${not empty errors.dateFin ? 'is-invalid' : ''}" 
                                           id="dateFin" name="dateFin" 
                                           value="${dateFinFormatted}" 
                                           required>
                                    <c:if test="${not empty errors.dateFin}">
                                        <div class="invalid-feedback">${errors.dateFin}</div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Location -->
                <div class="card mb-4">
                    <div class="card-header bg-info text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-map-marker-alt"></i> Lieu
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="lieu" class="form-label">
                                        Lieu *
                                    </label>
                                    <input type="text" class="form-control ${not empty errors.lieu ? 'is-invalid' : ''}" 
                                           id="lieu" name="lieu" value="${evenement.lieu}" 
                                           required maxlength="200" 
                                           placeholder="Ex: Centre culturel">
                                    <c:if test="${not empty errors.lieu}">
                                        <div class="invalid-feedback">${errors.lieu}</div>
                                    </c:if>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="adresseComplete" class="form-label">
                                        Adresse complète *
                                    </label>
                                    <input type="text" class="form-control ${not empty errors.adresseComplete ? 'is-invalid' : ''}" 
                                           id="adresseComplete" name="adresseComplete" value="${evenement.adresseComplete}" 
                                           required maxlength="500" 
                                           placeholder="123 Rue Principale, Tétouan">
                                    <c:if test="${not empty errors.adresseComplete}">
                                        <div class="invalid-feedback">${errors.adresseComplete}</div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="latitude" class="form-label">
                                        Latitude (optionnel)
                                    </label>
                                    <input type="number" class="form-control" id="latitude" name="latitude" 
                                           value="${evenement.latitude}" step="0.000001" 
                                           min="-90" max="90" placeholder="Ex: 35.5889">
                                    <small class="text-muted">Pour la carte interactive</small>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label for="longitude" class="form-label">
                                        Longitude (optionnel)
                                    </label>
                                    <input type="number" class="form-control" id="longitude" name="longitude" 
                                           value="${evenement.longitude}" step="0.000001" 
                                           min="-180" max="180" placeholder="Ex: -5.3626">
                                    <small class="text-muted">Pour la carte interactive</small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Capacity & Pricing -->
                <div class="card mb-4">
                    <div class="card-header bg-warning text-dark">
                        <h5 class="mb-0">
                            <i class="fas fa-users"></i> Capacité et tarification
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-4">
                                <div class="mb-3">
                                    <label for="capacite" class="form-label">
                                        Capacité maximale *
                                    </label>
                                    <input type="number" class="form-control ${not empty errors.capacite ? 'is-invalid' : ''}" 
                                           id="capacite" name="capacite" value="${evenement.capacite}" 
                                           required min="1" max="100000" placeholder="100">
                                    <c:if test="${not empty errors.capacite}">
                                        <div class="invalid-feedback">${errors.capacite}</div>
                                    </c:if>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="mb-3">
                                    <label class="form-label">
                                        Type d'événement *
                                    </label>
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="gratuit" 
                                               id="gratuitTrue" value="true" 
                                               ${evenement.gratuit or empty evenement ? 'checked' : ''} 
                                               onchange="togglePrix()">
                                        <label class="form-check-label" for="gratuitTrue">
                                            <i class="fas fa-gift text-success"></i> Gratuit
                                        </label>
                                    </div>
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="gratuit" 
                                               id="gratuitFalse" value="false" 
                                               ${not evenement.gratuit and not empty evenement ? 'checked' : ''} 
                                               onchange="togglePrix()">
                                        <label class="form-check-label" for="gratuitFalse">
                                            <i class="fas fa-ticket-alt text-primary"></i> Payant
                                        </label>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <c:choose>
                                    <c:when test="${evenement.gratuit}">
                                        <c:set var="prixDisplay" value="none" />
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="prixDisplay" value="block" />
                                    </c:otherwise>
                                </c:choose>
                                <div class="mb-3" id="prixContainer" style="display: ${prixDisplay};">
                                    <label for="prix" class="form-label">
                                        Prix (MAD) *
                                    </label>
                                    <input type="number" class="form-control ${not empty errors.prix ? 'is-invalid' : ''}" 
                                           id="prix" name="prix" value="${evenement.prix}" 
                                           min="0" step="0.01" placeholder="0.00">
                                    <c:if test="${not empty errors.prix}">
                                        <div class="invalid-feedback">${errors.prix}</div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Actions -->
                <div class="card">
                    <div class="card-body">
                        <div class="d-flex justify-content-between">
                            <a href="${pageContext.request.contextPath}/events/manage" class="btn btn-outline-secondary">
                                <i class="fas fa-times"></i> Annuler
                            </a>
                            <div>
                                <button type="submit" name="action" value="save" class="btn btn-primary">
                                    <i class="fas fa-save"></i>
                                    <c:choose>
                                        <c:when test="${isEdit}">Mettre à jour</c:when>
                                        <c:otherwise>Créer l'événement</c:otherwise>
                                    </c:choose>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
$(document).ready(function() {
    // Character counter for description
    const descField = $('#description');
    const charCount = $('#charCount');
    
    function updateCharCount() {
        charCount.text(descField.val().length);
    }
    
    descField.on('input', updateCharCount);
    updateCharCount();

    // Form validation
    $('#eventForm').on('submit', function(e) {
        const dateDebut = new Date($('#dateDebut').val());
        const dateFin = new Date($('#dateFin').val());
        const now = new Date();

        if (dateDebut < now) {
            alert('La date de début doit être dans le futur');
            e.preventDefault();
            return false;
        }

        if (dateFin <= dateDebut) {
            alert('La date de fin doit être après la date de début');
            e.preventDefault();
            return false;
        }

        // Check at least one category selected
        if ($('input[name="categoriesIds"]:checked').length === 0) {
            alert('Veuillez sélectionner au moins une catégorie');
            e.preventDefault();
            return false;
        }
    });
});

function togglePrix() {
    const isGratuit = $('#gratuitTrue').is(':checked');
    const prixContainer = $('#prixContainer');
    const prixInput = $('#prix');
    
    if (isGratuit) {
        prixContainer.hide();
        prixInput.removeAttr('required');
        prixInput.val('0');
    } else {
        prixContainer.show();
        prixInput.attr('required', true);
    }
}

function previewImage(input) {
    const preview = $('#imagePreview');
    
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        
        reader.onload = function(e) {
            preview.html('<img src="' + e.target.result + '" class="img-fluid rounded" style="max-height: 200px;">');
        };
        
        reader.readAsDataURL(input.files[0]);
    }
}
</script>

<jsp:include page="../layout/footer.jsp" />
