<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Système de Gestion d'Événements - ENSA Tétouan</title>

    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-8 text-center">
                <h1 class="display-4 mb-4">Système de Gestion d'Événements</h1>
                <p class="lead">ENSA Tétouan - Event Management Platform</p>

                <div class="alert alert-success mt-4" role="alert">
                    <h4 class="alert-heading">Projet initialisé avec succès!</h4>
                    <p>La structure du projet Java EE a été créée correctement.</p>
                    <hr>
                    <p class="mb-0">Prochaine étape: Implémentation des entités et de la couche DAO</p>
                </div>

                <div class="mt-5">
                    <h3>Fonctionnalités à implémenter:</h3>
                    <ul class="list-group text-start mt-3">
                        <li class="list-group-item">Authentification & Gestion des utilisateurs</li>
                        <li class="list-group-item">Gestion des événements (CRUD)</li>
                        <li class="list-group-item">Découverte et inscription aux événements</li>
                        <li class="list-group-item">Tableaux de bord par rôle</li>
                        <li class="list-group-item">Système de notifications</li>
                        <li class="list-group-item">Commentaires et évaluations</li>
                        <li class="list-group-item">Géolocalisation (Google Maps)</li>
                        <li class="list-group-item">Statistiques et rapports</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

    <!-- Custom JS -->
    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
