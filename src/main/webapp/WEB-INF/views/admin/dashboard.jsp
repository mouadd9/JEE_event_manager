<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - EventManager</title>
    
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
        
        .stat-card {
            background: white;
            border-radius: 15px;
            padding: 1.5rem;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            transition: transform 0.3s, box-shadow 0.3s;
            margin-bottom: 1.5rem;
        }
        
        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 20px rgba(0,0,0,0.15);
        }
        
        .stat-icon {
            font-size: 3rem;
            opacity: 0.8;
        }
        
        .stat-value {
            font-size: 2.5rem;
            font-weight: 700;
            margin: 0.5rem 0;
            color: #2d1038;
        }
        
        .stat-label {
            color: #2d1038;
            font-size: 0.9rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .quick-action-btn {
            background: white;
            border: 2px solid #8c65a7;
            color: #8c65a7;
            border-radius: 10px;
            padding: 1rem;
            text-align: center;
            transition: all 0.3s;
            text-decoration: none;
            display: block;
        }
        
        .quick-action-btn:hover {
            background: #8c65a7;
            color: white;
            transform: translateY(-3px);
        }
        
        .quick-action-btn i {
            font-size: 2rem;
            display: block;
            margin-bottom: 0.5rem;
        }
        
        .section-title {
            font-size: 1.5rem;
            font-weight: 700;
            color: #2d1038;
            margin-bottom: 1.5rem;
            padding-bottom: 0.5rem;
            border-bottom: 3px solid #8c65a7;
        }
        
        .alert-warning {
            background: #fff3cd;
            border-left: 4px solid #ffc107;
            border-radius: 10px;
        }
        
        .nav-pills .nav-link {
            color: #6c757d;
            border-radius: 10px;
        }
        
        .nav-pills .nav-link.active {
            background: #8c65a7;
        }
    </style>
</head>
<body>
    <!-- Admin Header -->
    <div class="admin-header">
        <div class="container">
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <h1><i class="fas fa-user-shield me-2"></i>Administration</h1>
                    <p class="mb-0">Tableau de bord d'administration</p>
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
                <a class="nav-link active" href="${pageContext.request.contextPath}/admin/dashboard">
                    <i class="fas fa-chart-line me-2"></i>Dashboard
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/users">
                    <i class="fas fa-users me-2"></i>Utilisateurs
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/events">
                    <i class="fas fa-calendar me-2"></i>Événements
                </a>
            </li>
        </ul>
        
        <!-- Error/Success Messages -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle me-2"></i>${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i>${success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <!-- Pending Organisateurs Alert -->
        <c:if test="${stats.pendingOrganisateurs > 0}">
            <div class="alert alert-warning" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>
                <strong>Attention!</strong> Il y a ${stats.pendingOrganisateurs} organisateur(s) en attente de validation.
                <a href="${pageContext.request.contextPath}/admin/users?verified=false&userType=ORGANISATEUR" class="alert-link">Voir la liste</a>
            </div>
        </c:if>
        
        <!-- User Statistics -->
        <h2 class="section-title">Statistiques Utilisateurs</h2>
        <div class="row">
            <div class="col-md-3">
                <div class="stat-card">
                    <i class="fas fa-users stat-icon text-primary"></i>
                    <div class="stat-value">${stats.totalUsers}</div>
                    <div class="stat-label">Total Utilisateurs</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-card">
                    <i class="fas fa-user-friends stat-icon text-success"></i>
                    <div class="stat-value">${stats.totalParticipants}</div>
                    <div class="stat-label">Participants</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-card">
                    <i class="fas fa-user-tie stat-icon text-info"></i>
                    <div class="stat-value">${stats.totalOrganisateurs}</div>
                    <div class="stat-label">Organisateurs</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-card">
                    <i class="fas fa-user-clock stat-icon text-warning"></i>
                    <div class="stat-value">${stats.pendingOrganisateurs}</div>
                    <div class="stat-label">En Attente</div>
                </div>
            </div>
        </div>
        
        <div class="row mb-4">
            <div class="col-md-6">
                <div class="stat-card">
                    <i class="fas fa-user-shield stat-icon text-secondary"></i>
                    <div class="stat-value">${stats.totalAdmins}</div>
                    <div class="stat-label">Administrateurs</div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="stat-card">
                    <i class="fas fa-user-slash stat-icon text-danger"></i>
                    <div class="stat-value">${stats.suspendedUsers}</div>
                    <div class="stat-label">Comptes Suspendus</div>
                </div>
            </div>
        </div>
        
        <!-- Event Statistics -->
        <h2 class="section-title">Statistiques Événements</h2>
        <div class="row">
            <div class="col-md-3">
                <div class="stat-card">
                    <i class="fas fa-calendar-alt stat-icon text-primary"></i>
                    <div class="stat-value">${stats.totalEvents}</div>
                    <div class="stat-label">Total Événements</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-card">
                    <i class="fas fa-calendar-check stat-icon text-success"></i>
                    <div class="stat-value">${stats.publishedEvents}</div>
                    <div class="stat-label">Publiés</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-card">
                    <i class="fas fa-calendar-day stat-icon text-info"></i>
                    <div class="stat-value">${stats.draftEvents}</div>
                    <div class="stat-label">Brouillons</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-card">
                    <i class="fas fa-eye-slash stat-icon text-warning"></i>
                    <div class="stat-value">${stats.hiddenEvents}</div>
                    <div class="stat-label">Masqués</div>
                </div>
            </div>
        </div>
        
        <div class="row mb-4">
            <div class="col-md-12">
                <div class="stat-card">
                    <i class="fas fa-calendar-times stat-icon text-danger"></i>
                    <div class="stat-value">${stats.cancelledEvents}</div>
                    <div class="stat-label">Événements Annulés</div>
                </div>
            </div>
        </div>
        
        <!-- Quick Actions -->
        <h2 class="section-title">Actions Rapides</h2>
        <div class="row mb-5">
            <div class="col-md-4">
                <a href="${pageContext.request.contextPath}/admin/users" class="quick-action-btn">
                    <i class="fas fa-users-cog"></i>
                    <div>Gérer les Utilisateurs</div>
                </a>
            </div>
            <div class="col-md-4">
                <a href="${pageContext.request.contextPath}/admin/events" class="quick-action-btn">
                    <i class="fas fa-tasks"></i>
                    <div>Modérer les Événements</div>
                </a>
            </div>
            <div class="col-md-4">
                <a href="${pageContext.request.contextPath}/admin/users?verified=false&userType=ORGANISATEUR" class="quick-action-btn">
                    <i class="fas fa-user-check"></i>
                    <div>Valider les Organisateurs</div>
                </a>
            </div>
        </div>
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
