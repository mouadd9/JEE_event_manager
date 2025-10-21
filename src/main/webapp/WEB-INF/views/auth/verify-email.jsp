<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vérification Email - Event Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .verify-container {
            max-width: 500px;
            margin: 80px auto;
            padding: 40px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }

        .verify-header {
            text-align: center;
            margin-bottom: 30px;
        }

        .verify-header .icon {
            font-size: 60px;
            color: #667eea;
            margin-bottom: 20px;
        }

        .verify-header h1 {
            font-size: 24px;
            color: #333;
            margin-bottom: 10px;
        }

        .verify-header p {
            color: #666;
            line-height: 1.6;
        }

        .email-display {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 5px;
            text-align: center;
            margin: 20px 0;
            font-weight: 600;
            color: #667eea;
        }

        .code-input-group {
            margin: 30px 0;
        }

        .code-input-group label {
            display: block;
            margin-bottom: 10px;
            color: #333;
            font-weight: 600;
        }

        .code-input {
            width: 100%;
            padding: 15px;
            font-size: 24px;
            text-align: center;
            letter-spacing: 10px;
            border: 2px solid #ddd;
            border-radius: 5px;
            font-family: monospace;
            transition: border-color 0.3s;
        }

        .code-input:focus {
            border-color: #667eea;
            outline: none;
        }

        .btn-verify {
            width: 100%;
            padding: 15px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s;
        }

        .btn-verify:hover {
            transform: translateY(-2px);
        }

        .resend-section {
            text-align: center;
            margin-top: 30px;
            padding-top: 30px;
            border-top: 1px solid #eee;
        }

        .resend-section p {
            color: #666;
            margin-bottom: 15px;
        }

        .btn-resend {
            background: #6c757d;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            font-weight: 600;
            cursor: pointer;
            transition: background 0.3s;
        }

        .btn-resend:hover {
            background: #5a6268;
        }

        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
            font-weight: 500;
        }

        .alert-danger {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .help-text {
            text-align: center;
            color: #999;
            font-size: 14px;
            margin-top: 20px;
        }

        .back-link {
            text-align: center;
            margin-top: 20px;
        }

        .back-link a {
            color: #667eea;
            text-decoration: none;
            font-weight: 600;
        }

        .back-link a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="verify-container">
        <div class="verify-header">
            <div class="icon">📧</div>
            <h1>Vérification de votre email</h1>
            <p>Nous avons envoyé un code de vérification à votre adresse email</p>
        </div>

        <c:if test="${not empty email}">
            <div class="email-display">
                ${email}
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                ${errorMessage}
            </div>
        </c:if>

        <c:if test="${not empty successMessage}">
            <div class="alert alert-success">
                ${successMessage}
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/verify-email">
            <div class="code-input-group">
                <label for="code">Entrez le code de vérification :</label>
                <input 
                    type="text" 
                    id="code" 
                    name="code" 
                    class="code-input" 
                    maxlength="6" 
                    pattern="[0-9]{6}"
                    placeholder="000000"
                    required
                    autofocus
                    oninput="this.value = this.value.replace(/[^0-9]/g, '')">
            </div>

            <button type="submit" class="btn-verify">
                Vérifier mon email
            </button>
        </form>

        <div class="resend-section">
            <p>Vous n'avez pas reçu le code ?</p>
            <form method="post" action="${pageContext.request.contextPath}/verify-email" style="display: inline;">
                <input type="hidden" name="action" value="resend">
                <button type="submit" class="btn-resend">
                    Renvoyer le code
                </button>
            </form>
        </div>

        <p class="help-text">
            Le code est valable pendant 15 minutes
        </p>

        <div class="back-link">
            <a href="${pageContext.request.contextPath}/login">
                ← Retour à la connexion
            </a>
        </div>
    </div>

    <script>
        // Auto-format code input
        const codeInput = document.getElementById('code');
        codeInput.addEventListener('input', function(e) {
            // Only allow numbers
            this.value = this.value.replace(/[^0-9]/g, '');
            
            // Auto-submit when 6 digits entered
            if (this.value.length === 6) {
                // Optional: auto-submit
                // this.form.submit();
            }
        });
    </script>
</body>
</html>
