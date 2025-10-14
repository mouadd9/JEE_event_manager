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
            --eb-orange: #f05537;
            --eb-orange-dark: #d1410c;
            --eb-purple: #3659e3;
            --eb-dark: #1e0a3c;
            --eb-gray: #6f7287;
            --eb-light-gray: #f8f7fa;
            --eb-border: #d1d5db;
        }
        
        body {
            font-family: 'Neue Plak', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background-color: var(--eb-light-gray);
            color: var(--eb-dark);
        }
        
        .checkout-container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 1rem;
        }
        
        .checkout-header {
            background: white;
            padding: 1.5rem;
            border-radius: 8px;
            margin-bottom: 2rem;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        
        .event-mini-info {
            display: flex;
            align-items: center;
            gap: 1rem;
        }
        
        .event-mini-image {
            width: 80px;
            height: 80px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 8px;
        }
        
        .event-mini-details h5 {
            margin: 0;
            font-size: 1.2rem;
            font-weight: 600;
        }
        
        .event-mini-details p {
            margin: 0.3rem 0 0 0;
            color: var(--eb-gray);
            font-size: 0.9rem;
        }
        
        .checkout-main {
            display: grid;
            grid-template-columns: 1fr 400px;
            gap: 2rem;
        }
        
        .checkout-left {
            background: white;
            padding: 2rem;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        
        .checkout-right {
            position: sticky;
            top: 2rem;
            height: fit-content;
        }
        
        .section-title {
            font-size: 1.5rem;
            font-weight: 700;
            margin-bottom: 1.5rem;
            color: var(--eb-dark);
        }
        
        .step-indicator {
            display: flex;
            align-items: center;
            margin-bottom: 2rem;
        }
        
        .step {
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .step-number {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            background: var(--eb-border);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 600;
        }
        
        .step.active .step-number {
            background: var(--eb-orange);
        }
        
        .step.completed .step-number {
            background: #10b981;
        }
        
        .step-divider {
            width: 60px;
            height: 2px;
            background: var(--eb-border);
            margin: 0 1rem;
        }
        
        /* Ticket Selection */
        .ticket-card {
            border: 2px solid var(--eb-border);
            border-radius: 8px;
            padding: 1.5rem;
            margin-bottom: 1rem;
            transition: all 0.3s;
        }
        
        .ticket-card:hover {
            border-color: var(--eb-orange);
            box-shadow: 0 4px 12px rgba(240, 85, 55, 0.15);
        }
        
        .ticket-header {
            display: flex;
            justify-content: space-between;
            align-items: start;
            margin-bottom: 1rem;
        }
        
        .ticket-name {
            font-size: 1.2rem;
            font-weight: 600;
            color: var(--eb-dark);
        }
        
        .ticket-price {
            font-size: 1.3rem;
            font-weight: 700;
            color: var(--eb-orange);
        }
        
        .ticket-description {
            color: var(--eb-gray);
            margin-bottom: 1rem;
            font-size: 0.95rem;
        }
        
        .ticket-quantity {
            display: flex;
            align-items: center;
            gap: 1rem;
        }
        
        .qty-label {
            font-weight: 500;
            color: var(--eb-gray);
        }
        
        .qty-controls {
            display: flex;
            align-items: center;
            border: 1px solid var(--eb-border);
            border-radius: 6px;
            overflow: hidden;
        }
        
        .qty-btn {
            width: 36px;
            height: 36px;
            border: none;
            background: white;
            cursor: pointer;
            font-size: 1.2rem;
            color: var(--eb-gray);
            transition: all 0.2s;
        }
        
        .qty-btn:hover:not(:disabled) {
            background: var(--eb-light-gray);
            color: var(--eb-orange);
        }
        
        .qty-btn:disabled {
            opacity: 0.3;
            cursor: not-allowed;
        }
        
        .qty-input {
            width: 50px;
            height: 36px;
            border: none;
            border-left: 1px solid var(--eb-border);
            border-right: 1px solid var(--eb-border);
            text-align: center;
            font-weight: 600;
            font-size: 1rem;
        }
        
        /* Attendee Info Form */
        .form-group {
            margin-bottom: 1.5rem;
        }
        
        .form-label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
            color: var(--eb-dark);
        }
        
        .form-label .required {
            color: #ef4444;
        }
        
        .form-control {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid var(--eb-border);
            border-radius: 6px;
            font-size: 1rem;
            transition: all 0.2s;
        }
        
        .form-control:focus {
            outline: none;
            border-color: var(--eb-orange);
            box-shadow: 0 0 0 3px rgba(240, 85, 55, 0.1);
        }
        
        .form-control.error {
            border-color: #ef4444;
        }
        
        .error-message {
            color: #ef4444;
            font-size: 0.875rem;
            margin-top: 0.25rem;
        }
        
        /* Waitlist */
        .waitlist-banner {
            background: #fef3c7;
            border: 2px solid #fbbf24;
            border-radius: 8px;
            padding: 1.5rem;
            margin-bottom: 1.5rem;
        }
        
        .waitlist-banner h4 {
            color: #92400e;
            font-size: 1.1rem;
            font-weight: 600;
            margin-bottom: 0.5rem;
        }
        
        .waitlist-banner p {
            color: #78350f;
            margin-bottom: 1rem;
        }
        
        .waitlist-checkbox {
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .waitlist-checkbox input {
            width: 20px;
            height: 20px;
        }
        
        /* Order Summary */
        .order-summary {
            background: white;
            padding: 1.5rem;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        
        .summary-title {
            font-size: 1.3rem;
            font-weight: 700;
            margin-bottom: 1.5rem;
        }
        
        .summary-row {
            display: flex;
            justify-content: space-between;
            padding: 0.75rem 0;
            border-bottom: 1px solid var(--eb-border);
        }
        
        .summary-row:last-child {
            border-bottom: none;
        }
        
        .summary-total {
            font-size: 1.2rem;
            font-weight: 700;
            padding-top: 1rem;
            margin-top: 1rem;
            border-top: 2px solid var(--eb-dark);
        }
        
        /* Buttons */
        .btn-primary-eb {
            width: 100%;
            padding: 1rem;
            background: var(--eb-orange);
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 1.1rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .btn-primary-eb:hover:not(:disabled) {
            background: var(--eb-orange-dark);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(240, 85, 55, 0.3);
        }
        
        .btn-primary-eb:disabled {
            background: #d1d5db;
            cursor: not-allowed;
            transform: none;
        }
        
        .btn-secondary-eb {
            width: 100%;
            padding: 1rem;
            background: white;
            color: var(--eb-dark);
            border: 2px solid var(--eb-border);
            border-radius: 6px;
            font-size: 1.1rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            margin-top: 1rem;
        }
        
        .btn-secondary-eb:hover {
            border-color: var(--eb-orange);
            color: var(--eb-orange);
        }
        
        /* Payment Stub */
        .payment-section {
            margin-top: 2rem;
            padding-top: 2rem;
            border-top: 2px solid var(--eb-border);
        }
        
        .payment-stub {
            background: var(--eb-light-gray);
            padding: 1.5rem;
            border-radius: 8px;
            text-align: center;
        }
        
        .payment-stub i {
            font-size: 3rem;
            color: var(--eb-gray);
            margin-bottom: 1rem;
        }
        
        .payment-stub p {
            color: var(--eb-gray);
            margin-bottom: 1rem;
        }
        
        @media (max-width: 992px) {
            .checkout-main {
                grid-template-columns: 1fr;
            }
            
            .checkout-right {
                position: static;
            }
        }
    </style>
</head>
<body>
    <div class="checkout-container">
        <!-- Header -->
        <div class="checkout-header">
            <div class="event-mini-info">
                <div class="event-mini-image"></div>
                <div class="event-mini-details">
                    <h5>${evenement.titre}</h5>
                    <p>
                        <i class="far fa-calendar me-2"></i>
                        <fmt:formatDate value="${evenement.dateDebut}" pattern="EEE, dd MMM yyyy à HH:mm" />
                    </p>
                    <p>
                        <i class="fas fa-map-marker-alt me-2"></i>
                        ${evenement.lieu}
                    </p>
                </div>
            </div>
        </div>

        <div class="checkout-main">
            <!-- Left Column -->
            <div class="checkout-left">
                <!-- Step Indicator -->
                <div class="step-indicator">
                    <div class="step ${empty step or step == 'tickets' ? 'active' : 'completed'}">
                        <div class="step-number">1</div>
                        <span>Billets</span>
                    </div>
                    <div class="step-divider"></div>
                    <div class="step ${step == 'info' ? 'active' : ''}">
                        <div class="step-number">2</div>
                        <span>Informations</span>
                    </div>
                    <div class="step-divider"></div>
                    <div class="step ${step == 'payment' ? 'active' : ''}">
                        <div class="step-number">3</div>
                        <span>Paiement</span>
                    </div>
                </div>

                <!-- Error Message -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        <i class="fas fa-exclamation-circle me-2"></i>
                        ${error}
                    </div>
                </c:if>

                <!-- Waitlist Banner -->
                <c:if test="${placesDisponibles <= 0}">
                    <div class="waitlist-banner">
                        <h4><i class="fas fa-clock me-2"></i>Événement complet</h4>
                        <p>Cet événement est actuellement complet. Rejoignez la liste d'attente pour être notifié si des places se libèrent.</p>
                        <div class="waitlist-checkbox">
                            <input type="checkbox" id="joinWaitlist" name="joinWaitlist">
                            <label for="joinWaitlist">Oui, ajoutez-moi à la liste d'attente</label>
                        </div>
                    </div>
                </c:if>

                <!-- Step 1: Select Tickets -->
                <c:if test="${empty step or step == 'tickets'}">
                    <h2 class="section-title">Sélectionnez vos billets</h2>
                    
                    <form action="${pageContext.request.contextPath}/events/${evenement.id}/register" method="POST" id="ticketForm">
                        <input type="hidden" name="step" value="tickets">
                        
                        <!-- Standard Ticket -->
                        <div class="ticket-card">
                            <div class="ticket-header">
                                <div>
                                    <div class="ticket-name">
                                        <i class="fas fa-ticket-alt me-2"></i>Billet Standard
                                    </div>
                                    <div class="ticket-price">Gratuit</div>
                                </div>
                            </div>
                            <div class="ticket-description">
                                Accès complet à l'événement avec toutes les activités principales
                            </div>
                            <div class="ticket-quantity">
                                <span class="qty-label">Quantité:</span>
                                <div class="qty-controls">
                                    <button type="button" class="qty-btn" onclick="changeQty('standard', -1)">
                                        <i class="fas fa-minus"></i>
                                    </button>
                                    <input type="number" class="qty-input" id="qty-standard" name="qty-standard" value="0" min="0" max="${placesDisponibles}" readonly>
                                    <button type="button" class="qty-btn" onclick="changeQty('standard', 1)">
                                        <i class="fas fa-plus"></i>
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- VIP Ticket -->
                        <div class="ticket-card">
                            <div class="ticket-header">
                                <div>
                                    <div class="ticket-name">
                                        <i class="fas fa-crown me-2"></i>Billet VIP
                                    </div>
                                    <div class="ticket-price">Gratuit</div>
                                </div>
                            </div>
                            <div class="ticket-description">
                                Accès prioritaire + Places réservées + Rencontre avec les organisateurs
                            </div>
                            <div class="ticket-quantity">
                                <span class="qty-label">Quantité:</span>
                                <div class="qty-controls">
                                    <button type="button" class="qty-btn" onclick="changeQty('vip', -1)">
                                        <i class="fas fa-minus"></i>
                                    </button>
                                    <input type="number" class="qty-input" id="qty-vip" name="qty-vip" value="0" min="0" max="${placesDisponibles}" readonly>
                                    <button type="button" class="qty-btn" onclick="changeQty('vip', 1)">
                                        <i class="fas fa-plus"></i>
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- Premium Ticket -->
                        <div class="ticket-card">
                            <div class="ticket-header">
                                <div>
                                    <div class="ticket-name">
                                        <i class="fas fa-star me-2"></i>Billet Premium
                                    </div>
                                    <div class="ticket-price">Gratuit</div>
                                </div>
                            </div>
                            <div class="ticket-description">
                                Tous les avantages VIP + Goodies exclusifs + Accès backstage
                            </div>
                            <div class="ticket-quantity">
                                <span class="qty-label">Quantité:</span>
                                <div class="qty-controls">
                                    <button type="button" class="qty-btn" onclick="changeQty('premium', -1)">
                                        <i class="fas fa-minus"></i>
                                    </button>
                                    <input type="number" class="qty-input" id="qty-premium" name="qty-premium" value="0" min="0" max="${placesDisponibles}" readonly>
                                    <button type="button" class="qty-btn" onclick="changeQty('premium', 1)">
                                        <i class="fas fa-plus"></i>
                                    </button>
                                </div>
                            </div>
                        </div>

                        <button type="submit" class="btn-primary-eb" id="continueBtn" disabled>
                            Continuer <i class="fas fa-arrow-right ms-2"></i>
                        </button>
                    </form>
                </c:if>

                <!-- Step 2: Attendee Info -->
                <c:if test="${step == 'info'}">
                    <h2 class="section-title">Informations du participant</h2>
                    
                    <form action="${pageContext.request.contextPath}/events/${evenement.id}/register" method="POST" id="attendeeForm">
                        <input type="hidden" name="step" value="info">
                        <input type="hidden" name="typeBillet" value="${selectedTicketType}">
                        <input type="hidden" name="quantite" value="${selectedQuantity}">
                        
                        <div class="form-group">
                            <label class="form-label">
                                Nom complet <span class="required">*</span>
                            </label>
                            <input type="text" class="form-control" id="nom" name="nom" value="${userName}" required>
                            <div class="error-message" id="nom-error"></div>
                        </div>

                        <div class="form-group">
                            <label class="form-label">
                                Email <span class="required">*</span>
                            </label>
                            <input type="email" class="form-control" id="email" name="email" value="${userEmail}" required>
                            <div class="error-message" id="email-error"></div>
                        </div>

                        <div class="form-group">
                            <label class="form-label">
                                Téléphone (optionnel)
                            </label>
                            <input type="tel" class="form-control" id="telephone" name="telephone" placeholder="+33 6 12 34 56 78">
                            <div class="error-message" id="telephone-error"></div>
                        </div>

                        <div class="form-group">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="newsletter" name="newsletter">
                                <label class="form-check-label" for="newsletter">
                                    Recevoir les actualités et événements similaires
                                </label>
                            </div>
                        </div>

                        <button type="submit" class="btn-primary-eb">
                            Continuer vers le paiement <i class="fas fa-arrow-right ms-2"></i>
                        </button>
                        <button type="button" class="btn-secondary-eb" onclick="history.back()">
                            <i class="fas fa-arrow-left me-2"></i>Retour
                        </button>
                    </form>
                </c:if>

                <!-- Step 3: Payment Stub -->
                <c:if test="${step == 'payment'}">
                    <h2 class="section-title">Paiement</h2>
                    
                    <div class="payment-stub">
                        <i class="fas fa-credit-card"></i>
                        <h4>Intégration de paiement à venir</h4>
                        <p>Cette fonctionnalité sera disponible prochainement avec l'intégration d'un système de paiement sécurisé.</p>
                        <p class="text-muted">Pour l'instant, les événements sont gratuits.</p>
                    </div>

                    <form action="${pageContext.request.contextPath}/events/${evenement.id}/register" method="POST">
                        <input type="hidden" name="step" value="confirm">
                        <input type="hidden" name="typeBillet" value="${selectedTicketType}">
                        <input type="hidden" name="quantite" value="${selectedQuantity}">
                        
                        <button type="submit" class="btn-primary-eb mt-4">
                            <i class="fas fa-check-circle me-2"></i>Finaliser l'inscription
                        </button>
                        <button type="button" class="btn-secondary-eb" onclick="history.back()">
                            <i class="fas fa-arrow-left me-2"></i>Retour
                        </button>
                    </form>
                </c:if>
            </div>

            <!-- Right Column: Order Summary -->
            <div class="checkout-right">
                <div class="order-summary">
                    <h3 class="summary-title">Récapitulatif</h3>
                    
                    <div class="summary-row">
                        <span>Places disponibles:</span>
                        <strong>${placesDisponibles}</strong>
                    </div>
                    
                    <div id="orderDetails">
                        <!-- Dynamically populated -->
                    </div>
                    
                    <div class="summary-row summary-total">
                        <span>Total:</span>
                        <span class="text-success">Gratuit</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        const maxTickets = ${placesDisponibles};
        let tickets = {
            standard: 0,
            vip: 0,
            premium: 0
        };

        function changeQty(type, delta) {
            const input = document.getElementById('qty-' + type);
            let newValue = parseInt(input.value) + delta;
            
            // Calculate total tickets
            let total = Object.values(tickets).reduce((a, b) => a + b, 0) - tickets[type] + newValue;
            
            // Validate
            if (newValue < 0) newValue = 0;
            if (newValue > 10) newValue = 10;
            if (total > maxTickets) return;
            
            tickets[type] = newValue;
            input.value = newValue;
            
            updateSummary();
            updateContinueButton();
        }

        function updateSummary() {
            const orderDetails = document.getElementById('orderDetails');
            let html = '';
            
            if (tickets.standard > 0) {
                html += `<div class="summary-row"><span>Standard x ${tickets.standard}</span><span>Gratuit</span></div>`;
            }
            if (tickets.vip > 0) {
                html += `<div class="summary-row"><span>VIP x ${tickets.vip}</span><span>Gratuit</span></div>`;
            }
            if (tickets.premium > 0) {
                html += `<div class="summary-row"><span>Premium x ${tickets.premium}</span><span>Gratuit</span></div>`;
            }
            
            orderDetails.innerHTML = html;
        }

        function updateContinueButton() {
            const total = Object.values(tickets).reduce((a, b) => a + b, 0);
            document.getElementById('continueBtn').disabled = total === 0;
        }

        // Form validation
        $(document).ready(function() {
            $('#attendeeForm').on('submit', function(e) {
                let isValid = true;
                
                // Validate name
                const nom = $('#nom').val().trim();
                if (nom.length < 2) {
                    $('#nom').addClass('error');
                    $('#nom-error').text('Le nom doit contenir au moins 2 caractères');
                    isValid = false;
                } else {
                    $('#nom').removeClass('error');
                    $('#nom-error').text('');
                }
                
                // Validate email
                const email = $('#email').val().trim();
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (!emailRegex.test(email)) {
                    $('#email').addClass('error');
                    $('#email-error').text('Veuillez entrer une adresse email valide');
                    isValid = false;
                } else {
                    $('#email').removeClass('error');
                    $('#email-error').text('');
                }
                
                // Validate phone (if provided)
                const telephone = $('#telephone').val().trim();
                if (telephone && telephone.length < 10) {
                    $('#telephone').addClass('error');
                    $('#telephone-error').text('Numéro de téléphone invalide');
                    isValid = false;
                } else {
                    $('#telephone').removeClass('error');
                    $('#telephone-error').text('');
                }
                
                if (!isValid) {
                    e.preventDefault();
                }
            });
        });
    </script>
</body>
</html>
