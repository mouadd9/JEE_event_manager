<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Créer un compte - EventManager</title>
    
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
        
        .register-container {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            max-width: 900px;
            width: 100%;
            overflow: hidden;
        }
        
        .register-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 3rem 2rem;
            text-align: center;
        }
        
        .register-header h1 {
            font-size: 2.5rem;
            font-weight: 700;
            margin-bottom: 0.5rem;
        }
        
        .register-header p {
            font-size: 1.1rem;
            opacity: 0.95;
        }
        
        .register-body {
            padding: 3rem 2rem;
        }
        
        .step-indicator {
            display: flex;
            justify-content: center;
            margin-bottom: 3rem;
            position: relative;
        }
        
        .step {
            display: flex;
            flex-direction: column;
            align-items: center;
            position: relative;
            flex: 1;
            max-width: 200px;
        }
        
        .step-number {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background: var(--bg-light);
            color: var(--text-light);
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            font-size: 1.2rem;
            margin-bottom: 0.5rem;
            transition: all 0.3s;
            z-index: 2;
        }
        
        .step.active .step-number {
            background: var(--primary-color);
            color: white;
            transform: scale(1.1);
        }
        
        .step.completed .step-number {
            background: #00ab55;
            color: white;
        }
        
        .step-label {
            font-size: 0.9rem;
            color: var(--text-light);
            font-weight: 600;
        }
        
        .step.active .step-label {
            color: var(--primary-color);
        }
        
        .step-line {
            position: absolute;
            top: 25px;
            left: 50%;
            right: -50%;
            height: 3px;
            background: var(--bg-light);
            z-index: 1;
        }
        
        .step:last-child .step-line {
            display: none;
        }
        
        .step.completed .step-line {
            background: #00ab55;
        }
        
        .form-section {
            display: none;
        }
        
        .form-section.active {
            display: block;
            animation: fadeIn 0.3s;
        }
        
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
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
        
        .role-card {
            border: 3px solid var(--bg-light);
            border-radius: 15px;
            padding: 2rem;
            cursor: pointer;
            transition: all 0.3s;
            text-align: center;
            height: 100%;
        }
        
        .role-card:hover {
            border-color: var(--primary-color);
            transform: translateY(-5px);
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }
        
        .role-card.selected {
            border-color: var(--primary-color);
            background: rgba(209, 65, 12, 0.05);
        }
        
        .role-card input[type="radio"] {
            display: none;
        }
        
        .role-icon {
            font-size: 3rem;
            color: var(--primary-color);
            margin-bottom: 1rem;
        }
        
        .role-title {
            font-size: 1.3rem;
            font-weight: 700;
            color: var(--text-dark);
            margin-bottom: 0.5rem;
        }
        
        .role-description {
            color: var(--text-light);
            font-size: 0.95rem;
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
        
        .btn-secondary-custom {
            background: white;
            color: var(--primary-color);
            border: 2px solid var(--primary-color);
            padding: 1rem 2rem;
            border-radius: 10px;
            font-weight: 600;
            font-size: 1.1rem;
            transition: all 0.3s;
            width: 100%;
        }
        
        .btn-secondary-custom:hover {
            background: var(--bg-light);
        }
        
        .alert {
            border-radius: 10px;
            border: none;
            padding: 1rem;
        }
        
        .login-link {
            text-align: center;
            margin-top: 2rem;
            color: var(--text-light);
        }
        
        .login-link a {
            color: var(--primary-color);
            font-weight: 600;
            text-decoration: none;
        }
        
        .login-link a:hover {
            text-decoration: underline;
        }
        
        .password-strength {
            height: 5px;
            border-radius: 3px;
            margin-top: 0.5rem;
            background: var(--bg-light);
            overflow: hidden;
        }
        
        .password-strength-bar {
            height: 100%;
            width: 0%;
            transition: all 0.3s;
        }
        
        .password-strength-bar.weak {
            width: 33%;
            background: #ff4842;
        }
        
        .password-strength-bar.medium {
            width: 66%;
            background: #ffa726;
        }
        
        .password-strength-bar.strong {
            width: 100%;
            background: #00ab55;
        }
        
        .password-requirements {
            font-size: 0.85rem;
            color: var(--text-light);
            margin-top: 0.5rem;
        }
        
        .password-requirements li {
            margin-bottom: 0.3rem;
        }
        
        .password-requirements li.valid {
            color: #00ab55;
        }
        
        .password-requirements li.valid i {
            color: #00ab55;
        }
    </style>
</head>
<body>
    <div class="register-container">
        <div class="register-header">
            <h1><i class="fas fa-calendar-star"></i> EventManager</h1>
            <p>Créez votre compte et commencez votre aventure</p>
        </div>
        
        <div class="register-body">
            <!-- Step Indicator -->
            <div class="step-indicator">
                <div class="step active" id="step1-indicator">
                    <div class="step-number">1</div>
                    <div class="step-label">Informations</div>
                    <div class="step-line"></div>
                </div>
                <div class="step" id="step2-indicator">
                    <div class="step-number">2</div>
                    <div class="step-label">Type de compte</div>
                </div>
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
            
            <!-- Registration Form -->
            <form id="registerForm" action="${pageContext.request.contextPath}/register" method="POST">
                <!-- Step 1: Basic Information -->
                <div class="form-section active" id="step1">
                    <div class="mb-4">
                        <label for="nom" class="form-label">
                            <i class="fas fa-user me-2"></i>Nom complet
                        </label>
                        <input type="text" class="form-control" id="nom" name="nom" 
                               placeholder="Entrez votre nom complet" required>
                    </div>
                    
                    <div class="mb-4">
                        <label for="email" class="form-label">
                            <i class="fas fa-envelope me-2"></i>Adresse email
                        </label>
                        <input type="email" class="form-control" id="email" name="email" 
                               placeholder="votre@email.com" required>
                    </div>
                    
                    <div class="mb-4">
                        <label for="password" class="form-label">
                            <i class="fas fa-lock me-2"></i>Mot de passe
                        </label>
                        <input type="password" class="form-control" id="password" name="password" 
                               placeholder="Créez un mot de passe sécurisé" required>
                        <div class="password-strength">
                            <div class="password-strength-bar" id="strengthBar"></div>
                        </div>
                        <ul class="password-requirements mt-2">
                            <li id="length-req"><i class="far fa-circle"></i> Au moins 8 caractères</li>
                            <li id="uppercase-req"><i class="far fa-circle"></i> Une lettre majuscule</li>
                            <li id="number-req"><i class="far fa-circle"></i> Un chiffre</li>
                        </ul>
                    </div>
                    
                    <div class="mb-4">
                        <label for="confirmPassword" class="form-label">
                            <i class="fas fa-lock me-2"></i>Confirmer le mot de passe
                        </label>
                        <input type="password" class="form-control" id="confirmPassword" 
                               placeholder="Confirmez votre mot de passe" required>
                        <small class="text-danger d-none" id="password-match-error">
                            Les mots de passe ne correspondent pas
                        </small>
                    </div>
                    
                    <button type="button" class="btn-primary-custom" onclick="nextStep()">
                        Continuer <i class="fas fa-arrow-right ms-2"></i>
                    </button>
                </div>
                
                <!-- Step 2: Choose Role -->
                <div class="form-section" id="step2">
                    <h3 class="text-center mb-4" style="color: var(--text-dark); font-weight: 700;">
                        Que souhaitez-vous faire ?
                    </h3>
                    
                    <div class="row g-4 mb-4">
                        <div class="col-md-6">
                            <label class="role-card" for="roleParticipant">
                                <input type="radio" name="userType" id="roleParticipant" value="PARTICIPANT" required>
                                <div class="role-icon">
                                    <i class="fas fa-ticket-alt"></i>
                                </div>
                                <div class="role-title">Trouver une expérience</div>
                                <div class="role-description">
                                    Découvrez et participez à des événements incroyables
                                </div>
                            </label>
                        </div>
                        
                        <div class="col-md-6">
                            <label class="role-card" for="roleOrganisateur">
                                <input type="radio" name="userType" id="roleOrganisateur" value="ORGANISATEUR" required>
                                <div class="role-icon">
                                    <i class="fas fa-calendar-plus"></i>
                                </div>
                                <div class="role-title">Organiser un événement</div>
                                <div class="role-description">
                                    Créez et gérez vos propres événements
                                </div>
                            </label>
                        </div>
                    </div>
                    
                    <div class="row g-3">
                        <div class="col-md-6">
                            <button type="button" class="btn-secondary-custom" onclick="prevStep()">
                                <i class="fas fa-arrow-left me-2"></i>Retour
                            </button>
                        </div>
                        <div class="col-md-6">
                            <button type="submit" class="btn-primary-custom">
                                Créer mon compte <i class="fas fa-check ms-2"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </form>
            
            <div class="login-link">
                Vous avez déjà un compte ? 
                <a href="${pageContext.request.contextPath}/login">Se connecter</a>
            </div>
        </div>
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        let currentStep = 1;
        
        // Role card selection
        document.querySelectorAll('.role-card').forEach(card => {
            card.addEventListener('click', function() {
                document.querySelectorAll('.role-card').forEach(c => c.classList.remove('selected'));
                this.classList.add('selected');
                this.querySelector('input[type="radio"]').checked = true;
            });
        });
        
        // Password strength checker
        const passwordInput = document.getElementById('password');
        const strengthBar = document.getElementById('strengthBar');
        
        passwordInput.addEventListener('input', function() {
            const password = this.value;
            let strength = 0;
            
            // Check length
            const lengthReq = document.getElementById('length-req');
            if (password.length >= 8) {
                strength++;
                lengthReq.classList.add('valid');
                lengthReq.querySelector('i').className = 'fas fa-check-circle';
            } else {
                lengthReq.classList.remove('valid');
                lengthReq.querySelector('i').className = 'far fa-circle';
            }
            
            // Check uppercase
            const uppercaseReq = document.getElementById('uppercase-req');
            if (/[A-Z]/.test(password)) {
                strength++;
                uppercaseReq.classList.add('valid');
                uppercaseReq.querySelector('i').className = 'fas fa-check-circle';
            } else {
                uppercaseReq.classList.remove('valid');
                uppercaseReq.querySelector('i').className = 'far fa-circle';
            }
            
            // Check number
            const numberReq = document.getElementById('number-req');
            if (/[0-9]/.test(password)) {
                strength++;
                numberReq.classList.add('valid');
                numberReq.querySelector('i').className = 'fas fa-check-circle';
            } else {
                numberReq.classList.remove('valid');
                numberReq.querySelector('i').className = 'far fa-circle';
            }
            
            // Update strength bar
            strengthBar.className = 'password-strength-bar';
            if (strength === 1) {
                strengthBar.classList.add('weak');
            } else if (strength === 2) {
                strengthBar.classList.add('medium');
            } else if (strength === 3) {
                strengthBar.classList.add('strong');
            }
        });
        
        // Password match checker
        const confirmPasswordInput = document.getElementById('confirmPassword');
        const passwordMatchError = document.getElementById('password-match-error');
        
        confirmPasswordInput.addEventListener('input', function() {
            if (this.value !== passwordInput.value) {
                passwordMatchError.classList.remove('d-none');
            } else {
                passwordMatchError.classList.add('d-none');
            }
        });
        
        // Step navigation
        function nextStep() {
            // Validate step 1
            const nom = document.getElementById('nom').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            if (!nom || !email || !password || !confirmPassword) {
                alert('Veuillez remplir tous les champs');
                return;
            }
            
            if (password !== confirmPassword) {
                alert('Les mots de passe ne correspondent pas');
                return;
            }
            
            if (password.length < 8) {
                alert('Le mot de passe doit contenir au moins 8 caractères');
                return;
            }
            
            // Move to step 2
            currentStep = 2;
            document.getElementById('step1').classList.remove('active');
            document.getElementById('step2').classList.add('active');
            
            document.getElementById('step1-indicator').classList.remove('active');
            document.getElementById('step1-indicator').classList.add('completed');
            document.getElementById('step2-indicator').classList.add('active');
        }
        
        function prevStep() {
            currentStep = 1;
            document.getElementById('step2').classList.remove('active');
            document.getElementById('step1').classList.add('active');
            
            document.getElementById('step2-indicator').classList.remove('active');
            document.getElementById('step1-indicator').classList.remove('completed');
            document.getElementById('step1-indicator').classList.add('active');
        }
        
        // Form validation before submit
        document.getElementById('registerForm').addEventListener('submit', function(e) {
            const userType = document.querySelector('input[name="userType"]:checked');
            if (!userType) {
                e.preventDefault();
                alert('Veuillez choisir un type de compte');
            }
        });
    </script>
</body>
</html>
