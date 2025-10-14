<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription - ${evenement.titre}</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <style>
        :root {
            --primary-color: #d1410c;
            --secondary-color: #1e0a3c;
            --accent-color: #f05537;
            --text-dark: #39364f;
            --text-light: #6f7287;
            --bg-light: #f8f7fa;
            --border-color: #e8e7ed;
            --success-color: #00ab55;
        }
        
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background-color: var(--bg-light);
            color: var(--text-dark);
        }
        
        .registration-container {
            max-width: 900px;
            margin: 2rem auto;
            padding: 0 1rem;
        }
        
        .event-header {
            background: white;
            border-radius: 12px;
            padding: 2rem;
            margin-bottom: 2rem;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }
        
        .event-title {
            font-size: 2rem;
            font-weight: 700;
            color: var(--text-dark);
            margin-bottom: 1rem;
        }
        
        .event-info-item {
            display: flex;
            align-items: center;
            margin-bottom: 0.8rem;
            color: var(--text-light);
        }
        
        .event-info-item i {
            margin-right: 0.8rem;
            color: var(--primary-color);
            width: 20px;
        }
        
        .registration-form {
            background: white;
            border-radius: 12px;
            padding: 2rem;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }
        
        .form-section {
            margin-bottom: 2rem;
        }
        
        .form-section-title {
            font-size: 1.3rem;
            font-weight: 600;
            margin-bottom: 1rem;
            color: var(--text-dark);
        }
        
        .ticket-option {
            border: 2px solid var(--border-color);
            border-radius: 12px;
            padding: 1.5rem;
            margin-bottom: 1rem;
            cursor: pointer;
            transition: all 0.3s;
            position: relative;
        }
        
        .ticket-option:hover {
            border-color: var(--primary-color);
            background-color: #fff5f2;
        }
        
        .ticket-option.selected {
            border-color: var(--primary-color);
            background-color: #fff5f2;
        }
        
        .ticket-option input[type="radio"] {
            position: absolute;
            opacity: 0;
        }
        
        .ticket-name {
            font-size: 1.2rem;
            font-weight: 600;
            color: var(--text-dark);
            margin-bottom: 0.5rem;
        }
        
        .ticket-price {
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--primary-color);
            margin-bottom: 0.5rem;
        }
        
        .ticket-price.free {
            color: var(--success-color);
        }
        
        .ticket-description {
            color: var(--text-light);
            font-size: 0.95rem;
        }
        
        .quantity-selector {
            display: flex;
            align-items: center;
            gap: 1rem;
        }
        
        .quantity-btn {
            width: 40px;
            height: 40px;
            border: 2px solid var(--border-color);
            background: white;
            border-radius: 8px;
            font-size: 1.2rem;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .quantity-btn:hover {
            border-color: var(--primary-color);
            color: var(--primary-color);
        }
        
        .quantity-input {
            width: 60px;
            text-align: center;
            border: 2px solid var(--border-color);
            border-radius: 8px;
            padding: 0.5rem;
            font-size: 1.1rem;
            font-weight: 600;
        }
        
        .alert-custom {
            padding: 1rem;
            border-radius: 8px;
            margin-bottom: 1.5rem;
        }
        
        .alert-success {
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
            color: #155724;
        }
        
        .alert-danger {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
        }
        
        .alert-warning {
            background-color: #fff3cd;
            border: 1px solid #ffeaa7;
            color: #856404;
        }
        
        .summary-box {
            background: var(--bg-light);
            border-radius: 12px;
            padding: 1.5rem;
            margin-top: 2rem;
        }
        
        .summary-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.8rem;
            font-size: 1.1rem;
        }
        
        .summary-total {
            border-top: 2px solid var(--border-color);
            padding-top: 1rem;
            margin-top: 1rem;
            font-size: 1.3rem;
            font-weight: 700;
        }
        
        .btn-register {
            width: 100%;
            padding: 1rem;
            font-size: 1.2rem;
            font-weight: 600;
            background: var(--primary-color);
            color: white;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .btn-register:hover {
            background: var(--accent-color);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(209, 65, 12, 0.3);
        }
        
        .btn-register:disabled {
            background: #ccc;
            cursor: not-allowed;
            transform: none;
        }
        
        .places-remaining {
            display: inline-block;
            padding: 0.5rem 1rem;
            background: var(--success-color);
            color: white;
            border-radius: 20px;
            font-weight: 600;
            font-size: 0.9rem;
        }
        
        .places-remaining.low {
            background: #ff9800;
        }
        
        .places-remaining.very-low {
            background: #f44336;
        }
    </style>
</head>
<body>
    <div class="registration-container">
        <!-- Event Header -->
        <div class="event-header">
            <h1 class="event-title">${evenement.titre}</h1>
            <div class="event-info-item">
                <i class="far fa-calendar"></i>
                <span><fmt:formatDate value="${evenement.dateDebut}" pattern="EEEE dd MMMM yyyy" /></span>
            </div>
            <div class="event-info-item">
                <i class="far fa-clock"></i>
                <span><fmt:formatDate value="${evenement.dateDebut}" pattern="HH:mm" /> - <fmt:formatDate value="${evenement.dateFin}" pattern="HH:mm" /></span>
            </div>
            <div class="event-info-item">
                <i class="fas fa-map-marker-alt"></i>
                <span>${evenement.lieu}</span>
            </div>
            <div class="event-info-item">
                <i class="fas fa-users"></i>
                <c:choose>
                    <c:when test="${placesDisponibles > 20}">
                        <span class="places-remaining">${placesDisponibles} places disponibles</span>
                    </c:when>
                    <c:when test="${placesDisponibles > 5}">
                        <span class="places-remaining low">${placesDisponibles} places disponibles</span>
                    </c:when>
                    <c:otherwise>
                        <span class="places-remaining very-low">Seulement ${placesDisponibles} places restantes !</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Error Message -->
        <c:if test="${not empty error}">
            <div class="alert-custom alert-danger">
                <i class="fas fa-exclamation-circle me-2"></i>
                ${error}
            </div>
        </c:if>

        <!-- Registration Form -->
        <div class="registration-form">
            <form action="${pageContext.request.contextPath}/events/${evenement.id}/register" method="POST" id="registrationForm">
                
                <!-- Ticket Selection -->
                <div class="form-section">
                    <h2 class="form-section-title">Choisissez votre type de billet</h2>
                    
                    <label class="ticket-option" onclick="selectTicket('STANDARD')">
                        <input type="radio" name="typeBillet" value="STANDARD" checked>
                        <div class="ticket-name">
                            <i class="fas fa-ticket-alt me-2"></i>Billet Standard
                        </div>
                        <div class="ticket-price free">Gratuit</div>
                        <div class="ticket-description">
                            Accès complet à l'événement avec toutes les activités principales
                        </div>
                    </label>
                    
                    <label class="ticket-option" onclick="selectTicket('VIP')">
                        <input type="radio" name="typeBillet" value="VIP">
                        <div class="ticket-name">
                            <i class="fas fa-crown me-2"></i>Billet VIP
                        </div>
                        <div class="ticket-price">Gratuit</div>
                        <div class="ticket-description">
                            Accès prioritaire + Places réservées + Rencontre avec les organisateurs
                        </div>
                    </label>
                    
                    <label class="ticket-option" onclick="selectTicket('PREMIUM')">
                        <input type="radio" name="typeBillet" value="PREMIUM">
                        <div class="ticket-name">
                            <i class="fas fa-star me-2"></i>Billet Premium
                        </div>
                        <div class="ticket-price">Gratuit</div>
                        <div class="ticket-description">
                            Tous les avantages VIP + Goodies exclusifs + Accès backstage
                        </div>
                    </label>
                </div>

                <!-- Quantity Selection -->
                <div class="form-section">
                    <h2 class="form-section-title">Nombre de billets</h2>
                    <div class="quantity-selector">
                        <button type="button" class="quantity-btn" onclick="changeQuantity(-1)">
                            <i class="fas fa-minus"></i>
                        </button>
                        <input type="number" name="quantite" id="quantite" value="1" min="1" max="10" class="quantity-input" readonly>
                        <button type="button" class="quantity-btn" onclick="changeQuantity(1)">
                            <i class="fas fa-plus"></i>
                        </button>
                        <span class="text-muted">(Maximum 10 billets par commande)</span>
                    </div>
                </div>

                <!-- Summary -->
                <div class="summary-box">
                    <h3 class="form-section-title">Récapitulatif</h3>
                    <div class="summary-row">
                        <span>Type de billet:</span>
                        <span id="summaryTicketType">Standard</span>
                    </div>
                    <div class="summary-row">
                        <span>Quantité:</span>
                        <span id="summaryQuantity">1</span>
                    </div>
                    <div class="summary-row summary-total">
                        <span>Total:</span>
                        <span class="text-success">Gratuit</span>
                    </div>
                </div>

                <!-- Submit Button -->
                <button type="submit" class="btn-register mt-4">
                    <i class="fas fa-check-circle me-2"></i>Confirmer l'inscription
                </button>
            </form>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // Sélection du type de billet
        function selectTicket(type) {
            document.querySelectorAll('.ticket-option').forEach(option => {
                option.classList.remove('selected');
            });
            event.currentTarget.classList.add('selected');
            
            // Mettre à jour le récapitulatif
            let typeName = type;
            if (type === 'STANDARD') typeName = 'Standard';
            else if (type === 'VIP') typeName = 'VIP';
            else if (type === 'PREMIUM') typeName = 'Premium';
            
            document.getElementById('summaryTicketType').textContent = typeName;
        }
        
        // Gestion de la quantité
        function changeQuantity(delta) {
            const input = document.getElementById('quantite');
            let value = parseInt(input.value) + delta;
            
            // Limiter entre 1 et 10
            value = Math.max(1, Math.min(10, value));
            
            input.value = value;
            document.getElementById('summaryQuantity').textContent = value;
        }
        
        // Initialiser la première option comme sélectionnée
        document.addEventListener('DOMContentLoaded', function() {
            document.querySelector('.ticket-option').classList.add('selected');
        });
    </script>
</body>
</html>
