<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Modifier l'Événement - EventHub</title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <!-- Leaflet CSS -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.css" crossorigin=""/>
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/organizer/events/new">
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
                        <i class="bi bi-pencil-square text-primary me-2 fs-4"></i>
                        <h1 class="h3 mb-0">Modifier l'Événement</h1>
                    </div>

                    <form action="${pageContext.request.contextPath}/organizer/events" method="POST" enctype="multipart/form-data">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="id" value="${event.id}">
                        <input type="hidden" id="dateDebut" name="dateDebut" value="${event.dateDebut}">
                        <input type="hidden" id="dateFin" name="dateFin" value="${event.dateFin}">
                        <input type="hidden" id="latitude" name="latitude" value="${event.latitude}">
                        <input type="hidden" id="longitude" name="longitude" value="${event.longitude}">

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="titre" class="form-label">Titre de l'événement</label>
                                <input type="text" class="form-control" id="titre" name="titre" required 
                                       value="${event.titre}" placeholder="Ex: Conférence sur l'IA">
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="capacite" class="form-label">Capacité</label>
                                <input type="number" class="form-control" id="capacite" name="capacite" 
                                       min="1" value="${event.capacite}" placeholder="Nombre de places">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="lieu" class="form-label">Nom du lieu</label>
                            <input type="text" class="form-control" id="lieu" name="lieu" 
                                   value="${event.lieu}" placeholder="Ex: Palais des Congrès, Salle de conférence">
                        </div>

                        <div class="mb-3">
                            <label for="map-select" class="form-label">Localisation (Cliquez sur la carte)</label>
                            <div id="map-select"></div>
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="dateDebutInput" class="form-label">Date de début</label>
                                <input type="datetime-local" class="form-control" id="dateDebutInput" required>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="dateFinInput" class="form-label">Date de fin</label>
                                <input type="datetime-local" class="form-control" id="dateFinInput" required>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="description" class="form-label">Description</label>
                            <textarea class="form-control" id="description" name="description" rows="4" 
                                      placeholder="Décrivez votre événement...">${event.description}</textarea>
                        </div>

                        <div class="mb-3">
                            <label for="eventImage" class="form-label">Image de l'événement</label>
                            <input type="file" class="form-control" id="eventImage" name="eventImage" 
                                   accept="image/jpeg,image/png,image/webp">
                            <div class="form-text">Formats acceptés: JPG, PNG, WEBP (max 5MB)</div>
                            <c:if test="${not empty event.imageUrl}">
                                <div class="mt-2">
                                    <img src="${event.imageUrl}" class="image-preview" alt="Image actuelle">
                                    <div class="text-muted small">Image actuelle</div>
                                </div>
                            </c:if>
                            <div id="imagePreview" class="mt-2"></div>
                        </div>

                        <div class="d-flex justify-content-between">
                            <a href="${pageContext.request.contextPath}/organizer/events/detail?id=${event.id}" class="btn btn-outline-secondary">
                                <i class="bi bi-arrow-left me-1"></i>Annuler
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-check-circle me-1"></i>Mettre à jour
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
        // Wait for window to fully load (including all scripts)
        window.addEventListener('load', function() {
            console.log('Window loaded, initializing...');

            // --- Image Preview ---
            const imageInput = document.getElementById('eventImage');
            const imagePreview = document.getElementById('imagePreview');

            imageInput.addEventListener('change', function(e) {
                const file = e.target.files[0];
                if (file) {
                    // Validate file size (5MB max)
                    if (file.size > 5 * 1024 * 1024) {
                        alert('Le fichier est trop volumineux. Taille maximale: 5MB');
                        this.value = '';
                        return;
                    }

                    // Validate file type
                    const allowedTypes = ['image/jpeg', 'image/png', 'image/webp'];
                    if (!allowedTypes.includes(file.type)) {
                        alert('Format de fichier non supporté. Utilisez JPG, PNG ou WEBP.');
                        this.value = '';
                        return;
                    }

                    // Show preview
                    const reader = new FileReader();
                    reader.onload = function(e) {
                        imagePreview.innerHTML = `
                            <img src="${e.target.result}" class="image-preview" alt="Nouvel aperçu">
                            <div class="text-muted small mt-1">Nouvel aperçu</div>
                        `;
                    };
                    reader.readAsDataURL(file);
                } else {
                    imagePreview.innerHTML = '';
                }
            });

            // --- Date Time Inputs ---
            const debutInput = document.getElementById('dateDebut');
            const finInput = document.getElementById('dateFin');
            const dateDebutInput = document.getElementById('dateDebutInput');
            const dateFinInput = document.getElementById('dateFinInput');

            // Convert backend format to datetime-local format
            function formatForInput(backendValue) {
                if (!backendValue) return "";
                // Backend format: "YYYY-MM-DDTHH:mm:ss" or "YYYY-MM-DDTHH:mm"
                // datetime-local needs: "YYYY-MM-DDTHH:mm"
                return backendValue.substring(0, 16);
            }

            // Set initial values
            dateDebutInput.value = formatForInput(debutInput.value);
            dateFinInput.value = formatForInput(finInput.value);

            // Convert datetime-local format to backend format
            function formatForBackend(datetimeLocalValue) {
                if (!datetimeLocalValue) return "";
                return datetimeLocalValue;
            }

            // Update hidden fields when user changes dates
            dateDebutInput.addEventListener('change', function() {
                debutInput.value = formatForBackend(this.value);
                console.log('Start date:', debutInput.value);
            });

            dateFinInput.addEventListener('change', function() {
                finInput.value = formatForBackend(this.value);
                console.log('End date:', finInput.value);
            });

            // --- Map (Leaflet) ---
            const latInput = document.getElementById('latitude');
            const lonInput = document.getElementById('longitude');

            const startLat = parseFloat(latInput.value) || 35.5785;
            const startLon = parseFloat(lonInput.value) || -5.3684;
            const startZoom = 12;

            console.log('Initializing map...');

            // Check if Leaflet is loaded
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

                // Add existing marker if coordinates exist
                if (latInput.value && lonInput.value) {
                    marker = L.marker([startLat, startLon]).addTo(map);
                }

                map.on('click', function(e) {
                    const lat = e.latlng.lat;
                    const lon = e.latlng.lng;

                    if (marker) {
                        marker.setLatLng(e.latlng);
                    } else {
                        marker = L.marker(e.latlng).addTo(map);
                    }

                    latInput.value = lat;
                    lonInput.value = lon;
                    console.log('Location selected:', lat, lon);
                });

                // Force map to resize after container is visible
                setTimeout(function() {
                    map.invalidateSize();
                    console.log('Map initialized successfully');
                }, 100);

            } catch (error) {
                console.error('Error initializing map:', error);
            }
        });
    </script>

</body>
</html>