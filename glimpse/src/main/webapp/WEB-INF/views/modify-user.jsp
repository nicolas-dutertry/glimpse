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
			<input type="hidden" name="userId" value="${userId}"/>
			<table>	
				<tr>
					<td>User ID</td>
					<td>${userId}</td>
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
					<td>Administrator</td>
					<td>
						<select name="administrator">
							<option <c:if test="${userAttributes.administrator}">selected="selected"</c:if>>true</option>
							<option <c:if test="${!userAttributes.administrator}">selected="selected"</c:if>>false</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>Label</td>
					<td><input type="text" name="label" value="${userAttributes.preferences.label}"/></td>
				</tr>
				<tr>
					<td>Locale</td>
					<td><input type="text" name="locale" value="${userAttributes.preferences.locale}"/></td>
				</tr>
				<tr>
					<td>Theme</td>
					<td><input type="text" name="Theme" value="${userAttributes.preferences.theme}"/></td>
				</tr>
				<tr>
					<td>Page file</td>
					<td><input type="file" name="page" accept="text/xml"/></td>
				</tr>
			</table>
			<input type="submit" value="OK"/>
		</form>
		<a href="<c:url value="/servlets/user-admin"/>">Back</a>
	</body>
</html>