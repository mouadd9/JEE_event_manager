<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="pageTitle" value="Statistiques" />
<jsp:include page="../layout/header.jsp" />

<div class="container-fluid py-4">
    <h2 class="mb-4">
        <i class="fas fa-chart-line"></i> Statistiques et Rapports
    </h2>

    <!-- Overview Cards -->
    <div class="row mb-4">
        <div class="col-md-3">
            <div class="card text-white bg-primary">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="text-uppercase mb-1">Total Événements</h6>
                            <h2 class="mb-0">${stats.totalEvents}</h2>
                        </div>
                        <i class="fas fa-calendar-alt fa-3x opacity-50"></i>
                    </div>
                    <div class="mt-2">
                        <small>
                            <i class="fas fa-check-circle"></i> ${stats.publishedEvents} publiés
                        </small>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="card text-white bg-success">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="text-uppercase mb-1">Total Participants</h6>
                            <h2 class="mb-0">${stats.totalParticipants}</h2>
                        </div>
                        <i class="fas fa-users fa-3x opacity-50"></i>
                    </div>
                    <div class="mt-2">
                        <small>
                            <i class="fas fa-arrow-up"></i> +${stats.newParticipantsThisMonth} ce mois
                        </small>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="card text-white bg-info">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="text-uppercase mb-1">Taux de Remplissage</h6>
                            <h2 class="mb-0">${stats.averageFillRate}%</h2>
                        </div>
                        <i class="fas fa-chart-pie fa-3x opacity-50"></i>
                    </div>
                    <div class="mt-2">
                        <small>
                            <i class="fas fa-chart-line"></i> Moyenne globale
                        </small>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="card text-white bg-warning">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="text-uppercase mb-1">Note Moyenne</h6>
                            <h2 class="mb-0">
                                <c:choose>
                                    <c:when test="${not empty stats.averageRating}">
                                        ${stats.averageRating}/5
                                    </c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </h2>
                        </div>
                        <i class="fas fa-star fa-3x opacity-50"></i>
                    </div>
                    <div class="mt-2">
                        <small>
                            <i class="fas fa-comments"></i> ${stats.totalReviews} avis
                        </small>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <!-- Charts Column -->
        <div class="col-lg-8">
            <!-- Events Timeline Chart -->
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0">
                        <i class="fas fa-chart-area"></i> Évolution des inscriptions
                    </h5>
                </div>
                <div class="card-body">
                    <canvas id="inscriptionsChart" style="max-height: 300px;"></canvas>
                </div>
            </div>

            <!-- Events by Category -->
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0">
                        <i class="fas fa-chart-bar"></i> Événements par catégorie
                    </h5>
                </div>
                <div class="card-body">
                    <canvas id="categoriesChart" style="max-height: 300px;"></canvas>
                </div>
            </div>

            <!-- Top Events -->
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">
                        <i class="fas fa-trophy"></i> Top 5 Événements
                    </h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Événement</th>
                                    <th>Participants</th>
                                    <th>Taux</th>
                                    <th>Note</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="event" items="${topEvents}" varStatus="status">
                                    <tr>
                                        <td>
                                            <span class="badge bg-secondary me-2">#${status.index + 1}</span>
                                            <c:out value="${event.titre}"/>
                                        </td>
                                        <td>${event.nombreInscriptions}</td>
                                        <td>
                                            <div class="progress" style="height: 20px; width: 100px;">
                                                <div class="progress-bar bg-success" role="progressbar" 
                                                     style="width: ${(event.nombreInscriptions * 100) / event.capacite}%">
                                                    ${(event.nombreInscriptions * 100) / event.capacite}%
                                                </div>
                                            </div>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty event.noteMoyenne}">
                                                    <c:forEach begin="1" end="5" var="i">
                                                        <i class="fas fa-star ${i <= event.noteMoyenne ? 'text-warning' : 'text-muted'}"></i>
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted">-</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- Sidebar Column -->
        <div class="col-lg-4">
            <!-- Quick Stats -->
            <div class="card mb-4">
                <div class="card-header bg-primary text-white">
                    <h5 class="mb-0">
                        <i class="fas fa-info-circle"></i> Statistiques rapides
                    </h5>
                </div>
                <div class="card-body">
                    <div class="d-flex justify-content-between mb-3">
                        <span>Événements actifs:</span>
                        <strong>${stats.activeEvents}</strong>
                    </div>
                    <div class="d-flex justify-content-between mb-3">
                        <span>Événements terminés:</span>
                        <strong>${stats.completedEvents}</strong>
                    </div>
                    <div class="d-flex justify-content-between mb-3">
                        <span>Événements annulés:</span>
                        <strong>${stats.cancelledEvents}</strong>
                    </div>
                    <hr>
                    <div class="d-flex justify-content-between mb-3">
                        <span>Vues totales:</span>
                        <strong><fmt:formatNumber value="${stats.totalViews}" pattern="#,###"/></strong>
                    </div>
                    <div class="d-flex justify-content-between mb-3">
                        <span>Commentaires:</span>
                        <strong>${stats.totalComments}</strong>
                    </div>
                    <div class="d-flex justify-content-between">
                        <span>Évaluations:</span>
                        <strong>${stats.totalEvaluations}</strong>
                    </div>
                </div>
            </div>

            <!-- Status Distribution -->
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0">
                        <i class="fas fa-chart-pie"></i> Répartition par statut
                    </h5>
                </div>
                <div class="card-body">
                    <canvas id="statusChart" style="max-height: 250px;"></canvas>
                </div>
            </div>

            <!-- Export Options -->
            <div class="card">
                <div class="card-header bg-success text-white">
                    <h5 class="mb-0">
                        <i class="fas fa-download"></i> Exporter les données
                    </h5>
                </div>
                <div class="card-body">
                    <div class="d-grid gap-2">
                        <a href="${pageContext.request.contextPath}/reports/export-pdf" 
                           class="btn btn-outline-danger">
                            <i class="far fa-file-pdf"></i> Rapport PDF
                        </a>
                        <a href="${pageContext.request.contextPath}/reports/export-excel" 
                           class="btn btn-outline-success">
                            <i class="far fa-file-excel"></i> Export Excel
                        </a>
                        <a href="${pageContext.request.contextPath}/reports/export-csv" 
                           class="btn btn-outline-info">
                            <i class="fas fa-file-csv"></i> Export CSV
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Chart.js Library -->
<script src="https://cdn.jsdelivr.net/npm/chart.js@3.9.1/dist/chart.min.js"></script>

