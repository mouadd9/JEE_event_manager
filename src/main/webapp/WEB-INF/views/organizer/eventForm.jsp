<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>${event != null ? 'Edit Event' : 'Create New Event'}</title>
    <%-- (All the <style> tags from the previous example go here) --%>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
        .nav { background-color: #333; overflow: hidden; }
        .nav a { float: left; color: white; text-align: center; padding: 14px 16px; text-decoration: none; font-size: 17px; }
        .nav a.active { background-color: #007bff; }
        .form-container { max-width: 800px; margin: 20px auto; padding: 20px; background-color: white; border-radius: 8px; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        .form-group input, .form-group textarea { width: 100%; padding: 8px; box-sizing: border-box; }
        .btn-submit { background-color: #007bff; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; }
    </style>
</head>
<body>

<div class="nav">
    <a href="${pageContext.request.contextPath}/organizer/dashboard">Dashboard</a>
    <a class="active" href="${pageContext.request.contextPath}/organizer/events/new">New Event</a>
</div>

<div class="form-container">
    <h1>${event != null ? 'Edit Event' : 'Create New Event'}</h1>

    <c:if test="${event != null}">
        <form action="${pageContext.request.contextPath}/organizer/events" method="POST">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="id" value="${event.id}">
                <%-- (All the form-groups from the previous example go here) --%>
            <div class="form-group">...</div>
            <button type="submit" class="btn-submit">Update Event</button>
        </form>
    </c:if>

    <c:if test="${event == null}">
        <form action="${pageContext.request.contextPath}/organizer/events" method="POST">
            <input type="hidden" name="action" value="create">
                <%-- (All the form-groups from the previous example go here) --%>
            <div class="form-group">...</div>
            <button type="submit" class="btn-submit">Create Event</button>
        </form>
    </c:if>
</div>

</body>
</html>