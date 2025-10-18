<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Organizer Dashboard</title>
    <%-- (All the <style> tags from the previous example go here) --%>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
        .nav { background-color: #333; overflow: hidden; }
        .nav a { float: left; color: white; text-align: center; padding: 14px 16px; text-decoration: none; font-size: 17px; }
        .nav a.active { background-color: #007bff; }
        .container { max-width: 1200px; margin: 20px auto; padding: 20px; }
        .stats { background-color: white; padding: 20px; margin-bottom: 20px; border-radius: 8px; }
        .event-list { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); grid-gap: 20px; }
        .card { background-color: white; border: 1px solid #ddd; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1); text-decoration: none; color: black; display: block; }
        .card:hover { box-shadow: 0 4px 8px rgba(0,0,0,0.15); }
        .card-header { padding: 16px; background-color: #f9f9f9; border-bottom: 1px solid #ddd; }
        .card-header h3 { margin: 0; }
        .card-body { padding: 16px; }
    </style>
</head>
<body>

<div class="nav">
    <a class="active" href="${pageContext.request.contextPath}/organizer/dashboard">Dashboard</a>
    <a href="${pageContext.request.contextPath}/organizer/events/new">New Event</a>
</div>

<div class="container">

    <div class="stats">
        <h2>Dashboard</h2>
        <p>Number of Submitted Events: <strong><c:out value="${events.size()}"/></strong></p>
    </div>

    <h2>Your Events</h2>
    <div class="event-list">

        <c:forEach var="event" items="${events}">
            <a href="${pageContext.request.contextPath}/organizer/events/detail?id=${event.id}" class="card">
                <div class="card-header">
                    <h3><c:out value="${event.titre}"/></h3>
                    <span>Status: <strong><c:out value="${event.statut}"/></strong></span>
                </div>
                <div class="card-body">
                    <p><strong>Date:</strong> <c:out value="${event.dateDebut}"/> to <c:out value="${event.dateFin}"/></p>
                    <p><strong>Location:</strong> <c:out value="${event.lieu}"/></p>
                </div>
            </a>
        </c:forEach>

    </div>
</div>
</body>
</html>