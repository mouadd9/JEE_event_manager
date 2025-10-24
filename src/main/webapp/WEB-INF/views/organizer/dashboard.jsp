<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<html>
<head>
    <title>Tableau de Bord</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        /* Professional Redesign CSS */
        :root {
            --color-bg: #1a1a1e;
            --color-bg-secondary: #25252a;
            --color-text: #dadada;
            --color-text-secondary: #9a9a9a;
            --color-border: #3a3a42;
            --color-primary: #3d8bfd;
            --color-primary-hover: #5a9eff;
            --color-green: #28a745;
            --color-orange: #fd7e14;
            --color-red: #dc3545;
            --font-family-sans: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji";
            --border-radius: 6px;
        }
        body {
            font-family: var(--font-family-sans);
            background-color: var(--color-bg);
            color: var(--color-text);
            margin: 0;
            padding: 0;
            line-height: 1.6;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }

        /* Navigation */
        .nav {
            background-color: var(--color-bg-secondary);
            border-bottom: 1px solid var(--color-border);
            padding: 0 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .nav-links {
            overflow: hidden;
        }
        .nav a {
            float: left;
            color: var(--color-text-secondary);
            text-align: center;
            padding: 18px 16px;
            text-decoration: none;
            font-size: 16px;
            font-weight: 500;
            transition: color 0.2s, border-bottom-color 0.2s;
            border-bottom: 3px solid transparent;
        }
        .nav a:hover {
            color: var(--color-text);
        }
        .nav a.active {
            color: var(--color-primary);
            border-bottom: 3px solid var(--color-primary);
        }
        .nav-user {
            color: var(--color-text);
            font-weight: 500;
            padding: 18px 0;
        }

        /* Stats Header */
        .stats {
            background-color: var(--color-bg-secondary);
            padding: 25px 30px;
            margin-top: 30px;
            border-radius: var(--border-radius);
            border: 1px solid var(--color-border);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .stats h2 {
            margin: 0;
            font-weight: 600;
        }
        .stats p {
            margin: 0;
            font-size: 16px;
            color: var(--color-text-secondary);
        }
        .stats strong {
            color: var(--color-text);
            font-weight: 600;
        }

        /* Page Title */
        .page-title {
            margin-top: 40px;
            margin-bottom: 20px;
            font-size: 24px;
            font-weight: 600;
            color: var(--color-text);
        }

        /* Event List */
        .event-list {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
            grid-gap: 20px;
        }
        .card {
            background-color: var(--color-bg-secondary);
            border-radius: var(--border-radius);
            border: 1px solid var(--color-border);
            text-decoration: none;
            color: var(--color-text);
            display: block;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
            overflow: hidden; /* To contain the border */

            /* Status Indicator */
            border-left-width: 5px;
            border-left-style: solid;
        }
        .card:hover {
            transform: translateY(-4px);
            box-shadow: 0 8px 20px rgba(0,0,0,0.3);
            border-color: #555;
        }

        /* Card Status Colors */
        .card.status-publie { border-left-color: var(--color-green); }
        .card.status-brouillon { border-left-color: var(--color-orange); }
        .card.status-annule { border-left-color: var(--color-red); }

        .card-header {
            padding: 20px;
            border-bottom: 1px solid var(--color-border);
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
        }
        .card-header h3 {
            margin: 0;
            font-size: 18px;
            font-weight: 600;
            padding-right: 15px;
        }
        .status-text {
            font-size: 14px;
            font-weight: 500;
            padding: 4px 8px;
            border-radius: 4px;
            flex-shrink: 0;
            background-color: var(--color-bg);
            border: 1px solid var(--color-border);
        }

        /* Status Text Colors */
        .status-text.status-publie { color: var(--color-green); border-color: var(--color-green); background-color: rgba(40, 167, 69, 0.1);}
        .status-text.status-brouillon { color: var(--color-orange); border-color: var(--color-orange); background-color: rgba(253, 126, 20, 0.1);}
        .status-text.status-annule { color: var(--color-red); border-color: var(--color-red); background-color: rgba(220, 53, 69, 0.1);}

        .card-body {
            padding: 20px;
        }
        .card-body p {
            margin: 0 0 10px 0;
            color: var(--color-text-secondary);
        }
        .card-body p strong {
            color: var(--color-text);
            font-weight: 500;
            min-width: 80px;
            display: inline-block;
        }

    </style>
</head>
<body>

<div class="nav">
    <div class="nav-links">
        <a class="active" href="${pageContext.request.contextPath}/organizer/dashboard">Tableau de Bord</a>
        <a href="${pageContext.request.contextPath}/organizer/events/new">Nouvel Événement</a>
    </div>
    <div class="nav-user">
        <c:out value="${organizer.firstName}"/> <c:out value="${organizer.lastName}"/>
    </div>
</div>

<div class="container">
    <div class="stats">
        <h2>Tableau de Bord</h2>
        <p>Nombre d'événements créés: <strong><c:out value="${events.size()}"/></strong></p>
    </div>

    <h2 class="page-title">Vos Événements</h2>
    <div class="event-list">
        <c:forEach var="event" items="${events}">

            <a href="${pageContext.request.contextPath}/organizer/events/detail?id=${event.id}"
               class="card status-${fn:toLowerCase(event.statut)}">

                <div class="card-header">
                    <h3><c:out value="${event.titre}"/></h3>

                    <c:choose>
                        <c:when test="${event.statut == 'PUBLIE'}">
                            <span class="status-text status-publie">Publié</span>
                        </c:when>
                        <c:when test="${event.statut == 'BROUILLON'}">
                            <span class="status-text status-brouillon">Non Publié</span>
                        </c:when>
                        <c:when test="${event.statut == 'ANNULE'}">
                            <span class="status-text status-annule">Annulé</span>
                        </c:when>
                    </c:choose>
                </div>
                <div class="card-body">
                    <p>
                        <strong>Date:</strong>
                        <fmt:formatDate value="${event.dateDebutAsDate}" pattern="dd MMM yyyy, HH:mm"/>                    </p>
                    <p>
                        <strong>Lieu:</strong>
                        <c:out value="${event.lieu}"/>
                    </p>
                </div>
            </a>
        </c:forEach>
    </div>
</div>
</body>
</html>