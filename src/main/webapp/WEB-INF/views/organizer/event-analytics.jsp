<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Analytics - ${event.titre} - EventHub</title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/organizer-theme.css">
    
    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f8f9fa;
            color: #1f2937;
        }
        
        .analytics-card {
            background: white;
            border-radius: 1rem;
            padding: 1.5rem;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 1.5rem;
        }
        
        .stat-number {
            font-size: 2rem;
            font-weight: 700;
            color: #a855f7;
        }
        
        .stat-label {
            color: #6b7280;
            font-size: 0.875rem;
            font-weight: 500;
        }
        
        .participant-avatar {
            width: 2.5rem;
            height: 2.5rem;
            border-radius: 50%;
            background: linear-gradient(135deg, #a855f7, #9333ea);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: 600;
            font-size: 0.875rem;
        }
        
        .comment-card, .review-card {
            background: #f8f9fa;
            border-radius: 0.75rem;
            padding: 1rem;
            margin-bottom: 1rem;
            border-left: 4px solid #a855f7;
        }
        
        .reply-section {
            margin-top: 1rem;
            padding-top: 1rem;
            border-top: 1px solid #e5e7eb;
        }
        
        .reply-form {
            background: white;
            border-radius: 0.5rem;
            padding: 1rem;
            margin-top: 0.5rem;
        }
        
        .capacity-bar {
            height: 1rem;
            background: #e5e7eb;
            border-radius: 0.5rem;
            overflow: hidden;
        }
        
        .capacity-fill {
            height: 100%;
            background: linear-gradient(90deg, #a855f7, #9333ea);
            transition: width 0.3s ease;
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
    <div class="container-fluid py-4">
        <!-- Header -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h1 class="h3 mb-1">
                            <i class="bi bi-graph-up text-primary me-2"></i>Analytics - <c:out value="${event.titre}"/>
                        </h1>
                        <p class="text-muted mb-0">
                            <i class="bi bi-calendar3 me-1"></i>
                            <fmt:formatDate value="${event.dateDebutAsDate}" pattern="dd MMM yyyy, HH:mm"/>
                        </p>
                    </div>
                    <div>
                        <a href="${pageContext.request.contextPath}/organizer/events/detail?id=${event.id}" class="btn btn-outline-primary">
                            <i class="bi bi-arrow-left me-1"></i>Retour à l'événement
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Statistics Cards -->
        <div class="row g-3 mb-4">
            <div class="col-md-3 col-sm-6">
                <div class="analytics-card text-center">
                    <div class="stat-number">${analytics.nombreInscrits}</div>
                    <div class="stat-label">Participants inscrits</div>
                </div>
            </div>
            <div class="col-md-3 col-sm-6">
                <div class="analytics-card text-center">
                    <div class="stat-number">${analytics.capaciteDisponible}</div>
                    <div class="stat-label">Places disponibles</div>
                </div>
            </div>
            <div class="col-md-3 col-sm-6">
                <div class="analytics-card text-center">
                    <div class="stat-number">
                        <c:choose>
                            <c:when test="${analytics.noteMoyenne != null}">
                                <fmt:formatNumber value="${analytics.noteMoyenne}" maxFractionDigits="1"/>
                            </c:when>
                            <c:otherwise>0.0</c:otherwise>
                        </c:choose>
                    </div>
                    <div class="stat-label">Note moyenne</div>
                </div>
            </div>
            <div class="col-md-3 col-sm-6">
                <div class="analytics-card text-center">
                    <div class="stat-number">${analytics.nombreCommentaires}</div>
                    <div class="stat-label">Commentaires</div>
                </div>
            </div>
        </div>

        <!-- Capacity Bar -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="analytics-card">
                    <h5 class="mb-3">
                        <i class="bi bi-people text-primary me-2"></i>Capacité de l'événement
                    </h5>
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <span class="fw-medium">${analytics.nombreInscrits} / ${analytics.capacite} participants</span>
                        <span class="text-muted">${analytics.pourcentageCapacite}%</span>
                    </div>
                    <div class="capacity-bar">
                        <div class="capacity-fill" style="width: ${analytics.pourcentageCapacite}%"></div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Export Buttons -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="analytics-card">
                    <h5 class="mb-3">
                        <i class="bi bi-download text-primary me-2"></i>Exporter les données
                    </h5>
                    <div class="d-flex gap-2">
                        <a href="${pageContext.request.contextPath}/organizer?action=export-participants&eventId=${event.id}" 
                           class="btn btn-success export-button">
                            <i class="bi bi-file-excel me-1"></i>Liste des participants (CSV)
                        </a>
                        <a href="${pageContext.request.contextPath}/organizer?action=export-analytics&eventId=${event.id}" 
                           class="btn btn-danger export-button">
                            <i class="bi bi-file-pdf me-1"></i>Rapport complet (PDF)
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <div class="row">
            <!-- Participants List -->
            <div class="col-lg-6 mb-4">
                <div class="analytics-card">
                    <h5 class="mb-3">
                        <i class="bi bi-people text-primary me-2"></i>Participants inscrits
                    </h5>
                    <c:choose>
                        <c:when test="${not empty analytics.participants}">
                            <div class="list-group list-group-flush">
                                <c:forEach var="participant" items="${analytics.participants}">
                                    <div class="list-group-item border-0 px-0">
                                        <div class="d-flex align-items-center">
                                            <div class="participant-avatar me-3">
                                                ${participant.initials}
                                            </div>
                                            <div class="flex-grow-1">
                                                <h6 class="mb-1">${participant.displayName}</h6>
                                                <small class="text-muted">${participant.email}</small>
                                            </div>
                                            <button class="btn btn-sm btn-outline-primary" 
                                                    onclick="showParticipantDetails(${participant.id})">
                                                <i class="bi bi-eye"></i>
                                            </button>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center text-muted py-4">
                                <i class="bi bi-people fs-1 mb-2"></i>
                                <p>Aucun participant inscrit</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Comments Section -->
            <div class="col-lg-6 mb-4">
                <div class="analytics-card">
                    <h5 class="mb-3">
                        <i class="bi bi-chat-dots text-primary me-2"></i>Commentaires
                    </h5>
                    <c:choose>
                        <c:when test="${not empty analytics.commentaires}">
                            <c:forEach var="commentaire" items="${analytics.commentaires}">
                                <div class="comment-card">
                                    <div class="d-flex align-items-start mb-2">
                                        <div class="participant-avatar me-3">
                                            ${commentaire.participantInitials}
                                        </div>
                                        <div class="flex-grow-1">
                                            <h6 class="mb-1">${commentaire.participantDisplayName}</h6>
                                            <small class="text-muted">
                                                <fmt:formatDate value="${commentaire.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                            </small>
                                        </div>
                                    </div>
                                    <p class="mb-2">${commentaire.texte}</p>
                                    
                                    <!-- Reply Section -->
                                    <div class="reply-section">
                                        <button class="btn btn-sm btn-outline-primary" 
                                                onclick="showReplyForm('comment-${commentaire.id}')">
                                            <i class="bi bi-reply me-1"></i>Répondre
                                        </button>
                                        <div id="replyForm-comment-${commentaire.id}" class="reply-form" style="display:none;">
                                            <textarea class="form-control mb-2" placeholder="Votre réponse..."></textarea>
                                            <button class="btn btn-primary btn-sm" 
                                                    onclick="submitReply('comment', ${commentaire.id})">
                                                Envoyer
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center text-muted py-4">
                                <i class="bi bi-chat-dots fs-1 mb-2"></i>
                                <p>Aucun commentaire</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Reviews Section -->
        <div class="row">
            <div class="col-12">
                <div class="analytics-card">
                    <h5 class="mb-3">
                        <i class="bi bi-star text-primary me-2"></i>Évaluations
                    </h5>
                    <c:choose>
                        <c:when test="${not empty analytics.evaluations}">
                            <c:forEach var="evaluation" items="${analytics.evaluations}">
                                <div class="review-card">
                                    <div class="d-flex align-items-start mb-2">
                                        <div class="participant-avatar me-3">
                                            ${evaluation.participantInitials}
                                        </div>
                                        <div class="flex-grow-1">
                                            <h6 class="mb-1">${evaluation.participantDisplayName}</h6>
                                            <div class="mb-1">
                                                <span class="text-warning">${evaluation.starsDisplay}</span>
                                                <span class="ms-2 fw-medium">${evaluation.noteDisplay}</span>
                                            </div>
                                            <small class="text-muted">
                                                <fmt:formatDate value="${evaluation.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                            </small>
                                        </div>
                                    </div>
                                    <c:if test="${not empty evaluation.commentaire}">
                                        <p class="mb-2">${evaluation.commentaire}</p>
                                    </c:if>
                                    
                                    <!-- Reply Section -->
                                    <div class="reply-section">
                                        <button class="btn btn-sm btn-outline-primary" 
                                                onclick="showReplyForm('review-${evaluation.id}')">
                                            <i class="bi bi-reply me-1"></i>Répondre
                                        </button>
                                        <div id="replyForm-review-${evaluation.id}" class="reply-form" style="display:none;">
                                            <textarea class="form-control mb-2" placeholder="Votre réponse..."></textarea>
                                            <button class="btn btn-primary btn-sm" 
                                                    onclick="submitReply('review', ${evaluation.id})">
                                                Envoyer
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center text-muted py-4">
                                <i class="bi bi-star fs-1 mb-2"></i>
                                <p>Aucune évaluation</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>

    <!-- Participant Details Modal -->
    <div class="modal fade" id="participantModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Détails du participant</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="text-center mb-3">
                        <div class="participant-avatar mx-auto" style="width: 4rem; height: 4rem; font-size: 1.5rem;">
                            <span id="modal-initials">??</span>
                        </div>
                    </div>
                    <table class="table table-borderless">
                        <tr><th>Nom:</th><td id="modal-nom"></td></tr>
                        <tr><th>Email:</th><td id="modal-email"></td></tr>
                        <tr><th>Téléphone:</th><td id="modal-telephone"></td></tr>
                        <tr><th>Membre depuis:</th><td id="modal-createdAt"></td></tr>
                        <tr><th>Inscriptions:</th><td id="modal-inscriptions"></td></tr>
                        <tr><th>Commentaires:</th><td id="modal-commentaires"></td></tr>
                        <tr><th>Évaluations:</th><td id="modal-evaluations"></td></tr>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        function showParticipantDetails(participantId) {
            fetch(`${pageContext.request.contextPath}/organizer/participant/${participantId}`)
                .then(res => res.json())
                .then(data => {
                    document.getElementById('modal-initials').textContent = data.initials;
                    document.getElementById('modal-nom').textContent = data.displayName;
                    document.getElementById('modal-email').textContent = data.email;
                    document.getElementById('modal-telephone').textContent = data.telephone || 'Non renseigné';
                    document.getElementById('modal-createdAt').textContent = data.accountAge;
                    document.getElementById('modal-inscriptions').textContent = data.nombreInscriptions;
                    document.getElementById('modal-commentaires').textContent = data.nombreCommentaires;
                    document.getElementById('modal-evaluations').textContent = data.nombreEvaluations;
                    new bootstrap.Modal(document.getElementById('participantModal')).show();
                })
                .catch(error => {
                    console.error('Error loading participant details:', error);
                    alert('Erreur lors du chargement des détails du participant');
                });
        }
        
        function showReplyForm(formId) {
            const form = document.getElementById('replyForm-' + formId);
            if (form.style.display === 'none') {
                form.style.display = 'block';
            } else {
                form.style.display = 'none';
            }
        }
        
        function submitReply(type, id) {
            const form = document.querySelector(`#replyForm-${type}-${id}`);
            const textarea = form.querySelector('textarea');
            const text = textarea.value.trim();
            
            if (!text) {
                alert('Veuillez saisir une réponse');
                return;
            }
            
            // TODO: Implement reply submission
            console.log(`Submitting ${type} reply for ID ${id}:`, text);
            alert('Fonctionnalité de réponse en cours de développement');
        }
    </script>
</body>
</html>