<script>
// Inscriptions Timeline Chart
const inscriptionsCtx = document.getElementById('inscriptionsChart').getContext('2d');
new Chart(inscriptionsCtx, {
    type: 'line',
    data: {
        labels: ${inscriptionsChartLabels},
        datasets: [{
            label: 'Inscriptions',
            data: ${inscriptionsChartData},
            borderColor: 'rgb(75, 192, 192)',
            backgroundColor: 'rgba(75, 192, 192, 0.2)',
            tension: 0.3
        }]
    },
    options: {
        responsive: true,
        plugins: {
            legend: {
                display: true
            },
            title: {
                display: false
            }
        },
        scales: {
            y: {
                beginAtZero: true
            }
        }
    }
});

// Categories Chart
const categoriesCtx = document.getElementById('categoriesChart').getContext('2d');
new Chart(categoriesCtx, {
    type: 'bar',
    data: {
        labels: ${categoriesChartLabels},
        datasets: [{
            label: 'Nombre d\'événements',
            data: ${categoriesChartData},
            backgroundColor: [
                'rgba(255, 99, 132, 0.7)',
                'rgba(54, 162, 235, 0.7)',
                'rgba(255, 206, 86, 0.7)',
                'rgba(75, 192, 192, 0.7)',
                'rgba(153, 102, 255, 0.7)',
                'rgba(255, 159, 64, 0.7)'
            ]
        }]
    },
    options: {
        responsive: true,
        plugins: {
            legend: {
                display: false
            }
        },
        scales: {
            y: {
                beginAtZero: true,
                ticks: {
                    stepSize: 1
                }
            }
        }
    }
});

// Status Distribution Chart
const statusCtx = document.getElementById('statusChart').getContext('2d');
new Chart(statusCtx, {
    type: 'doughnut',
    data: {
        labels: ['Publiés', 'Brouillons', 'Terminés', 'Annulés'],
        datasets: [{
            data: [
                ${stats.publishedEvents},
                ${stats.draftEvents},
                ${stats.completedEvents},
                ${stats.cancelledEvents}
            ],
            backgroundColor: [
                'rgba(40, 167, 69, 0.8)',
                'rgba(108, 117, 125, 0.8)',
                'rgba(52, 58, 64, 0.8)',
                'rgba(220, 53, 69, 0.8)'
            ]
        }]
    },
    options: {
        responsive: true,
        plugins: {
            legend: {
                position: 'bottom'
            }
        }
    }
});
</script>

<jsp:include page="../layout/footer.jsp" />
