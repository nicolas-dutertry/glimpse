var users;
var currentUserId;

function setCurrentUserId(userId) {
	currentUserId = userId;
}

function getUserIndex(userId) {
	for(var i = 0; i < users.length; i++) {
		var user = users[i];
		if(user.id == userId) {
			return i;
		}
	}
	return -1;
}

function getUser(userId) {
	var i = getUserIndex(userId);
	if(i != -1) {
		return users[i];
	}
	return null;
}

function deleteUser(userId) {
	if(userId == currentUserId) {
		showMessage("error", "Cannot delete current connected user");
		return;
	}
	
	if(confirm("Deleting a user cannot be undone. Are you sure you want to delete " + userId + " ?")) {
		var i = getUserIndex(userId);
		users.splice(1, i);
		buildUserList();
		
		$.ajax({
			url: "services/users/" + userId,
			type: "delete",
			cache: false,
			success: function( data ) {
			},
			error: function() {
				showMessage("error", "Error while deleting " + userId)
			}
		});
	}
}

function refresh() {
	$("#userList").html("Loading...");
	$.ajax({
		url: "services/users",
		type: "GET",
		dataType: "json",
		cache: false,
		success: function( data ) {
			users = data["userDescription"];
			buildUserList();
		}
	});
}

function buildUserList() {
	$("#userList").html("Loading...");
	var s = "<table style=\"width: 100%\"><tr><th>User Identifier</th><th>Label</th><th>Administrator</th><th>Locale</th><th>Theme</th></tr>";
	for(var i = 0; i < users.length; i++) {
		var user = users[i];
		var cssClass = "darkline";
		if(i % 2 == 0) {
			cssClass = "clearline";
		}
		s+= "<tr class=\"" + cssClass + "\">";
		s+= "<td>" + user.id + "</td>";
		s+= "<td>" + user.attributes.preferences.label + "</td>";
		s+= "<td>" + user.attributes.administrator + "</td>";
		s+= "<td>" + user.attributes.preferences.locale + "</td>";
		s+= "<td>" + user.attributes.preferences.theme + "</td>";
		s+= "</td>";
		
		s+= "</tr>";
	}
	
	s += "</table>";
	
	
	$("#userList").html(s);
	
	$("#userList tr").contextMenu({
			menu: "userMenu"
		},				
		function(action, el, pos) {
			var userId = $("td", el).first().text();
			if(action == "edit") {
				openEditUserDialog(userId);
			} else if(action == "delete") {
				deleteUser(userId);
			} else if(action == "changePassword") {
				openChangePasswordDialog(userId);
			}
		}
	);
}

function clearMissingRequiredFields(formId) {
	$("#" + formId +" input").removeClass("missing");
}

function checkMissingRequiredFields(formId) {
	clearMissingRequiredFields(formId);
	var selector = "#" + formId + " input.required";
	var isMissing = false;
	$(selector).each(function() {
		if($(this).val() == '') {
			isMissing = true;
			$(this).addClass("missing");
		}
	});
	if(isMissing) {
		showMessage("error", "Missing required field");
	}
	return !isMissing;
}

function openAddUserDialog() {
	clearMissingRequiredFields("addUserForm");
	document.addUserForm.userId.value = "";
	document.addUserForm.password.value = "";
	document.addUserForm.password2.value = "";
	$("#addUserDialog").dialog("open");
}

function addUser() {
	$.ajax({
		url: "services/users",
		type: "POST",
		cache: false,
		data: {
			userId: document.addUserForm.userId.value,
			password: document.addUserForm.password.value
		},
		success: function( data ) {
			refresh();
		},
		error: function(jqXHR, textStatus, errorThrown) {
			showMessage("error", jqXHR.responseText);
		}
	});
}

function openEditUserDialog(userId) {
	clearMissingRequiredFields("addEditForm");
	
	document.editUserForm.userId.value = userId;
	
	var user = getUser(userId);
	
	$("#editAdministrator").removeAttr("disabled");
	if(user.attributes.administrator) {
		$("#editAdministrator").val("true");
	} else {
		$("#editAdministrator").val("false");
	}
	if(userId == currentUserId) {
		$("#editAdministrator").attr("disabled", "disabled");
	}
	
	document.editUserForm.label.value = user.attributes.preferences.label;
	document.editUserForm.locale.value = user.attributes.preferences.locale;
	document.editUserForm.theme.value = user.attributes.preferences.theme;
	
	
	$("#editUserDialog").dialog("open");
}

