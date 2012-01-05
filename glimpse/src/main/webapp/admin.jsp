<%@page import="org.glimpse.server.GlimpseUtils"%>
<%@ page session="false"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader("Expires", 0); //prevent caching at the proxy server
%>

<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		
		<link type="text/css" rel="stylesheet" href="css/smoothness/jquery-ui-1.8.16.custom.css">
		<link type="text/css" rel="stylesheet" href="css/jquery.contextMenu.css">
		<link type="text/css" rel="stylesheet" href="css/admin.css">
		
		<link rel="shortcut icon" href="themes/default/favicon.ico"/>
	
		<script type="text/javascript" src="javascript/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="javascript/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="javascript/jquery.contextMenu.js"></script>
		<script type="text/javascript" src="javascript/admin.js"></script>
		<script type="text/javascript">
		setCurrentUserId("<%=GlimpseUtils.getUserId(request)%>");
		</script>
	</head>

	<body>
		<div class="header">Glimpse - User administration</div>
		
		<p>
			<a id="homeButton" href=".">Home</a>
			<a id="refreshButton" href="javascript:refresh()">Refresh</a>
			<a id="addUserButton" href="javascript:openAddUserDialog()">Add user</a>
		</p>
		
		<div id="userList"></div>
		
		<div id="addUserDialog" title="Add user">
			<form name="addUserForm" id="addUserForm" method="post">
				<table>
					<tr>
						<td>User Identifier</td>
						<td><input class="required" type="text" name="userId"/></td>
					</tr>
					<tr>
						<td>Password</td>
						<td><input class="required" type="password" name="password"/></td>
					</tr>
					<tr>
						<td>Confirm password</td>
						<td><input class="required" type="password" name="password2"/></td>
					</tr>
				</table>
			</form>
		</div>
		
		<div id="editUserDialog" title="Edit user">
			<form name="editUserForm" id="editUserForm" method="post">
				<table>
					<tr>
						<td>User Identifier</td>
						<td><input type="text" name="userId" disabled="disabled"/></td>
					</tr>
					<tr>
						<td>Label</td>
						<td><input class="required" type="text" name="label"/></td>
					</tr>
					<tr>
						<td>Administrator</td>
						<td>
							<select id="editAdministrator" name="administrator">
								<option value="true">true</option>
								<option value="false">false</option>
							</select>
						</td>
					</tr>
					<tr>
						<td>Locale</td>
						<td><input class="required" type="text" name="locale"/></td>
					</tr>
					<tr>
						<td>Theme</td>
						<td><input class="required" type="text" name="theme"/></td>
					</tr>
				</table>
			</form>
		</div>
		
		<div id="changePasswordDialog" title="Change password">
			<form name="changePasswordForm" id="changePasswordForm" method="post">
				<table>
					<tr>
						<td>User Identifier</td>
						<td><input type="text" name="userId" disabled="disabled"/></td>
					</tr>
					<tr>
						<td>Password</td>
						<td><input class="required" type="password" name="password"/></td>
					</tr>
					<tr>
						<td>Confirm password</td>
						<td><input class="required" type="password" name="password2"/></td>
					</tr>
				</table>
			</form>
		</div>
		
		<ul id="userMenu" class="contextMenu" style="width: 140px;">
			<li class="edit"><a href="#edit">Edit</a></li>
			<li><a href="#changePassword">Change password</a></li>
			<li class="delete separator"><a href="#delete">Delete</a></li>
		</ul>
		
		<div id="message" class="message" onclick="animateHideMessage()">
			<h3 id="message-text">Message</h3>
		</div>
	</body>
</html>
