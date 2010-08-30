<%@ page session="false"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
	<head>
		<script type="text/javascript">
		function deleteUser(userId) {
			if(confirm("Deleting a user cannot be undone. Are you sure ?")) {
				document.deleteForm.userId.value = userId;
				document.deleteForm.submit();
			}
		}
		function modifyUser(userId) {
			document.modifyForm.userId.value = userId;
			document.modifyForm.submit();
		}
		</script>
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
		
		<table border="1">
			<tr>
				<th>ID</th>
				<th>Administrator</th>
				<th>Label</th>
				<th>Locale</th>
				<th>Theme</th>
				<th colspan="2"/>
			</tr>
			<c:forEach var="entry" items="${userAttributesMap}">
				<tr>
					<td>${entry.key}</td>
					<td>${entry.value.administrator}</td>
					<td>${entry.value.preferences.label}</td>
					<td>${entry.value.preferences.locale}</td>
					<td>${entry.value.preferences.theme}</td>
					<td><a href="javascript:modifyUser('${entry.key}')">Modify</a></td>
					<td><a href="javascript:deleteUser('${entry.key}')">Delete</a></td>
				</tr>
			</c:forEach>
		</table>
		<form name="deleteForm" id="deleteForm" method="post">
			<input type="hidden" name="actionType" value="delete"/>
			<input type="hidden" name="userId"/>
		</form>
		<form name="modifyForm" id="modifyForm" method="post" action="<c:url value="/servlets/modify-user"/>">
			<input type="hidden" name="userId"/>
		</form>
		<p>Add new user</p>
		<form name="createUserForm" id="createUserForm" method="post">
			<input type="hidden" name="actionType" value="create"/>
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
			</table>
			<a href="javascript:document.createUserForm.submit()">Create</a>
		</form>
	</body>
</html>