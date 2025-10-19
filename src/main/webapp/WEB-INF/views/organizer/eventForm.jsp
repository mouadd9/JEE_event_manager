<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>${event != null ? 'Edit Event' : 'Create New Event'}</title>
    <%-- Styles for the nav bar and form --%>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
        .nav { background-color: #333; overflow: hidden; }
        .nav a { float: left; color: white; text-align: center; padding: 14px 16px; text-decoration: none; }
        .nav a.active { background-color: #007bff; }
        .form-container { max-width: 800px; margin: 20px auto; padding: 20px; background-color: white; border-radius: 8px; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        .form-group input, .form-group textarea { width: 100%; padding: 8px; box-sizing: border-box; }
        .btn-submit { background-color: #007bff; color: white; padding: 10px 15px; border: none; }
    </style>
</head>
<body>

<div class="nav">
    <a href="${pageContext.request.contextPath}/organizer/dashboard">Dashboard</a>
    <a class="active" href="${pageContext.request.contextPath}/organizer/events/new">New Event</a>
</div>

<div class="form-container">
    <h1>${event != null ? 'Edit Event' : 'Create New Event'}</h1>

    <%-- Check if we are editing or creating --%>
    <c:choose>
        <c:when test="${event != null}">
            <form action="${pageContext.request.contextPath}/organizer/events" method="POST">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="id" value="${event.id}">
                <div class="form-group">...</div>
                <button type="submit" class="btn-submit">Update Event</button>
            </form>
        </c:when>
        <c:otherwise>
            <form action="${pageContext.request.contextPath}/organizer/events" method="POST">
                <input type="hidden" name="action" value="create">
                <div class="form-group">
                    <label for="titre">Title</label>
                    <input type="text" id="titre" name="titre" required>
                </div>
                <div class="form-group">
                    <label for="dateDebut">Start Date</label>
                    <input type="datetime-local" id="dateDebut" name="dateDebut" required>
                </div>
                <div class="form-group">
                    <label for="dateFin">End Date</label>
                    <input type="datetime-local" id="dateFin" name="dateFin" required>
                </div>
                <div class="form-group">
                    <label for="lieu">Location</label>
                    <input type="text" id="lieu" name="lieu">
                </div>
                <div class="form-group">
                    <label for="description">Description</label>
                    <textarea id="description" name="description" rows="5"></textarea>
                </div>
                <button type="submit" class="btn-submit">Create Event</button>
            </form>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>