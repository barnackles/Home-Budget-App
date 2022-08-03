<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>HOME!</h1>

<a href="<c:url value = "/login" />" >Sign in</a> <br>
<a href="<c:url value = "/registration" />" >Register</a> <br>
<a href="<c:url value = "/test" />" >TEST</a>

<%--<table border="1">--%>
<%--    <tr>--%>
<%--        <td>Tytuł</td>--%>
<%--        <td>Data dodania</td>--%>
<%--        <td>Zawartość</td>--%>
<%--    </tr>--%>
<%--    <c:forEach items="${articles}" var="article">--%>
<%--        <tr>--%>
<%--            <td>${article.title}</td>--%>
<%--            <td>${article.created}</td>--%>
<%--            <td>${article.content}</td>--%>
<%--        </tr>--%>
<%--    </c:forEach>--%>
<%--</table>--%>
</body>
</html>