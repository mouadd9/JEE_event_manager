package com.projet.jee.servlet;

import com.google.gson.Gson;
import com.projet.jee.model.Evaluation;
import com.projet.jee.service.AuthenticationService;
import com.projet.jee.service.EvaluationService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet for managing event ratings/evaluations.
 * Supports GET (retrieve stats) and POST (submit rating) operations.
 */
public class RatingServlet extends HttpServlet {
    private EvaluationService evaluationService;
    private AuthenticationService authService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        evaluationService = new EvaluationService();
        authService = new AuthenticationService();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String evenementIdStr = request.getParameter("evenementId");

        if (evenementIdStr == null) {
            sendJsonError(response, "ID événement manquant");
            return;
        }

        try {
            Long evenementId = Long.parseLong(evenementIdStr);
            Double avgRating = evaluationService.getAverageRating(evenementId);
            long count = evaluationService.countByEvent(evenementId);
            long[] distribution = evaluationService.getRatingDistribution(evenementId);

            Map<String, Object> result = new HashMap<>();
            result.put("averageRating", avgRating);
            result.put("count", count);
            result.put("distribution", distribution);

            sendJsonResponse(response, result);

        } catch (NumberFormatException e) {
            sendJsonError(response, "ID événement invalide");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long userId = authService.getCurrentUserId(request.getSession());
        if (userId == null) {
            sendJsonError(response, "Non authentifié");
            return;
        }

        String evenementIdStr = request.getParameter("evenementId");
        String noteStr = request.getParameter("note");
        String commentaire = request.getParameter("commentaire");

        try {
            Long evenementId = Long.parseLong(evenementIdStr);
            Integer note = Integer.parseInt(noteStr);

            Evaluation evaluation = evaluationService.rateEvent(userId, evenementId, note, commentaire);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("evaluation", evaluation);
            result.put("averageRating", evaluationService.getAverageRating(evenementId));

            sendJsonResponse(response, result);

        } catch (NumberFormatException e) {
            sendJsonError(response, "Données invalides");
        } catch (IllegalArgumentException e) {
            sendJsonError(response, e.getMessage());
        }
    }

    private void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(data));
        out.flush();
    }

    private void sendJsonError(HttpServletResponse response, String message) throws IOException {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        sendJsonResponse(response, error);
    }
}
