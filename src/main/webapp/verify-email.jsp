<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vérification Email - Event Manager</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <%@ include file="/WEB-INF/jspf/theme-head.jspf" %>
    <style>
        :root {
            --primary-color: #8c65a7;
            --accent-color: #8338b5;
        }
        
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--accent-color) 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .verification-container {
            max-width: 500px;
            width: 100%;
            padding: 20px;
        }
        .verification-card {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            overflow: hidden;
        }
        .card-header {
            background: linear-gradient(135deg, #c28fe4 0%, #764ba2 100%);
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
        .code-input-group {
            display: flex;
            justify-content: center;
            gap: 10px;
            margin: 30px 0;
        }
        .code-input {
            width: 50px;
            height: 60px;
            text-align: center;
            font-size: 24px;
            font-weight: bold;
            border: 2px solid #e0e0e0;
            border-radius: 10px;
            transition: all 0.3s;
        }
        .code-input:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 3px rgba(140, 101, 167, 0.1);
            outline: none;
        }
        .btn-verify {
            background: linear-gradient(135deg, #c28fe4 0%, #764ba2 100%);
            border: none;
            color: white;
            padding: 12px;
            border-radius: 10px;
            font-weight: 600;
            width: 100%;
            transition: transform 0.2s;
        }
        .btn-verify:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 20px rgba(140, 101, 167, 0.4);
        }
        .resend-link {
            color: var(--primary-color);
            text-decoration: none;
            font-weight: 500;
        }
        .resend-link:hover {
            text-decoration: underline;
        }
        .email-sent-to {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 10px;
            margin-bottom: 20px;
            text-align: center;
        }
        .timer {
            color: #dc3545;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <div class="verification-container">
        <div class="verification-card">
            <div class="card-header">
                <i class="fas fa-envelope-open-text"></i>
                <h2 class="mb-0">Vérifiez votre email</h2>
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

                <div class="email-sent-to">
                    <p class="mb-1"><small class="text-muted">Code envoyé à</small></p>
                    <p class="mb-0"><strong>${email}</strong></p>
                </div>

                <p class="text-center text-muted mb-4">
                    Entrez le code de vérification à 6 chiffres que nous avons envoyé à votre adresse email.
                </p>

                <form action="${pageContext.request.contextPath}/register" method="POST" id="verificationForm">
                    <input type="hidden" name="action" value="verify-code">
                    
                    <div class="code-input-group">
                        <input type="text" class="code-input" maxlength="1" pattern="[0-9]" required>
                        <input type="text" class="code-input" maxlength="1" pattern="[0-9]" required>
                        <input type="text" class="code-input" maxlength="1" pattern="[0-9]" required>
                        <input type="text" class="code-input" maxlength="1" pattern="[0-9]" required>
                        <input type="text" class="code-input" maxlength="1" pattern="[0-9]" required>
                        <input type="text" class="code-input" maxlength="1" pattern="[0-9]" required>
                    </div>
                    
                    <input type="hidden" name="code" id="fullCode">
                    
                    <button type="submit" class="btn btn-verify">
                        <i class="fas fa-check me-2"></i>Vérifier le code
                    </button>
                </form>

                <div class="text-center mt-4">
                    <p class="mb-2">
                        <small class="text-muted">Le code expire dans <span class="timer" id="timer">15:00</span></small>
                    </p>
                    <p class="mb-0">
                        <small>Vous n'avez pas reçu le code ? 
                            <a href="${pageContext.request.contextPath}/register" class="resend-link">
                                Renvoyer le code
                            </a>
                        </small>
                    </p>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // Code input handling
        const inputs = document.querySelectorAll('.code-input');
        const form = document.getElementById('verificationForm');
        const fullCodeInput = document.getElementById('fullCode');

        inputs.forEach((input, index) => {
            input.addEventListener('input', (e) => {
                const value = e.target.value;
                
                // Only allow digits
                if (!/^\d*$/.test(value)) {
                    e.target.value = '';
                    return;
                }
                
                // Move to next input
                if (value && index < inputs.length - 1) {
                    inputs[index + 1].focus();
                }
                
                // Update hidden field with full code
                updateFullCode();
            });
            
            input.addEventListener('keydown', (e) => {
                // Move to previous input on backspace
                if (e.key === 'Backspace' && !e.target.value && index > 0) {
                    inputs[index - 1].focus();
                }
            });
            
            // Paste handling
            input.addEventListener('paste', (e) => {
                e.preventDefault();
                const paste = e.clipboardData.getData('text');
                const digits = paste.replace(/\D/g, '').slice(0, 6);
                
                digits.split('').forEach((digit, i) => {
                    if (inputs[i]) {
                        inputs[i].value = digit;
                    }
                });
                
                if (digits.length === 6) {
                    inputs[5].focus();
                }
                
                updateFullCode();
            });
        });
        
        function updateFullCode() {
            const code = Array.from(inputs).map(input => input.value).join('');
            fullCodeInput.value = code;
        }
        
        // Timer countdown
        let timeLeft = 15 * 60; // 15 minutes in seconds
        const timerElement = document.getElementById('timer');
        
        function updateTimer() {
            const minutes = Math.floor(timeLeft / 60);
            const seconds = timeLeft % 60;
            timerElement.textContent = `${minutes}:${seconds.toString().padStart(2, '0')}`;
            
            if (timeLeft > 0) {
                timeLeft--;
                setTimeout(updateTimer, 1000);
            } else {
                timerElement.textContent = 'Expiré';
                timerElement.style.color = '#dc3545';
            }
        }
        
        updateTimer();
        
        // Auto-submit when all 6 digits are entered
        form.addEventListener('submit', (e) => {
            const code = fullCodeInput.value;
            if (code.length !== 6) {
                e.preventDefault();
                alert('Veuillez entrer les 6 chiffres du code');
            }
        });
    </script>
</body>
</html>
