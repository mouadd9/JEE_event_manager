<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>${pageTitle != null ? pageTitle : 'Event Management System'} - ENSA Tétouan</title>

    <!-- Bootstrap 5.3 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font Awesome 6 -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- Custom CSS -->
    <style>
        :root {
            --primary-color: #0d6efd;
            --secondary-color: #6c757d;
            --success-color: #198754;
            --danger-color: #dc3545;
            --warning-color: #ffc107;
            --info-color: #0dcaf0;
        }

        body {
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        main {
            flex: 1;
        }

        .navbar-brand {
            font-weight: 700;
            font-size: 1.5rem;
        }

        .navbar-brand i {
            color: var(--primary-color);
        }

        .user-badge {
            font-size: 0.75rem;
            padding: 0.25rem 0.5rem;
        }

        .search-form {
            max-width: 400px;
        }

        @media (max-width: 991px) {
            .search-form {
                max-width: 100%;
                margin-top: 1rem;
            }
        }
    </style>
</head>
<body>

<!-- Navigation Bar -->
<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow">
    <div class="container-fluid">
        <!-- Brand -->
        <a class="navbar-brand" href="${pageContext.request.contextPath}/">
            <i class="fas fa-calendar-alt"></i> EventManagement
        </a>

        <!-- Mobile Toggle -->
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarMain"
                aria-controls="navbarMain" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <!-- Navbar Content -->
        <div class="collapse navbar-collapse" id="navbarMain">
            <!-- Left Navigation -->
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/">
                        <i class="fas fa-home"></i> Accueil
                    </a>
                </li>

                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/events/browse">
                        <i class="fas fa-search"></i> Parcourir
                    </a>
                </li>

                <!-- User-specific navigation -->
                <c:if test="${not empty sessionScope.currentUser}">
                    <c:set var="userRole" value="${sessionScope.currentUser.role}"/>

                    <!-- Participant Menu -->
                    <c:if test="${userRole == 'PARTICIPANT'}">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/events/my-events">
                                <i class="fas fa-ticket-alt"></i> Mes Événements
                            </a>
                        </li>
                    </c:if>

                    <!-- Organizer Menu -->
                    <c:if test="${userRole == 'ORGANISATEUR'}">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/events/manage">
                                <i class="fas fa-tasks"></i> Mes Événements
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/events/create">
                                <i class="fas fa-plus-circle"></i> Créer
                            </a>
                        </li>
                    </c:if>

                    <!-- Admin Menu -->
                    <c:if test="${userRole == 'ADMINISTRATEUR'}">
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="adminDropdown" role="button"
                               data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="fas fa-cog"></i> Administration
                            </a>
                            <ul class="dropdown-menu" aria-labelledby="adminDropdown">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/users">
                                    <i class="fas fa-users"></i> Utilisateurs
                                </a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/events">
                                    <i class="fas fa-calendar"></i> Événements
                                </a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/categories">
                                    <i class="fas fa-tags"></i> Catégories
                                </a></li>
                            </ul>
                        </li>
                    </c:if>
                </c:if>
            </ul>


            <!-- Right Navigation -->
            <ul class="navbar-nav">
                <c:choose>
                    <c:when test="${not empty sessionScope.currentUser}">
                        <!-- User Menu -->
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button"
                               data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="fas fa-user-circle"></i>
                                <c:out value="${sessionScope.currentUser.nom}"/>
                                <span class="badge bg-light text-dark user-badge ms-1">
                                    <c:out value="${sessionScope.currentUser.role}"/>
                                </span>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                                <li>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/${fn:toLowerCase(sessionScope.currentUser.role == 'ORGANISATEUR' ? 'organizer' : sessionScope.currentUser.role == 'PARTICIPANT' ? 'participant' : 'admin')}/dashboard">
                                        <i class="fas fa-tachometer-alt"></i> Tableau de bord
                                    </a>
                                </li>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/profile">
                                        <i class="fas fa-user-edit"></i> Mon Profil
                                    </a>
                                </li>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">
                                        <i class="fas fa-sign-out-alt"></i> Déconnexion
                                    </a>
                                </li>
                            </ul>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <!-- Guest Menu -->
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/login">
                                <i class="fas fa-sign-in-alt"></i> Connexion
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="btn btn-outline-light btn-sm ms-2" href="${pageContext.request.contextPath}/register">
                                <i class="fas fa-user-plus"></i> S'inscrire
                            </a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>

<!-- Main Content Start -->
<main class="py-4">
