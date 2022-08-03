<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="input" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<head>
        <title>Registration</title>
</head>

<body>

<div class="container">
Please register:
        <form:form method="POST" modelAttribute="user">

                Username:
                <input:input path="userName"/>
                <input:errors path="userName" /> <br>
                Email:
                <input:input path="email"/>
                <input:errors path="email"/> <br>
                Password:
                <input:password path="password" />
                <input:errors path="password"/> <br>
                <input type="submit" value="Sign In"/>
        </form:form>


</div>
</body>
</html>