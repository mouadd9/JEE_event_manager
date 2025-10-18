<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
  <title>Event Details: <c:out value="${event.titre}"/></title>
  <style>
    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
    .nav { background-color: #333; overflow: hidden; }
    .nav a { float: left; color: white; text-align: center; padding: 14px 16px; text-decoration: none; font-size: 17px; }
    .detail-container { max-width: 900px; margin: 20px auto; padding: 20px; background-color: white; border-radius: 8px; }
    .detail-header { border-bottom: 2px solid #eee; padding-bottom: 20px; margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center; }
    .detail-header h1 { margin: 0; }
    .detail-actions a, .detail-actions button { text-decoration: none; padding: 8px 12px; border: none; border-radius: 4px; cursor: pointer; color: white; margin-left: 5px; }
    .btn-edit { background-color: #007bff; }
    .btn-publish { background-color: #28a745; }
    .btn-cancel { background-color: #dc3545; }
    .btn-delete { background-color: #6c757d; }
  </style>
</head>
<body>

<div class="nav">
  <a href="${pageContext.request.contextPath}/organizer/dashboard">Dashboard</a>
  <a href="${pageContext.request.contextPath}/organizer/events/new">New Event</a>
</div>

<div class="detail-container">

  <div class="detail-header">
    <h1><c:out value="${event.titre}"/></h1>

    <div class="detail-actions">
      <a href="${pageContext.request.contextPath}/organizer/events/edit?id=${event.id}" class="btn-edit">Edit</a>

      <c:if test="${event.statut != 'PUBLIE'}">
        <form action="${pageContext.request.contextPath}/organizer/events" method="POST" style="display:inline;">
          <input type="hidden" name="action" value="publish">
          <input type="hidden" name="eventId" value="${event.id}">
          <button type="submit" class="btn-publish">Publish</button>
        </form>
      </c:if>

      <c:if test="${event.statut != 'ANNULE'}">
        <form action="${pageContext.request.contextPath}/organizer/events" method="POST" style="display:inline;">
          <input type="hidden" name="action" value="cancel">
          <input type="hidden" name="eventId" value="${event.id}">
          <button type="submit" class="btn-cancel">Cancel</button>
        </form>
      </c:if>

      <form action="${pageContext.request.contextPath}/organizer/events" method="POST" style="display:inline;">
        <input type="hidden" name="action" value="delete">
        <input type="hidden" name="eventId" value="${event.id}">
        <button type="submit" class="btn-delete">Delete</button>
      </form>
    </div>
  </div>

  <div class="detail-body">
    <p>Event details will go here...</p>
  </div>
</div>

</body>
</html>