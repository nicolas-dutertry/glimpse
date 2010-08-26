<%@ page session="false"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
	<head>
		<style>
			.errorMessage {
				color: red;
			}
			.infoMessage {
				color: blue;
			}
		</style>
	</head>
	<body>
		<div class="errorMessage">${errorMessage}</div>
		<div class="infoMessage">${message}</div>
		<form method="post" enctype="multipart/form-data">
			<table>
				<tr>
					<td>User ID</td>
					<td><input type="text" name="userId"/></td>
				</tr>
				<tr>
					<td>Password</td>
					<td><input type="password" name="password1"/></td>
				</tr>
				<tr>
					<td>Retype password</td>
					<td><input type="password" name="password2"/></td>
				</tr>
				<tr>
					<td>User description file</td>
					<td><input type="file" name="userDescription" accept="text/xml"/></td>
				</tr>
				<tr>
					<td>Page file</td>
					<td><input type="file" name="page" accept="text/xml"/></td>
				</tr>
			</table>
			<input type="submit" value="OK"/>
		</form>
	</body>
</html>