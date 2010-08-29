<%@ page session="false"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
	<head>
		<script type="text/javascript">
		function delete(userId) {
			if(confirm("Deleting a user cannot be undone. Are you sure ?")) {
				document.deleteForm.userId.value = userId;
				document.deleteForm.submit();
			}
		}
		</script>
	</head>
	<body>
		<table>
			<tr>
				<td>ID</td>
				<td>Administrator</td>
				<td>Label</td>
				<td>Locale</td>
				<td>Theme</td>
				<td/>
			</tr>
			<c:forEach var="userDescription" items="${userDescriptions}">
				<tr>
					<td>${userDescription.id}</td>
					<td>${userDescription.attributes.administrator}</td>
					<td>${userDescription.attributes.preferences.label}</td>
					<td>${userDescription.attributes.preferences.locale}</td>
					<td>${userDescription.attributes.preferences.theme}</td>
					<td><a href="javascript:delete('${userDescription.id}')">Delete</a></td>
				</tr>
			</c:forEach>
		</table>
		<form name="deleteForm" id="deleteForm" method="post">
			<input type="hidden" name="actionType" value="delete"/>
			<input type="hidden" name="userId"/>
		</form>
	</body>
</html>