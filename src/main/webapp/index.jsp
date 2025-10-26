<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // Direct redirect to catalogue without showing any page
    response.sendRedirect(request.getContextPath() + "/catalogue");
%>