function updateUser() {
	var userId = document.editUserForm.userId.value;
	var user = getUser(userId);
	if($("#editAdministrator").val() == "true") {
		user.attributes.administrator = true;
	} else {
		user.attributes.administrator = false;
	}
	
	user.attributes.preferences.label = document.editUserForm.label.value;
	user.attributes.preferences.locale = document.editUserForm.locale.value;
	user.attributes.preferences.theme = document.editUserForm.theme.value;
	
	buildUserList();
	
	$.ajax({
		url: "services/users/" + userId + "/attributes",
		type: "POST",
		cache: false,
		data: {
			administrator: $("#editAdministrator").val(),
			label: document.editUserForm.label.value,
			locale: document.editUserForm.locale.value,
			theme: document.editUserForm.theme.value
		},
		success: function( data ) {
			refresh();
		},
		error: function(jqXHR, textStatus, errorThrown) {
			showMessage("error", jqXHR.responseText);
		}
	});
}

function openChangePasswordDialog(userId) {
	clearMissingRequiredFields("changePasswordForm");
	
	document.changePasswordForm.userId.value = userId;
	document.changePasswordForm.password.value = "";
	document.changePasswordForm.password2.value = "";			
	
	$("#changePasswordDialog").dialog("open");
}

function changePassword() {
	var userId = document.changePasswordForm.userId.value;
	
	$.ajax({
		url: "services/users/" + userId + "/password",
		type: "POST",
		cache: false,
		data: {
			password: document.changePasswordForm.password.value
		},
		success: function( data ) {
			showMessage("success", "Password successfully changed");
		},
		error: function(jqXHR, textStatus, errorThrown) {
			showMessage("error", jqXHR.responseText);
		}
	});
}

var messageTimeout = null;
function showMessage(type, text) {
	hideMessage();
	
	$("#message").removeClass("info error warning success");
	$("#message").addClass(type);
	$("#message-text").html(text);
	$("#message").animate({top:"0"}, 500);
	
	messageTimeout = setTimeout(animateHideMessage, 5000);
}

function hideMessage() {
	if(messageTimeout != null) {
		clearTimeout(messageTimeout);
	}
	messageTimeout = null;
	var messageHeight = $("#message").outerHeight();
	 //move element outside viewport
    $("#message").css('top', -messageHeight);
}

function animateHideMessage() {
	if(messageTimeout != null) {
		clearTimeout(messageTimeout);
	}
	messageTimeout = null;
	$("#message").animate({top: -$("#message").outerHeight()}, 500);
}

$(function() {
	$("#homeButton").button({
		icons: {
			primary: "home-icon"
		}
	});
	
	$("#refreshButton").button({
		icons: {
			primary: "refresh-icon"
		}
	});
	
	$("#addUserButton").button({
			icons: {
				primary: "add-user-icon"
			}
	});
	
	$("#addUserDialog").dialog({
		autoOpen : false,
		modal : true,
		buttons : {
			OK : function() {
				if (checkMissingRequiredFields("addUserDialog")) {
					if(document.addUserForm.password.value != document.addUserForm.password2.value) {
						showMessage("error", "Passwords do not match");
					} else {
						$(this).dialog("close");
						addUser();
					}
				}
			},
			Cancel : function() {
				$(this).dialog("close");
			}
		}
	});
	
	$("#editUserDialog").dialog({
		autoOpen : false,
		modal : true,
		buttons : {
			OK : function() {
				if (checkMissingRequiredFields("editUserDialog")) {
					$(this).dialog("close");
					updateUser();
				}
			},
			Cancel : function() {
				$(this).dialog("close");
			}
		}
	});
	
	$("#changePasswordDialog").dialog({
		autoOpen : false,
		modal : true,
		buttons : {
			OK : function() {
				if (checkMissingRequiredFields("changePasswordDialog")) {
					if(document.changePasswordForm.password.value != document.changePasswordForm.password2.value) {
						showMessage("error", "Passwords do not match");
					} else {
						$(this).dialog("close");
						changePassword();
					}
				}
			},
			Cancel : function() {
				$(this).dialog("close");
			}
		}
	});
	
	hideMessage();
	
	refresh();
});