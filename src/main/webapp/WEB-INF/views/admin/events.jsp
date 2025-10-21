<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Modération Événements - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .admin-container { max-width: 1400px; margin: 20px auto; padding: 20px; }
        .events-table { width: 100%; border-collapse: collapse; background: white; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .events-table th, .events-table td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        .events-table th { background: #667eea; color: white; font-weight: 600; }
        .events-table tr:hover { background: #f8f9fa; }
        .badge { padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: 600; }
        .badge-success { background: #d4edda; color: #155724; }
        .badge-warning { background: #fff3cd; color: #856404; }
        .btn { padding: 6px 12px; border: none; border-radius: 4px; cursor: pointer; font-size: 14px; margin: 2px; }
        .btn-danger { background: #dc3545; color: white; }
        .btn-warning { background: #ffc107; color: #333; }
        .btn-success { background: #28a745; color: white; }
        .filters { margin-bottom: 20px; }
        .filters select { padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/views/layout/header.jsp" />

    <div class="admin-container">
        <h1>📅 Modération des Événements</h1>
        <jsp:include page="/WEB-INF/views/layout/messages.jsp" />

        <div class="filters">
            <form method="get">
                <select name="status" onchange="this.form.submit()">
                    <option value="">Tous les événements</option>
                    <option value="publie" ${selectedStatus == 'publie' ? 'selected' : ''}>Publiés</option>
                    <option value="brouillon" ${selectedStatus == 'brouillon' ? 'selected' : ''}>Brouillons</option>
                </select>
            </form>
        </div>

        <table class="events-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Titre</th>
                    <th>Organisateur</th>
                    <th>Date Début</th>
                    <th>Statut</th>
                    <th>Inscriptions</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${events}" var="event">
                    <tr>
                        <td>${event.id}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/events/details?id=${event.id}" target="_blank">
                                ${event.titre}
                            </a>
                        </td>
                        <td>${event.organisateur.nom}</td>
                        <td>${event.dateDebut}</td>
                        <td>
                            <c:choose>
                                <c:when test="${event.statut == 'PUBLIE'}">
                                    <span class="badge badge-success">✓ Publié</span>
                                </c:when>
                                <c:when test="${event.statut == 'BROUILLON'}">
                                    <span class="badge badge-warning">📝 Brouillon</span>
                                </c:when>
                            </c:choose>
                        </td>
                        <td>${event.nombreInscriptions} / ${event.capacite}</td>
                        <td>
                            <c:if test="${event.statut == 'PUBLIE'}">
                                <form method="post" style="display: inline;">
                                    <input type="hidden" name="action" value="unpublish">
                                    <input type="hidden" name="eventId" value="${event.id}">
                                    <button type="submit" class="btn btn-warning" onclick="return confirm('Dépublier ?')">
                                        📝 Dépublier
                                    </button>
                                </form>
                            </c:if>
                            <c:if test="${event.statut == 'BROUILLON'}">
                                <form method="post" style="display: inline;">
                                    <input type="hidden" name="action" value="publish">
                                    <input type="hidden" name="eventId" value="${event.id}">
                                    <button type="submit" class="btn btn-success" onclick="return confirm('Publier ?')">
                                        ✓ Publier
                                    </button>
                                </form>
                            </c:if>
                            <form method="post" style="display: inline;">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="eventId" value="${event.id}">
                                <button type="submit" class="btn btn-danger" onclick="return confirm('Supprimer définitivement ?')">
                                    🗑 Supprimer
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</body>
</html>
