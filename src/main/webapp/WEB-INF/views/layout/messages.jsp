<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Flash Messages Container -->
<div class="container mt-3">
    <!-- Success Message -->
    <c:if test="${not empty sessionScope.successMessage or not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-2"></i>
            <strong>Succès!</strong>
            <c:out value="${not empty sessionScope.successMessage ? sessionScope.successMessage : successMessage}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>

    <!-- Error Message -->
    <c:if test="${not empty sessionScope.errorMessage or not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle me-2"></i>
            <strong>Erreur!</strong>
            <c:out value="${not empty sessionScope.errorMessage ? sessionScope.errorMessage : errorMessage}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <!-- Warning Message -->
    <c:if test="${not empty sessionScope.warningMessage or not empty warningMessage}">
        <div class="alert alert-warning alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-triangle me-2"></i>
            <strong>Attention!</strong>
            <c:out value="${not empty sessionScope.warningMessage ? sessionScope.warningMessage : warningMessage}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="warningMessage" scope="session"/>
    </c:if>

    <!-- Info Message -->
    <c:if test="${not empty sessionScope.infoMessage or not empty infoMessage}">
        <div class="alert alert-info alert-dismissible fade show" role="alert">
            <i class="fas fa-info-circle me-2"></i>
            <strong>Information:</strong>
            <c:out value="${not empty sessionScope.infoMessage ? sessionScope.infoMessage : infoMessage}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="infoMessage" scope="session"/>
    </c:if>
</div>
