<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Créer un Nouvel Événement - EventHub</title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <!-- Leaflet CSS -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.css" crossorigin=""/>
    <%@ include file="/WEB-INF/jspf/theme-head.jspf" %>
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/organizer-theme.css">
    
    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f8f9fa;
            color: #1f2937;
        }
        
        .form-container {
            background: white;
            border-radius: 1rem;
            padding: 2rem;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            margin-top: 2rem;
        }
        
        .form-label {
            font-weight: 600;
            color: #374151;
            margin-bottom: 0.5rem;
        }
        
        .form-control, .form-select {
            border: 1px solid #d1d5db;
            border-radius: 0.5rem;
            padding: 0.75rem;
            font-size: 0.875rem;
            transition: all 0.2s;
        }
        
        .form-control:focus, .form-select:focus {
            border-color: #a855f7;
            box-shadow: 0 0 0 3px rgba(168, 85, 247, 0.1);
        }
        
        #map-select {
            height: 400px;
            width: 100%;
            border-radius: 0.5rem;
            border: 1px solid #d1d5db;
        }
        
        .image-preview {
            max-width: 200px;
            max-height: 200px;
            border-radius: 0.5rem;
            margin-top: 0.5rem;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #a855f7 0%, #9333ea 100%);
            border: none;
            border-radius: 0.5rem;
            padding: 0.75rem 1.5rem;
            font-weight: 600;
            transition: all 0.2s;
        }
        
        .btn-primary:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 8px rgba(168, 85, 247, 0.3);
        }
    </style>
