<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Mot de passe oublié" scope="request"/>
<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/messages.jsp"/>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-5 col-lg-4">
            <div class="card shadow-lg mt-5">
                <div class="card-body p-5">
                    <div class="text-center mb-4">
                        <i class="fas fa-unlock-alt fa-3x text-primary"></i>
                        <h3 class="mt-3 fw-bold">Mot de passe oublié ?</h3>
                        <p class="text-muted">Entrez votre adresse email pour recevoir un nouveau mot de passe.</p>
                    </div>
                    <form action="${pageContext.request.contextPath}/forgot-password" method="post">
                        <div class="mb-3">
                            <label for="email" class="form-label">
                                <i class="fas fa-envelope"></i> Adresse Email
                            </label>
                            <input type="email" class="form-control" id="email" name="email" required autofocus placeholder="votre@email.com">
                        </div>
                        <div class="d-grid mb-3">
                            <button type="submit" class="btn btn-primary btn-lg">
                                <i class="fas fa-paper-plane"></i> Envoyer
                            </button>
                        </div>
                        <div class="text-center">
                            <a href="${pageContext.request.contextPath}/login" class="text-decoration-none small">
                                Retour à la connexion
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="../layout/footer.jsp"/>
