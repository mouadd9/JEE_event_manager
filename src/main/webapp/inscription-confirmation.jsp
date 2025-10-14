<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription confirmée !</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <style>
        :root {
            --primary-color: #d1410c;
            --success-color: #00ab55;
            --text-dark: #39364f;
            --text-light: #6f7287;
            --bg-light: #f8f7fa;
        }
        
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background-color: var(--bg-light);
            color: var(--text-dark);
        }
        
        .confirmation-container {
            max-width: 700px;
            margin: 3rem auto;
            padding: 0 1rem;
        }
        
        .success-icon {
            width: 100px;
            height: 100px;
            background: var(--success-color);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 2rem;
            animation: scaleIn 0.5s ease-out;
        }
        
        .success-icon i {
            font-size: 3rem;
            color: white;
        }
        
        @keyframes scaleIn {
            from {
                transform: scale(0);
            }
            to {
                transform: scale(1);
            }
        }
        
        .confirmation-card {
            background: white;
            border-radius: 12px;
            padding: 3rem 2rem;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            text-align: center;
        }
        
        .confirmation-title {
            font-size: 2rem;
            font-weight: 700;
            color: var(--text-dark);
            margin-bottom: 1rem;
        }
        
        .confirmation-message {
            font-size: 1.1rem;
            color: var(--text-light);
            margin-bottom: 2rem;
        }
        
        .status-badge {
            display: inline-block;
            padding: 0.6rem 1.5rem;
            border-radius: 50px;
            font-weight: 600;
            margin-bottom: 2rem;
        }
        
        .status-badge.accepted {
            background: #d4edda;
            color: #155724;
        }
        
        .status-badge.pending {
            background: #fff3cd;
            color: #856404;
        }
        
        .details-box {
            background: var(--bg-light);
            border-radius: 12px;
            padding: 2rem;
            margin: 2rem 0;
            text-align: left;
        }
        
        .detail-row {
            display: flex;
            justify-content: space-between;
            padding: 0.8rem 0;
            border-bottom: 1px solid #e0e0e0;
        }
        
        .detail-row:last-child {
            border-bottom: none;
        }
        
        .detail-label {
            color: var(--text-light);
            font-weight: 500;
        }
        
        .detail-value {
            color: var(--text-dark);
            font-weight: 600;
        }
        
        .action-buttons {
            display: flex;
            gap: 1rem;
            justify-content: center;
            margin-top: 2rem;
        }
        
        .btn-custom {
            padding: 0.8rem 2rem;
            border-radius: 8px;
            font-weight: 600;
            text-decoration: none;
            transition: all 0.3s;
            border: none;
            cursor: pointer;
        }
        
        .btn-primary-custom {
            background: var(--primary-color);
            color: white;
        }
        
        .btn-primary-custom:hover {
            background: #b8350a;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(209, 65, 12, 0.3);
        }
        
        .btn-secondary-custom {
            background: white;
            color: var(--text-dark);
            border: 2px solid #e0e0e0;
        }
        
        .btn-secondary-custom:hover {
            border-color: var(--primary-color);
            color: var(--primary-color);
        }
        
        .info-box {
            background: #e3f2fd;
            border-left: 4px solid #2196f3;
            padding: 1rem;
            border-radius: 8px;
            margin-top: 2rem;
            text-align: left;
        }
        
        .info-box i {
            color: #2196f3;
            margin-right: 0.5rem;
        }
    </style>
</head>
<body>
    <div class="confirmation-container">
        <div class="confirmation-card">
            <div class="success-icon">
                <i class="fas fa-check"></i>
            </div>
            
            <h1 class="confirmation-title">Inscription confirmée !</h1>
            <p class="confirmation-message">
                Votre inscription à l'événement a été enregistrée avec succès.
            </p>
            
            <c:choose>
                <c:when test="${inscription.statut == 'VALIDEE'}">
                    <span class="status-badge accepted">
                        <i class="fas fa-check-circle me-2"></i>Inscription validée
                    </span>
                </c:when>
                <c:otherwise>
                    <span class="status-badge pending">
                        <i class="fas fa-clock me-2"></i>En attente de validation
                    </span>
                </c:otherwise>
            </c:choose>
            
            <div class="details-box">
                <div class="detail-row">
                    <span class="detail-label">Événement</span>
                    <span class="detail-value">${inscription.evenement.titre}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Date</span>
                    <span class="detail-value">
                        <fmt:formatDate value="${inscription.evenement.dateDebut}" pattern="dd/MM/yyyy à HH:mm" />
                    </span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Lieu</span>
                    <span class="detail-value">${inscription.evenement.lieu}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Type de billet</span>
                    <span class="detail-value">${inscription.typeBillet}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Quantité</span>
                    <span class="detail-value">${inscription.quantite} billet(s)</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Numéro de confirmation</span>
                    <span class="detail-value">#${inscription.id}</span>
                </div>
            </div>
            
            <div class="info-box">
                <i class="fas fa-info-circle"></i>
                <strong>Que faire maintenant ?</strong><br>
                Un email de confirmation a été envoyé à votre adresse. 
                Vous pouvez retrouver tous vos événements dans votre espace personnel.
            </div>
            
            <div class="action-buttons">
                <a href="${pageContext.request.contextPath}/catalogue" class="btn-custom btn-secondary-custom">
                    <i class="fas fa-arrow-left me-2"></i>Retour au catalogue
                </a>
                <a href="${pageContext.request.contextPath}/mes-inscriptions" class="btn-custom btn-primary-custom">
                    <i class="fas fa-list me-2"></i>Mes inscriptions
                </a>
            </div>
        </div>
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