</head>
<body>
    <!-- Header -->
    <nav class="navbar navbar-expand-lg organizer-navbar sticky-top shadow-sm">
        <div class="container-fluid">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/organizer/dashboard">
                <i class="bi bi-calendar-event"></i>EventHub Organisateur
            </a>
            
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto align-items-center">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/organizer/dashboard">
                            <i class="bi bi-speedometer2 me-1"></i>Tableau de Bord
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/organizer/events/new">
                            <i class="bi bi-plus-circle me-1"></i>Nouvel Événement
                        </a>
                    </li>
                    <li class="nav-item dropdown ms-3">
                        <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" id="userDropdown" 
                           role="button" data-bs-toggle="dropdown">
                            <div class="user-avatar me-2">
                                <i class="bi bi-person-fill"></i>
                            </div>
                            <span><c:out value="${organizer.nom}"/></span>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li><a class="dropdown-item" href="#">
                                <i class="bi bi-person me-2"></i>Mon Profil
                            </a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">
                                <i class="bi bi-box-arrow-right me-2"></i>Déconnexion
                            </a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container py-4">
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="form-container">
                    <div class="d-flex align-items-center mb-4">
                        <i class="bi bi-plus-circle-fill text-primary me-2 fs-4"></i>
                        <h1 class="h3 mb-0">Créer un Nouvel Événement</h1>
                    </div>

                    <form action="${pageContext.request.contextPath}/organizer/events" method="POST" enctype="multipart/form-data">
                        <input type="hidden" name="action" value="create">

                        <input type="hidden" id="latitude" name="latitude" value="">
                        <input type="hidden" id="longitude" name="longitude" value="">

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="titre" class="form-label">Titre de l'événement</label>
                                <input type="text" class="form-control" id="titre" name="titre" required 
                                       minlength="5" maxlength="100"
                                       placeholder="Ex: Conférence sur l'IA (5-100 caractères)">
                                <div class="form-text">Le titre doit contenir entre 5 et 100 caractères</div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="capacite" class="form-label">Capacité</label>
                                <input type="number" class="form-control" id="capacite" name="capacite" 
                                       min="1" value="100" placeholder="Nombre de places">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="lieu" class="form-label">Nom du lieu</label>
                            <input type="text" class="form-control" id="lieu" name="lieu" required
                                   placeholder="Ex: Palais des Congrès, Salle de conférence">
                        </div>

                        <div class="mb-3">
                            <label for="map-select" class="form-label">Localisation (Cliquez sur la carte)</label>
                            <div id="map-select"></div>
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="dateDebutInput" class="form-label">Date de début</label>
                                <input type="datetime-local" class="form-control" id="dateDebutInput" name="dateDebut" required>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="dateFinInput" class="form-label">Date de fin</label>
                                <input type="datetime-local" class="form-control" id="dateFinInput" name="dateFin" required>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="description" class="form-label">Description</label>
                            <textarea class="form-control" id="description" name="description" rows="4" 
                                      maxlength="1000" placeholder="Décrivez votre événement..."></textarea>
                            <div class="form-text">Maximum 1000 caractères</div>
                        </div>

                        <div class="mb-3">
                            <label for="eventImage" class="form-label">Image de l'événement</label>
                            <input type="file" class="form-control" id="eventImage" name="eventImage" 
                                   accept="image/jpeg,image/png,image/webp">
                            <div class="form-text">Formats acceptés: JPG, PNG, WEBP (max 5MB)</div>
                            <div id="imagePreview" class="mt-2"></div>
                        </div>

                        <div class="d-flex justify-content-between">
                            <a href="${pageContext.request.contextPath}/organizer/dashboard" class="btn btn-outline-secondary">
                                <i class="bi bi-arrow-left me-1"></i>Annuler
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-check-circle me-1"></i>Créer l'événement
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Leaflet JS -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.js"></script>

    <script>
        // No formatForBackend function needed

        window.addEventListener('load', function() {
            console.log('Window loaded, initializing...');

            // Get all form elements
            const form = document.querySelector('form');
            const imageInput = document.getElementById('eventImage');
            const imagePreview = document.getElementById('imagePreview');
            // 'dateDebut' and 'dateFin' (hidden inputs) are removed
            const dateDebutInput = document.getElementById('dateDebutInput'); // Used for validation
            const dateFinInput = document.getElementById('dateFinInput'); // Used for validation
            const latInput = document.getElementById('latitude');
            const lonInput = document.getElementById('longitude');

            // --- Image Preview ---
            imageInput.addEventListener('change', function(e) {
                const file = e.target.files[0];
                if (file) {
                    if (file.size > 5 * 1024 * 1024) {
                        alert('Le fichier est trop volumineux. Taille maximale: 5MB');
                        this.value = '';
                        return;
                    }

                    const allowedTypes = ['image/jpeg', 'image/png', 'image/webp'];
                    if (!allowedTypes.includes(file.type)) {
                        alert('Format de fichier non supporté. Utilisez JPG, PNG ou WEBP.');
                        this.value = '';
                        return;
                    }

                    const reader = new FileReader();
                    reader.onload = function(e) {
                        imagePreview.innerHTML = `
                    <img src="${e.target.result}" class="image-preview" alt="Aperçu">
                    <div class="text-muted small mt-1">Aperçu de l'image</div>
                `;
                    };
                    reader.readAsDataURL(file);
                } else {
                    imagePreview.innerHTML = '';
                }
            });

            // --- Date Time Inputs (Block Removed) ---
            // No longer needed as 'name' attribute is on the visible inputs

            // --- Map (Leaflet) ---
            const startLat = 35.5785;
            const startLon = -5.3684;
            const startZoom = 12;

            if (typeof L === 'undefined') {
                console.error('Leaflet is not loaded!');
                return;
            }

            try {
                const map = L.map('map-select').setView([startLat, startLon], startZoom);

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: '© OpenStreetMap'
                }).addTo(map);

                let marker = null;

                map.on('click', function(e) {
                    if (marker) {
                        marker.setLatLng(e.latlng);
                    } else {
                        marker = L.marker(e.latlng).addTo(map);
                    }

                    latInput.value = e.latlng.lat;
                    lonInput.value = e.latlng.lng;
                    console.log('Location selected:', e.latlng.lat, e.latlng.lng);
                });

                setTimeout(() => map.invalidateSize(), 100);

            } catch (error) {
                console.error('Error initializing map:', error);
            }

            // --- Form Submission Handler ---
            form.addEventListener('submit', function(e) {
                console.log('=== FORM SUBMISSION STARTED ===');

                // Get form values
                const titre = document.getElementById('titre').value.trim();
                const lieu = document.getElementById('lieu').value.trim();
                const description = document.getElementById('description').value.trim();
                const startDate = dateDebutInput.value;
                const endDate = dateFinInput.value;

                // Validate title (5-100 characters)
                if (!titre || titre.length < 5 || titre.length > 100) {
                    e.preventDefault();
                    alert('Le titre doit contenir entre 5 et 100 caractères.');
                    document.getElementById('titre').focus();
                    return false;
                }

                // Validate location name (required)
                if (!lieu || lieu.trim() === '') {
                    e.preventDefault();
                    alert('Le nom du lieu est obligatoire.');
                    document.getElementById('lieu').focus();
                    return false;
                }

                // Validate description (max 1000 characters)
                if (description.length > 1000) {
                    e.preventDefault();
                    alert('La description ne peut pas dépasser 1000 caractères.');
                    document.getElementById('description').focus();
                    return false;
                }

                // Validate dates
                if (!startDate || startDate.trim() === '') {
                    e.preventDefault();
                    alert('Veuillez sélectionner la date de début.');
                    dateDebutInput.focus();
                    return false;
                }

                if (!endDate || endDate.trim() === '') {
                    e.preventDefault();
                    alert('Veuillez sélectionner la date de fin.');
                    dateFinInput.focus();
                    return false;
                }

                // Validate location coordinates
                if (!latInput.value || !lonInput.value) {
                    e.preventDefault();
                    alert('Veuillez sélectionner un lieu sur la carte.');
                    return false;
                }

                console.log('Submitting fields:', {
                    titre: titre,
                    lieu: lieu,
                    description: description,
                    dateDebut: startDate,
                    dateFin: endDate,
                    latitude: latInput.value,
                    longitude: lonInput.value
                });
            });
        });
    </script>

</body>
</html>