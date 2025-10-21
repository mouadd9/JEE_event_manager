<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Event Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .admin-container {
            max-width: 1400px;
            margin: 0 auto;
            padding: 20px;
        }

        .admin-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            border-radius: 10px;
            margin-bottom: 30px;
        }

        .admin-header h1 {
            margin: 0 0 10px 0;
            font-size: 32px;
        }

        .admin-header p {
            margin: 0;
            opacity: 0.9;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .stat-card {
            background: white;
            padding: 25px;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            transition: transform 0.2s;
        }

        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.15);
        }

        .stat-card .stat-icon {
            font-size: 36px;
            margin-bottom: 10px;
        }

        .stat-card .stat-value {
            font-size: 32px;
            font-weight: bold;
            color: #333;
            margin: 10px 0;
        }

        .stat-card .stat-label {
            color: #666;
            font-size: 14px;
        }

        .stat-card.pending {
            background: #fff3cd;
            border-left: 4px solid #ffc107;
        }

        .stat-card.published {
            background: #d4edda;
            border-left: 4px solid #28a745;
        }

        .stat-card.draft {
            background: #f8d7da;
            border-left: 4px solid #dc3545;
        }

        .quick-actions {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-bottom: 30px;
        }

        .action-btn {
            padding: 15px 20px;
            background: white;
            border: 2px solid #667eea;
            color: #667eea;
            border-radius: 8px;
            font-weight: 600;
            text-decoration: none;
            text-align: center;
            transition: all 0.3s;
            display: block;
        }

        .action-btn:hover {
            background: #667eea;
            color: white;
        }

        .section {
            background: white;
            padding: 25px;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }

        .section h2 {
            margin-top: 0;
            color: #333;
            border-bottom: 2px solid #667eea;
            padding-bottom: 10px;
        }

        .pending-list {
            list-style: none;
            padding: 0;
            margin: 0;
        }

        .pending-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            margin-bottom: 10px;
            transition: background 0.2s;
        }

        .pending-item:hover {
            background: #f8f9fa;
        }

        .pending-info {
            flex: 1;
        }

        .pending-name {
            font-weight: 600;
            color: #333;
            margin-bottom: 5px;
        }

        .pending-email {
            color: #666;
            font-size: 14px;
        }

        .pending-actions {
            display: flex;
            gap: 10px;
        }

        .btn-approve {
            padding: 8px 16px;
            background: #28a745;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 600;
            transition: background 0.2s;
        }

        .btn-approve:hover {
            background: #218838;
        }

        .btn-reject {
            padding: 8px 16px;
            background: #dc3545;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 600;
            transition: background 0.2s;
        }

        .btn-reject:hover {
            background: #c82333;
        }

        .no-data {
            text-align: center;
            padding: 40px;
            color: #999;
        }

        .nav-tabs {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
            border-bottom: 2px solid #ddd;
        }

        .nav-tab {
            padding: 10px 20px;
            background: none;
            border: none;
            border-bottom: 2px solid transparent;
            color: #666;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
        }

        .nav-tab.active {
            color: #667eea;
            border-bottom-color: #667eea;
        }

        .nav-tab:hover {
            color: #667eea;
        }
    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/views/layout/header.jsp" />

    <div class="admin-container">
        <div class="admin-header">
            <h1>👨‍💼 Administration</h1>
            <p>Tableau de bord de gestion de la plateforme</p>
        </div>

        <jsp:include page="/WEB-INF/views/layout/messages.jsp" />

        <!-- Statistics Cards -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon">👥</div>
                <div class="stat-value">${totalUsers}</div>
                <div class="stat-label">Total Utilisateurs</div>
            </div>

            <div class="stat-card pending">
                <div class="stat-icon">⏳</div>
                <div class="stat-value">${pendingOrganizersCount}</div>
                <div class="stat-label">Organisateurs en Attente</div>
            </div>

            <div class="stat-card">
                <div class="stat-icon">📅</div>
                <div class="stat-value">${totalEvents}</div>
                <div class="stat-label">Total Événements</div>
            </div>

            <div class="stat-card published">
                <div class="stat-icon">✅</div>
                <div class="stat-value">${publishedEvents}</div>
                <div class="stat-label">Événements Publiés</div>
            </div>

            <div class="stat-card draft">
                <div class="stat-icon">📝</div>
                <div class="stat-value">${draftEvents}</div>
                <div class="stat-label">Brouillons</div>
            </div>
        </div>

        <!-- Quick Actions -->
        <div class="quick-actions">
            <a href="${pageContext.request.contextPath}/admin/users" class="action-btn">
                Gérer les Utilisateurs
            </a>
            <a href="${pageContext.request.contextPath}/admin/events" class="action-btn">
                Modérer les Événements
            </a>
            <a href="${pageContext.request.contextPath}/admin/users?status=pending" class="action-btn">
                Approuver Organisateurs
            </a>
        </div>

        <!-- Pending Organizers Section -->
        <c:if test="${pendingOrganizersCount > 0}">
            <div class="section">
                <h2>⏳ Organisateurs en Attente d'Approbation</h2>
                
                <ul class="pending-list">
                    <c:forEach items="${pendingOrganizers}" var="org">
                        <li class="pending-item">
                            <div class="pending-info">
                                <div class="pending-name">${org.nom} - ${org.organisation}</div>
                                <div class="pending-email">${org.email} | Inscrit le: ${org.dateInscription}</div>
                            </div>
                            <div class="pending-actions">
                                <form method="post" action="${pageContext.request.contextPath}/admin/users" style="display: inline;">
                                    <input type="hidden" name="action" value="approve">
                                    <input type="hidden" name="userId" value="${org.id}">
                                    <button type="submit" class="btn-approve" onclick="return confirm('Approuver cet organisateur ?')">
                                        ✓ Approuver
                                    </button>
                                </form>
                                <form method="post" action="${pageContext.request.contextPath}/admin/users" style="display: inline;">
                                    <input type="hidden" name="action" value="reject">
                                    <input type="hidden" name="userId" value="${org.id}">
                                    <button type="submit" class="btn-reject" onclick="return confirm('Rejeter et supprimer ce compte ?')">
                                        ✗ Rejeter
                                    </button>
                                </form>
                            </div>
                        </li>
                    </c:forEach>
                </ul>

                <c:if test="${pendingOrganizersCount > 5}">
                    <div style="text-align: center; margin-top: 20px;">
                        <a href="${pageContext.request.contextPath}/admin/users?status=pending" class="action-btn" style="display: inline-block;">
                            Voir tous les organisateurs en attente
                        </a>
                    </div>
                </c:if>
            </div>
        </c:if>
    </div>

    <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</body>
</html>
