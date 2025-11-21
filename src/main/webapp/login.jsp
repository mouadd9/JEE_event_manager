<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="fr">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Connexion - EventManager</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        :root {
            --primary-color: #8c65a7;
            --secondary-color: #c191e1;
            --accent-color: #8338b5;
            --text-dark: #212529;
            --text-light: #6c757d;
            --bg-light: #f8f9fa;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            min-height: 100vh;
            overflow: hidden;
        }

        .split-container {
            display: flex;
            height: 100vh;
        }

        /* LEFT SIDE - BRANDING */
        .left-side {
            flex: 1;
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--accent-color) 100%);
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding: 4rem;
            color: white;
            position: relative;
            overflow: hidden;
        }

        .left-side::before {
            content: '';
            position: absolute;
            top: -50%;
            left: -50%;
            width: 200%;
            height: 200%;
            background: radial-gradient(circle, rgba(255,255,255,0.1) 1px, transparent 1px);
            background-size: 50px 50px;
            animation: moveBackground 20s linear infinite;
        }

        @keyframes moveBackground {
            0% { transform: translate(0, 0); }
            100% { transform: translate(50px, 50px); }
        }

        .branding {
            position: relative;
            z-index: 1;
            text-align: center;
        }

        .branding-icon {
            font-size: 5rem;
            margin-bottom: 2rem;
            animation: float 3s ease-in-out infinite;
        }

        @keyframes float {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-20px); }
        }

        .branding h1 {
            font-size: 3rem;
            font-weight: 700;
            margin-bottom: 1rem;
            text-shadow: 0 2px 10px rgba(0,0,0,0.2);
        }

        .branding p {
            font-size: 1.3rem;
            opacity: 0.95;
            line-height: 1.6;
            max-width: 500px;
            margin: 0 auto;
        }

        .features-list {
            margin-top: 3rem;
            text-align: left;
        }

        .feature-item {
            display: flex;
            align-items: center;
            margin-bottom: 1.5rem;
            font-size: 1.1rem;
        }

        .feature-item i {
            font-size: 1.5rem;
            margin-right: 1rem;
            background: rgba(255,255,255,0.2);
            padding: 0.8rem;
            border-radius: 10px;
        }

        /* RIGHT SIDE - FORM */
        .right-side {
            flex: 1;
            background: white;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 2rem;
        }

        .login-form-container {
            max-width: 450px;
            width: 100%;
        }

        .login-header {
            text-align: center;
            margin-bottom: 3rem;
        }

        .login-header h2 {
            font-size: 2rem;
            font-weight: 700;
            color: var(--text-dark);
            margin-bottom: 0.5rem;
        }

        .login-header p {
            color: var(--text-light);
            font-size: 1rem;
        }

        .form-label {
            font-weight: 600;
            color: var(--text-dark);
            margin-bottom: 0.5rem;
            display: block;
        }

        .form-control {
            border: 2px solid #e0e0e0;
            border-radius: 10px;
            padding: 0.9rem 1.2rem;
            font-size: 1rem;
            transition: all 0.3s;
            width: 100%;
        }

        .form-control:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 4px rgba(140, 101, 167, 0.1);
            outline: none;
        }

        .btn-login {
            background: var(--primary-color);
            color: white;
            border: none;
            padding: 1rem 2rem;
            border-radius: 10px;
            font-weight: 600;
            font-size: 1.1rem;
            transition: all 0.3s;
            width: 100%;
            cursor: pointer;
        }

        .btn-login:hover {
            background: var(--accent-color);
            transform: translateY(-2px);
            box-shadow: 0 5px 20px rgba(140, 101, 167, 0.3);
        }

        .alert {
            border-radius: 10px;
            border: none;
            padding: 1rem;
            margin-bottom: 1.5rem;
        }

        .alert-danger {
            background: #fee;
            color: #c00;
        }

        .alert-success {
            background: #efe;
            color: #0a0;
        }

        .form-footer {
            margin-top: 1.5rem;
            text-align: center;
        }

        .form-footer a {
            color: var(--primary-color);
            text-decoration: none;
            font-weight: 500;
        }

        .form-footer a:hover {
            text-decoration: underline;
        }

        .register-prompt {
            margin-top: 2rem;
            padding-top: 2rem;
            border-top: 1px solid #e0e0e0;
            text-align: center;
            color: var(--text-light);
        }

        .register-prompt a {
            color: var(--primary-color);
            font-weight: 600;
            text-decoration: none;
        }

        .register-prompt a:hover {
            text-decoration: underline;
        }

        .back-home {
            text-align: center;
            margin-top: 1.5rem;
        }

        .back-home a {
            color: var(--text-light);
            text-decoration: none;
            font-size: 0.95rem;
        }

        .back-home a:hover {
            color: var(--primary-color);
        }

        .mb-4 {
            margin-bottom: 1.5rem;
        }

        .mt-4 {
            margin-top: 1.5rem;
        }

        /* RESPONSIVE */
        @media (max-width: 768px) {
            .split-container {
                flex-direction: column;
            }

            .left-side {
                padding: 2rem;
                min-height: 40vh;
            }

            .branding h1 {
                font-size: 2rem;
            }

            .branding p {
                font-size: 1rem;
            }

            .features-list {
                display: none;
            }

            .right-side {
                min-height: 60vh;
            }
        }
    </style>
