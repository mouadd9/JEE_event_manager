<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Gestion Utilisateurs - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .admin-container { max-width: 1400px; margin: 20px auto; padding: 20px; }
        .users-table { width: 100%; border-collapse: collapse; background: white; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .users-table th, .users-table td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        .users-table th { background: #667eea; color: white; font-weight: 600; }
        .users-table tr:hover { background: #f8f9fa; }
        .badge { padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: 600; }
        .badge-success { background: #d4edda; color: #155724; }
        .badge-warning { background: #fff3cd; color: #856404; }
        .badge-danger { background: #f8d7da; color: #721c24; }
        .btn { padding: 6px 12px; border: none; border-radius: 4px; cursor: pointer; font-size: 14px; margin: 2px; }
        .btn-success { background: #28a745; color: white; }
        .btn-danger { background: #dc3545; color: white; }
        .btn-warning { background: #ffc107; color: #333; }
        .filters { margin-bottom: 20px; display: flex; gap: 10px; }
        .filters select { padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/views/layout/header.jsp" />

    <div class="admin-container">
        <h1>👥 Gestion des Utilisateurs</h1>
        <jsp:include page="/WEB-INF/views/layout/messages.jsp" />

        <div class="filters">
            <form method="get">
                <select name="status" onchange="this.form.submit()">
                    <option value="">Tous les statuts</option>
                    <option value="pending" ${selectedStatus == 'pending' ? 'selected' : ''}>En attente d'approbation</option>
                </select>
            </form>
        </div>

        <table class="users-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nom</th>
                    <th>Email</th>
                    <th>Type</th>
                    <th>Statut</th>
                    <th>Organisation</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${users}" var="user">
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.nom}</td>
                        <td>${user.email}</td>
                        <td>
                            <c:choose>
                                <c:when test="${user.role == 'PARTICIPANT'}">
                                    <span class="badge badge-success">Participant</span>
                                </c:when>
                                <c:when test="${user.role == 'ORGANISATEUR'}">
                                    <span class="badge badge-warning">Organisateur</span>
                                </c:when>
                                <c:when test="${user.role == 'ADMINISTRATEUR'}">
                                    <span class="badge badge-danger">Admin</span>
                                </c:when>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${user.role == 'ORGANISATEUR' && !user.approved}">
                                    <span class="badge badge-warning">⏳ En attente</span>
                                </c:when>
                                <c:when test="${user.statut == 'ACTIF'}">
                                    <span class="badge badge-success">✓ Actif</span>
                                </c:when>
                                <c:when test="${user.statut == 'SUSPENDU'}">
                                    <span class="badge badge-danger">⊘ Suspendu</span>
                                </c:when>
                            </c:choose>
                        </td>
                        <td>
                            <c:if test="${user.role == 'ORGANISATEUR'}">
                                ${user.organisation}
                            </c:if>
                        </td>
                        <td>
                            <c:if test="${user.role == 'ORGANISATEUR' && !user.approved}">
                                <form method="post" style="display: inline;">
                                    <input type="hidden" name="action" value="approve">
                                    <input type="hidden" name="userId" value="${user.id}">
                                    <button type="submit" class="btn btn-success" onclick="return confirm('Approuver ?')">✓ Approuver</button>
                                </form>
                                <form method="post" style="display: inline;">
                                    <input type="hidden" name="action" value="reject">
                                    <input type="hidden" name="userId" value="${user.id}">
                                    <button type="submit" class="btn btn-danger" onclick="return confirm('Rejeter ?')">✗ Rejeter</button>
                                </form>
                            </c:if>
                            <c:if test="${user.role != 'ADMINISTRATEUR' && user.statut == 'ACTIF'}">
                                <form method="post" style="display: inline;">
                                    <input type="hidden" name="action" value="suspend">
                                    <input type="hidden" name="userId" value="${user.id}">
                                    <button type="submit" class="btn btn-warning" onclick="return confirm('Suspendre ?')">⊘ Suspendre</button>
                                </form>
                            </c:if>
                            <c:if test="${user.role != 'ADMINISTRATEUR' && user.statut == 'SUSPENDU'}">
                                <form method="post" style="display: inline;">
                                    <input type="hidden" name="action" value="activate">
                                    <input type="hidden" name="userId" value="${user.id}">
                                    <button type="submit" class="btn btn-success" onclick="return confirm('Activer ?')">✓ Activer</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</body>
</html>
