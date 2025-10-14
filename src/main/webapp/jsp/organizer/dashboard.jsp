<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Organisateur Dashboard - Event Management Platform</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-success">
        <div class="container-fluid">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">
                <i class="bi bi-calendar-event"></i> Event Management
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/organizer/dashboard">
                            <i class="bi bi-speedometer2"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/profile">
                            <i class="bi bi-person"></i> ${sessionScope.userEmail}
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                            <i class="bi bi-box-arrow-right"></i> Déconnexion
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div class="row">
            <div class="col-12">
                <div class="alert alert-success" role="alert">
                    <h4 class="alert-heading">
                        <i class="bi bi-check-circle-fill"></i> Bienvenue, Organisateur!
                    </h4>
                    <p>Félicitations! Votre demande pour devenir organisateur a été approuvée.</p>
                    <hr>
                    <p class="mb-0">Vous pouvez maintenant créer et gérer des événements.</p>
                </div>

                <h1 class="mb-4">
                    <i class="bi bi-calendar-plus"></i> Tableau de bord - Organisateur
                </h1>

                <!-- Quick Actions -->
                <div class="row mb-4">
                    <div class="col-md-6">
                        <div class="card border-success">
                            <div class="card-body text-center">
                                <i class="bi bi-plus-circle text-success" style="font-size: 3rem;"></i>
                                <h5 class="card-title mt-3">Créer un événement</h5>
                                <p class="card-text">Organisez un nouvel événement pour votre communauté</p>
                                <a href="#" class="btn btn-success disabled">
                                    <i class="bi bi-calendar-plus"></i> Créer un événement
                                </a>
                                <p class="text-muted mt-2 small">
                                    <i class="bi bi-info-circle"></i> Fonctionnalité gérée par le module Événements
                                </p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card border-primary">
                            <div class="card-body text-center">
                                <i class="bi bi-list-ul text-primary" style="font-size: 3rem;"></i>
                                <h5 class="card-title mt-3">Mes événements</h5>
                                <p class="card-text">Consultez et gérez vos événements existants</p>
                                <a href="#" class="btn btn-primary disabled">
                                    <i class="bi bi-list"></i> Voir mes événements
                                </a>
                                <p class="text-muted mt-2 small">
                                    <i class="bi bi-info-circle"></i> Fonctionnalité gérée par le module Événements
                                </p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Stats Cards -->
                <div class="row">
                    <div class="col-md-4">
                        <div class="card text-white bg-info mb-3">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-center">
                                    <div>
                                        <h6 class="card-title">Événements créés</h6>
                                        <h2 class="mb-0">0</h2>
                                    </div>
                                    <i class="bi bi-calendar-check" style="font-size: 2.5rem; opacity: 0.5;"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card text-white bg-warning mb-3">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-center">
                                    <div>
                                        <h6 class="card-title">Participants totaux</h6>
                                        <h2 class="mb-0">0</h2>
                                    </div>
                                    <i class="bi bi-people" style="font-size: 2.5rem; opacity: 0.5;"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card text-white bg-success mb-3">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-center">
                                    <div>
                                        <h6 class="card-title">Note moyenne</h6>
                                        <h2 class="mb-0">-</h2>
                                    </div>
                                    <i class="bi bi-star" style="font-size: 2.5rem; opacity: 0.5;"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Information Box -->
                <div class="card mt-4">
                    <div class="card-header bg-light">
                        <h5 class="mb-0">
                            <i class="bi bi-lightbulb"></i> Prochaines étapes
                        </h5>
                    </div>
                    <div class="card-body">
                        <ul>
                            <li>Complétez votre profil d'organisateur avec vos informations</li>
                            <li>Créez votre premier événement (fonctionnalité gérée par votre collègue)</li>
                            <li>Invitez des participants à rejoindre vos événements</li>
                            <li>Gérez les inscriptions et communiquez avec les participants</li>
                        </ul>
                        <div class="alert alert-info mt-3">
                            <i class="bi bi-info-circle"></i>
                            <strong>Note:</strong> La création et la gestion d'événements sont gérées par le module Événements
                            développé par votre coéquipier. Cette page sert de point d'entrée pour les organisateurs.
                        </div>
                    </div>
                </div>

                <!-- Profile Link -->
                <div class="d-grid gap-2 mt-4">
                    <a href="${pageContext.request.contextPath}/profile" class="btn btn-outline-success btn-lg">
                        <i class="bi bi-person-gear"></i> Modifier mon profil d'organisateur
                    </a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