</head>
<body>
    <div class="split-container">
        <!-- LEFT SIDE - BRANDING -->
        <div class="left-side">
            <div class="branding">
                <div class="branding-icon">
                    <i class="fas fa-calendar-star"></i>
                </div>
                <h1>EventManager</h1>
                <p>Découvrez, participez et créez des événements inoubliables</p>

                <div class="features-list">
                    <div class="feature-item">
                        <i class="fas fa-search"></i>
                        <span>Recherche d'événements facile</span>
                    </div>
                    <div class="feature-item">
                        <i class="fas fa-ticket-alt"></i>
                        <span>Réservation en un clic</span>
                    </div>
                    <div class="feature-item">
                        <i class="fas fa-users"></i>
                        <span>Communauté active</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- RIGHT SIDE - LOGIN FORM -->
        <div class="right-side">
            <div class="login-form-container">
                <div class="login-header">
                    <h2>Connexion</h2>
                    <p>Accédez à votre compte EventManager</p>
                </div>

                <!-- Error/Success Messages -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">
                        <i class="fas fa-exclamation-circle me-2"></i>${error}
                    </div>
                </c:if>

                <c:if test="${not empty success}">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle me-2"></i>${success}
                    </div>
                </c:if>

                <!-- Login Form -->
                <form action="${pageContext.request.contextPath}/login" method="POST" autocomplete="on">
                    <div class="mb-4">
                        <label for="email" class="form-label">
                            <i class="fas fa-envelope me-2"></i>Adresse email
                        </label>
                        <input type="email" class="form-control" id="email" name="email"
                               placeholder="votre@email.com" autocomplete="username" required autofocus>
                    </div>

                    <div class="mb-4">
                        <label for="password" class="form-label">
                            <i class="fas fa-lock me-2"></i>Mot de passe
                        </label>
                        <input type="password" class="form-control" id="password" name="password"
                               placeholder="Entrez votre mot de passe" autocomplete="current-password" required>
                    </div>

                    <div class="form-footer">
                        <a href="${pageContext.request.contextPath}/forgot-password">
                            <i class="fas fa-key me-1"></i>Mot de passe oublié ?
                        </a>
                    </div>

                    <button type="submit" class="btn-login mt-4">
                        <i class="fas fa-sign-in-alt me-2"></i>Se connecter
                    </button>
                </form>

                <div class="register-prompt">
                    Vous n'avez pas de compte ?
                    <a href="${pageContext.request.contextPath}/register">Créer un compte</a>
                </div>

                <div class="back-home">
                    <a href="${pageContext.request.contextPath}/catalogue">
                        <i class="fas fa-arrow-left me-1"></i>Retour à l'accueil
                    </a>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
