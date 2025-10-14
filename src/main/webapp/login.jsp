<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion - EventManager</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Neue+Plak:wght@400;600;700&display=swap" rel="stylesheet">
    
    <style>
        :root {
            --primary-color: #d1410c;
            --secondary-color: #1e0a3c;
            --accent-color: #f05537;
            --text-dark: #39364f;
            --text-light: #6f7287;
            --bg-light: #f8f7fa;
        }
        
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Neue Plak', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 2rem;
        }
        
        .login-container {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            max-width: 500px;
            width: 100%;
            overflow: hidden;
        }
        
        .login-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 3rem 2rem;
            text-align: center;
        }
        
        .login-header h1 {
            font-size: 2.5rem;
            font-weight: 700;
            margin-bottom: 0.5rem;
        }
        
        .login-header p {
            font-size: 1.1rem;
            opacity: 0.95;
        }
        
        .login-body {
            padding: 3rem 2rem;
        }
        
        .form-label {
            font-weight: 600;
            color: var(--text-dark);
            margin-bottom: 0.5rem;
        }
        
        .form-control {
            border: 2px solid var(--bg-light);
            border-radius: 10px;
            padding: 0.8rem 1rem;
            font-size: 1rem;
            transition: all 0.3s;
        }
        
        .form-control:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 3px rgba(209, 65, 12, 0.1);
        }
        
        .btn-primary-custom {
            background: var(--primary-color);
            color: white;
            border: none;
            padding: 1rem 2rem;
            border-radius: 10px;
            font-weight: 600;
            font-size: 1.1rem;
            transition: all 0.3s;
            width: 100%;
        }
        
        .btn-primary-custom:hover {
            background: var(--accent-color);
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(209, 65, 12, 0.3);
        }
        
        .alert {
            border-radius: 10px;
            border: none;
            padding: 1rem;
        }
        
        .register-link {
            text-align: center;
            margin-top: 2rem;
            color: var(--text-light);
        }
        
        .register-link a {
            color: var(--primary-color);
            font-weight: 600;
            text-decoration: none;
        }
        
        .register-link a:hover {
            text-decoration: underline;
        }
        
        .divider {
            display: flex;
            align-items: center;
            text-align: center;
            margin: 2rem 0;
            color: var(--text-light);
        }
        
        .divider::before,
        .divider::after {
            content: '';
            flex: 1;
            border-bottom: 1px solid var(--bg-light);
        }
        
        .divider span {
            padding: 0 1rem;
            font-weight: 600;
        }
        
        .social-login {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1rem;
            margin-bottom: 2rem;
        }
        
        .btn-social {
            padding: 0.8rem;
            border: 2px solid var(--bg-light);
            border-radius: 10px;
            background: white;
            color: var(--text-dark);
            font-weight: 600;
            transition: all 0.3s;
            cursor: pointer;
        }
        
        .btn-social:hover {
            border-color: var(--primary-color);
            transform: translateY(-2px);
        }
        
        .btn-social i {
            margin-right: 0.5rem;
        }
        
        .forgot-password {
            text-align: right;
            margin-top: 0.5rem;
        }
        
        .forgot-password a {
            color: var(--primary-color);
            text-decoration: none;
            font-size: 0.9rem;
        }
        
        .forgot-password a:hover {
            text-decoration: underline;
        }
        
        .back-home {
            text-align: center;
            margin-top: 1rem;
        }
        
        .back-home a {
            color: var(--text-light);
            text-decoration: none;
            font-size: 0.9rem;
        }
        
        .back-home a:hover {
            color: var(--primary-color);
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="login-header">
            <h1><i class="fas fa-calendar-star"></i> EventManager</h1>
            <p>Connectez-vous à votre compte</p>
        </div>
        
        <div class="login-body">
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
                
                <div class="mb-3">
                    <label for="password" class="form-label">
                        <i class="fas fa-lock me-2"></i>Mot de passe
                    </label>
                    <input type="password" class="form-control" id="password" name="password" 
                           placeholder="Entrez votre mot de passe" autocomplete="current-password" required>
                </div>
                
                <div class="forgot-password">
                    <a href="#"><i class="fas fa-key me-1"></i>Mot de passe oublié ?</a>
                </div>
                
                <button type="submit" class="btn-primary-custom mt-4">
                    <i class="fas fa-sign-in-alt me-2"></i>Se connecter
                </button>
            </form>
            
            <div class="divider">
                <span>OU</span>
            </div>
            
            <div class="social-login">
                <button class="btn-social" onclick="alert('Fonctionnalité à venir')">
                    <i class="fab fa-google"></i>Google
                </button>
                <button class="btn-social" onclick="alert('Fonctionnalité à venir')">
                    <i class="fab fa-facebook-f"></i>Facebook
                </button>
            </div>
            
            <div class="register-link">
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
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
