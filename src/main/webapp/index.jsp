<%@ page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; UTF-8">
<title>Betoffice Web Application</title>
</head>
<body>
  <h1>betoffice jweb</h1>
  <ul>
    <li><%= new Date().toString() %></li>
    <li>PathInfo: <%= request.getPathInfo() %></li>
    <li>ServletPath: <%= request.getServletPath() %></li>
    <li>ServerName: <%= request.getServerName() %></li>
    <li>ServletPort: <%= request.getServerPort() %></li>
    <li>ContextPath: <%= request.getContextPath() %></li>
    <li>LocalAddr: <%= request.getLocalAddr() %></li>
    <li>LocalPort: <%= request.getLocalPort() %></li>
    <li>RemoteHost: <%= request.getRemoteHost() %></li>
    <li><a href="./test_tipp.html">Test Tipp Formular</a>
  </ul>
  <h2>Umlaute:</h2>
  <p>
    ÄÖÜ äöü ß
  </p>
  <p>
    <form action="/betoffice-jweb/bo/tipp/auth" method="post">
      <table>
        <tr>
          <td>Name:</td>
          <td><input type="text" id="nickname" name="nickname" size="15"/></td>
        </tr>
        <tr>
          <td>Password:</td>
          <td><input type="text" id="password" name="password" size="15"/></td>
        </tr>
        <tr>
          <td><input type="submit">Submit</td>
        </tr>
      </table>
    </form>
  </p>
</body>
</html>
