<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion Utilisateurs - Admin</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Neue+Plak:wght@400;600;700&display=swap" rel="stylesheet">
    <%@ include file="/WEB-INF/jspf/theme-head.jspf" %>
    <!-- Theme CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/theme.css">
    
    <style>
        body {
            font-family: 'Neue Plak', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background-color: #f8f9fa;
        }
        
        .admin-header {
            background: linear-gradient(135deg, #8c65a7 0%, #7d24bd 100%);
            color: white;
            padding: 2rem 0;
            margin-bottom: 2rem;
        }
        
        .user-card {
            background: white;
            border-radius: 10px;
            padding: 1.5rem;
            margin-bottom: 1rem;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            transition: transform 0.3s;
        }
        
        .user-card h5,
        .user-card p {
            color: #2d1038 !important;
        }
        
        .user-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.15);
        }
        
        .badge-verified {
            background: #28a745;
        }
        
        .badge-pending {
            background: #ffc107;
            color: #000;
        }
        
        .badge-suspended {
            background: #dc3545;
        }
        
        .btn-action {
            padding: 0.4rem 0.8rem;
            border-radius: 5px;
            font-size: 0.85rem;
            margin: 0.2rem;
        }
        
        .filter-card {
            background: white;
            border-radius: 10px;
            padding: 1.5rem;
            margin-bottom: 2rem;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .nav-pills .nav-link {
            color: #6c757d;
            border-radius: 10px;
        }
        
        .nav-pills .nav-link.active {
            background: #8c65a7;
        }
        
        h4, h5, .form-label {
            color: #2d1038 !important;
        }
        
        .form-select,
        .form-select option {
            color: #2d1038 !important;
            background-color: #fff !important;
        }
    </style>
</head>
<body>
    <!-- Admin Header -->
    <div class="admin-header">
        <div class="container">
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <h1><i class="fas fa-users me-2"></i>Gestion des Utilisateurs</h1>
                    <p class="mb-0">Gérer, valider et modérer les comptes utilisateurs</p>
                </div>
                <div>
                    <span class="me-3"><i class="fas fa-user me-2"></i>${sessionScope.userName}</span>
                    <a href="${pageContext.request.contextPath}/admin/profil" class="btn btn-outline-light me-2">
                        <i class="fas fa-user-circle me-2"></i>Profil
                    </a>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light">
                        <i class="fas fa-sign-out-alt me-2"></i>Déconnexion
                    </a>
                </div>
            </div>
        </div>
    </div>
    
    <div class="container">
        <!-- Navigation Tabs -->
        <ul class="nav nav-pills mb-4">
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">
                    <i class="fas fa-chart-line me-2"></i>Dashboard
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link active" href="${pageContext.request.contextPath}/admin/users">
                    <i class="fas fa-users me-2"></i>Utilisateurs
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/events">
                    <i class="fas fa-calendar me-2"></i>Événements
                </a>
            </li>
        </ul>
        
        <!-- Filters -->
        <div class="filter-card">
            <form method="GET" action="${pageContext.request.contextPath}/admin/users">
                <div class="row g-3 align-items-end">
                    <div class="col-md-3">
                        <label class="form-label text-dark"><i class="fas fa-user-tag me-1"></i>Type d'utilisateur</label>
                        <select name="userType" class="form-select" style="color: #2d1038 !important;">
                            <option value="">Tous</option>
                            <option value="PARTICIPANT" ${selectedUserType == 'PARTICIPANT' ? 'selected' : ''}>Participants</option>
                            <option value="ORGANISATEUR" ${selectedUserType == 'ORGANISATEUR' ? 'selected' : ''}>Organisateurs</option>
                            <option value="ADMIN" ${selectedUserType == 'ADMIN' ? 'selected' : ''}>Administrateurs</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label text-dark"><i class="fas fa-check-circle me-1"></i>Vérification</label>
                        <select name="verified" class="form-select" style="color: #2d1038 !important;">
                            <option value="">Tous</option>
                            <option value="true" ${selectedVerified == 'true' ? 'selected' : ''}>Vérifiés</option>
                            <option value="false" ${selectedVerified == 'false' ? 'selected' : ''}>Non vérifiés</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label text-dark"><i class="fas fa-ban me-1"></i>Suspension</label>
                        <select name="suspended" class="form-select" style="color: #2d1038 !important;">
                            <option value="">Tous</option>
                            <option value="false" ${selectedSuspended == 'false' ? 'selected' : ''}>Actifs</option>
                            <option value="true" ${selectedSuspended == 'true' ? 'selected' : ''}>Suspendus</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <button type="submit" class="btn btn-primary w-100">
                            <i class="fas fa-filter me-2"></i>Filtrer
                        </button>
                    </div>
                </div>
            </form>
        </div>
        
        <!-- Pending Organisateurs Section -->
        <c:if test="${not empty pendingOrganisateurs && pendingOrganisateurs.size() > 0}">
            <div class="alert alert-warning mb-4">
                <h5 class="text-dark"><i class="fas fa-exclamation-triangle me-2"></i>Organisateurs en attente d'approbation (${pendingOrganisateurs.size()})</h5>
                <p class="mb-0">Ces organisateurs ont vérifié leur email et attendent votre approbation pour accéder à leur compte.</p>
            </div>
            <div class="row mb-4">
                <c:forEach var="user" items="${pendingOrganisateurs}">
                    <div class="col-12">
                        <div class="user-card border-warning border-start border-4">
                            <div class="row align-items-center">
                                <div class="col-md-6">
                                    <h5 class="text-dark"><i class="fas fa-user-tie me-2"></i>${user.nom}</h5>
                                    <p class="text-dark mb-1">
                                        <i class="fas fa-envelope me-2"></i>${user.email}
                                    </p>
                                    <span class="badge badge-pending">
                                        <i class="fas fa-clock me-1"></i>En attente d'approbation
                                    </span>
                                    <c:if test="${user.isVerified}">
                                        <span class="badge badge-verified">
                                            <i class="fas fa-check me-1"></i>Email vérifié
                                        </span>
                                    </c:if>
                                </div>
                                <div class="col-md-6 text-end">
                                    <button class="btn btn-success btn-action" onclick="verifyUser(${user.id})">
                                        <i class="fas fa-check me-1"></i>Approuver
                                    </button>
                                    <button class="btn btn-danger btn-action" onclick="suspendUser(${user.id})">
                                        <i class="fas fa-ban me-1"></i>Refuser
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:if>
        
        <!-- Users List -->
        <h4 class="mb-3" style="color: #2d1038 !important;">Liste des utilisateurs (${users.size()})</h4>
        <div class="row">
            <c:forEach var="user" items="${users}">
                <div class="col-12">
                    <div class="user-card">
                        <div class="row align-items-center">
                            <div class="col-md-1">
                                <i class="fas fa-${user.userType == 'PARTICIPANT' ? 'user' : user.userType == 'ORGANISATEUR' ? 'user-tie' : 'user-shield'} fa-3x text-primary"></i>
                            </div>
                            <div class="col-md-5">
                                <h5 class="text-dark">${user.nom}</h5>
                                <p class="text-dark mb-1">
                                    <i class="fas fa-envelope me-2"></i>${user.email}
                                </p>
                                <span class="badge bg-secondary me-1">${user.userType}</span>
                                <c:if test="${user.isVerified}">
                                    <span class="badge badge-verified">
                                        <i class="fas fa-check me-1"></i>Email vérifié
                                    </span>
                                </c:if>
                                <c:if test="${user.userType == 'ORGANISATEUR' && user.isActive}">
                                    <span class="badge badge-verified">
                                        <i class="fas fa-check-circle me-1"></i>Approuvé
                                    </span>
                                </c:if>
                                <c:if test="${user.userType == 'ORGANISATEUR' && !user.isActive && !user.isSuspended}">
                                    <span class="badge badge-pending">
                                        <i class="fas fa-clock me-1"></i>En attente
                                    </span>
                                </c:if>
                                <c:if test="${user.isSuspended}">
                                    <span class="badge badge-suspended">
                                        <i class="fas fa-ban me-1"></i>Suspendu
                                    </span>
                                </c:if>
                            </div>
                            <div class="col-md-6 text-end">
                                <c:if test="${!user.isActive && user.userType == 'ORGANISATEUR' && !user.isSuspended}">
                                    <button class="btn btn-success btn-action" onclick="verifyUser(${user.id})">
                                        <i class="fas fa-check me-1"></i>Approuver
                                    </button>
                                </c:if>
                                <c:if test="${!user.isSuspended && user.isActive}">
                                    <button class="btn btn-warning btn-action" onclick="suspendUser(${user.id})">
                                        <i class="fas fa-ban me-1"></i>Suspendre
                                    </button>
                                </c:if>
                                <c:if test="${user.isSuspended}">
                                    <button class="btn btn-success btn-action" onclick="activateUser(${user.id})">
                                        <i class="fas fa-check-circle me-1"></i>Activer
                                    </button>
                                </c:if>
                                <c:if test="${user.userType != 'ADMIN'}">
                                    <button class="btn btn-danger btn-action" onclick="deleteUser(${user.id})">
                                        <i class="fas fa-trash me-1"></i>Supprimer
                                    </button>
                                </c:if>
                            </div>
                        </div>
                        <c:if test="${user.isSuspended && not empty user.suspensionReason}">
                            <div class="mt-2 p-2 bg-light rounded">
                                <small class="text-dark"><strong>Raison de suspension:</strong> ${user.suspensionReason}</small>
                            </div>
                        </c:if>
                    </div>
                </div>
            </c:forEach>
            
            <c:if test="${empty users}">
                <div class="col-12">
                    <div class="alert alert-info text-center">
                        <i class="fas fa-info-circle me-2"></i>Aucun utilisateur trouvé avec ces critères.
                    </div>
                </div>
            </c:if>
        </div>
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        function verifyUser(userId) {
            if (!confirm('Voulez-vous vraiment approuver cet organisateur ?')) return;
            
            console.log('Approve organisateur:', userId);
            performAction('verify', userId);
        }
        
        function suspendUser(userId) {
            const reason = prompt('Raison de la suspension:');
            if (!reason || reason.trim() === '') {
                console.log('Suspension cancelled or empty reason');
                return;
            }
            
            console.log('Suspend user:', userId, 'Reason:', reason);
            performAction('suspend', userId, reason);
        }
        
        function activateUser(userId) {
            if (!confirm('Voulez-vous vraiment activer ce compte ?')) return;
            
            console.log('Activate user:', userId);
            performAction('activate', userId);
        }
        
        function deleteUser(userId) {
            if (!confirm('Voulez-vous vraiment supprimer cet utilisateur ? Cette action est irréversible.')) return;
            
            console.log('Delete user:', userId);
            performAction('delete', userId);
        }
        
        function performAction(action, userId, reason) {
            console.log('performAction called with:', { action, userId, reason });
            
            // Validate parameters
            if (!action || !userId) {
                console.error('Missing parameters:', { action, userId });
                alert('Erreur: Paramètres manquants (action ou userId)');
                return;
            }
            
            // Use URLSearchParams instead of FormData for better compatibility
            const params = new URLSearchParams();
            params.append('action', action);
            params.append('userId', userId.toString());
            if (reason) {
                params.append('reason', reason);
            }
            
            // Debug: Log params contents
            console.log('URLSearchParams contents:');
            for (let pair of params.entries()) {
                console.log(pair[0] + ': ' + pair[1]);
            }
            
            const url = '${pageContext.request.contextPath}/admin/users';
            console.log('Sending POST to:', url);
            
            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                },
                body: params.toString()
            })
            .then(response => {
                console.log('Response status:', response.status);
                if (!response.ok) {
                    throw new Error('HTTP error! status: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log('Response data:', data);
                if (data.success) {
                    alert(data.message);
                    location.reload();
                } else {
                    alert('Erreur: ' + (data.message || 'Opération échouée'));
                }
            })
            .catch(error => {
                console.error('Fetch error:', error);
                alert('Erreur lors de l\'opération. Veuillez vérifier la console pour plus de détails.');
            });
        }
    </script>
</body>
</html>
