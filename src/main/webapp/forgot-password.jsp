<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mot de passe oublié - Event Manager</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <%@ include file="/WEB-INF/jspf/theme-head.jspf" %>
    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .forgot-container {
            max-width: 500px;
            width: 100%;
            padding: 20px;
        }
        .forgot-card {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            overflow: hidden;
        }
        .card-header {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            padding: 40px 30px;
            text-align: center;
        }
        .card-header i {
            font-size: 60px;
            margin-bottom: 20px;
        }
        .card-body {
            padding: 40px 30px;
        }
        .form-control {
            padding: 12px 20px;
            border-radius: 10px;
            border: 2px solid #e0e0e0;
            transition: all 0.3s;
        }
        .form-control:focus {
            border-color: #f5576c;
            box-shadow: 0 0 0 3px rgba(245, 87, 108, 0.1);
        }
        .btn-reset {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            border: none;
            color: white;
            padding: 12px;
            border-radius: 10px;
            font-weight: 600;
            width: 100%;
            transition: transform 0.2s;
        }
        .btn-reset:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 20px rgba(245, 87, 108, 0.4);
        }
        .back-link {
            color: #f5576c;
            text-decoration: none;
            font-weight: 500;
        }
        .back-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="forgot-container">
        <div class="forgot-card">
            <div class="card-header">
                <i class="fas fa-key"></i>
                <h2 class="mb-0">Mot de passe oublié?</h2>
            </div>
            <div class="card-body">
                <!-- Success/Error Messages -->
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

                <p class="text-center text-muted mb-4">
                    Entrez votre adresse email et nous vous enverrons un mot de passe temporaire.
                </p>

                <form action="${pageContext.request.contextPath}/forgot-password" method="POST">
                    <div class="mb-4">
                        <label for="email" class="form-label">
                            <i class="fas fa-envelope me-2"></i>Adresse email
                        </label>
                        <input type="email" class="form-control" id="email" name="email" 
                               placeholder="votre@email.com" required autofocus>
                    </div>
                    
                    <button type="submit" class="btn btn-reset">
                        <i class="fas fa-paper-plane me-2"></i>Envoyer le mot de passe
                    </button>
                </form>

                <div class="text-center mt-4">
                    <p class="mb-0">
                        <small>
                            <a href="${pageContext.request.contextPath}/login" class="back-link">
                                <i class="fas fa-arrow-left me-1"></i>Retour à la connexion
                            </a>
                        </small>
                    </p>
                </div>

                <div class="alert alert-info mt-4" role="alert">
                    <i class="fas fa-info-circle me-2"></i>
                    <small>
                        <strong>Important:</strong> Pour votre sécurité, vous devrez changer ce mot de passe temporaire lors de votre première connexion.
                    </small>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
