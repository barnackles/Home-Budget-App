<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<!DOCTYPE html>
<head>
    <title>HomeBudgetApp</title>
</head>

<body>

<div class="container">
    <form action="<c:url value = "/login"/>" method="POST">
        <br/>
        Login : <input type="text" name="userLogin" class="form-control"/>
        Password: <input type="password" name="password" class="form-control"/>
        <input type="submit" value="Sign In"/>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
</div>
</body>
</html>