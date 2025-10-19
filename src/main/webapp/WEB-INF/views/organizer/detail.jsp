<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
  <title>Event Details: <c_rt:out value="${event.titre}"/></title>
  <style>
    /* Basic Reset & Body Style */
    body {
      font-family: Arial, sans-serif;
      background-color: #f8f9fa; /* Light grey background */
      margin: 0;
      padding: 0;
      color: #333;
    }

    /* Navbar Style */
    .nav {
      background-color: #343a40; /* Darker grey */
      overflow: hidden;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    .nav a {
      float: left;
      color: #f2f2f2; /* Lighter text */
      text-align: center;
      padding: 14px 16px;
      text-decoration: none;
      font-size: 17px;
      transition: background-color 0.3s, color 0.3s;
    }
    .nav a:hover {
      background-color: #ddd;
      color: black;
    }
    .nav a.active {
      background-color: #007bff; /* Primary blue */
      color: white;
    }

    /* Main Container */
    .detail-container {
      max-width: 900px;
      margin: 30px auto; /* More margin */
      padding: 30px; /* More padding */
      background-color: #ffffff; /* White background */
      border-radius: 8px;
      box-shadow: 0 4px 8px rgba(0,0,0,0.1); /* Softer shadow */
    }

    /* Header Styling */
    .detail-header {
      border-bottom: 2px solid #dee2e6; /* Lighter border */
      padding-bottom: 20px;
      margin-bottom: 30px; /* More space */
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap; /* Allow wrapping on small screens */
      gap: 15px; /* Space between title and actions */
    }
    .detail-header h1 {
      margin: 0;
      color: #343a40;
      flex-grow: 1; /* Allow title to take available space */
    }
    .detail-actions {
      white-space: nowrap; /* Keep buttons on one line if possible */
    }
    .detail-actions a, .detail-actions button {
      text-decoration: none;
      padding: 10px 15px; /* Bigger buttons */
      border: none;
      border-radius: 5px; /* Slightly more rounded */
      cursor: pointer;
      color: white;
      margin-left: 8px; /* Consistent spacing */
      font-size: 14px;
      font-weight: bold;
      transition: opacity 0.3s;
    }
    .detail-actions a:hover, .detail-actions button:hover {
      opacity: 0.85;
    }

    /* Button Colors */
    .btn-edit { background-color: #007bff; } /* Blue */
    .btn-publish { background-color: #28a745; } /* Green */
    .btn-unpublish { background-color: #ffc107; color: #333; } /* Yellow */
    .btn-cancel { background-color: #fd7e14; } /* Orange */
    .btn-delete { background-color: #dc3545; } /* Red */

    /* Detail Body Styling */
    .detail-body {
      line-height: 1.6;
    }
    .detail-body h2 {
      border-bottom: 1px solid #eee;
      padding-bottom: 10px;
      margin-bottom: 15px;
      color: #495057;
    }
    .detail-body p {
      margin-bottom: 15px;
    }
    .detail-body strong {
      color: #495057;
    }
    .status-badge {
      padding: 5px 10px;
      border-radius: 4px;
      color: white;
      font-size: 0.9em;
      vertical-align: middle;
      margin-left: 10px;
    }
    .status-badge.brouillon { background-color: #6c757d; } /* Grey */
    .status-badge.publie { background-color: #28a745; } /* Green */
    .status-badge.annule { background-color: #dc3545; } /* Red */

  </style>
</head>
<body>

<div class="nav">
  <a href="${pageContext.request.contextPath}/organizer/dashboard">Dashboard</a>
  <a href="${pageContext.request.contextPath}/organizer/events/new">New Event</a>
</div>

<div class="detail-container">

  <div class="detail-header">
    <h1><c_rt:out value="${event.titre}"/>
      <span class="status-badge ${fn:toLowerCase(event.statut)}"><c_rt:out value="${event.statut}"/></span>
    </h1>

    <div class="detail-actions">
      <a href="${pageContext.request.contextPath}/organizer/events/edit?id=${event.id}" class="btn-edit">Edit</a>

      <c:choose>
        <c:when test="${event.statut == 'PUBLIE'}">
          <form action="${pageContext.request.contextPath}/organizer/events" method="POST" style="display:inline;">
            <input type="hidden" name="action" value="unpublish">
            <input type="hidden" name="eventId" value="${event.id}">
            <button type="submit" class="btn-unpublish">Unpublish</button>
          </form>
        </c:when>
        <c:when test="${event.statut == 'BROUILLON'}">
          <form action="${pageContext.request.contextPath}/organizer/events" method="POST" style="display:inline;">
            <input type="hidden" name="action" value="publish">
            <input type="hidden" name="eventId" value="${event.id}">
            <button type="submit" class="btn-publish">Publish</button>
          </form>
        </c:when>
      </c:choose>

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
    <h2>Event Details</h2>
    <p><strong>Status:</strong> <c_rt:out value="${event.statut}"/></p>
    <p><strong>Date & Time:</strong> From <c_rt:out value="${event.dateDebut}"/> to <c_rt:out value="${event.dateFin}"/></p>
    <p><strong>Location:</strong> <c_rt:out value="${event.lieu}"/></p>

    <h2>Description</h2>
    <p><c_rt:out value="${event.description}"/></p>

    <%-- Placeholder for future content --%>
    <h2>Participants</h2>
    <p>Participant list will be displayed here.</p>

  </div>
</div>

</body>
</html>