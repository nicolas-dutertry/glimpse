package org.glimpse.server.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.glimpse.client.UserAttributes;
import org.glimpse.client.UserPreferences;
import org.glimpse.server.GlimpseUtils;
import org.glimpse.server.manager.DuplicateUserIdException;
import org.glimpse.server.manager.InvalidPasswordException;
import org.glimpse.server.manager.InvalidUserIdException;
import org.glimpse.server.manager.UserManager;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class UserAdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String connectedUserId = GlimpseUtils.getUserId(request);
		if(StringUtils.isBlank(connectedUserId)) {
			throw new ServletException("Login before using this page");
		}
		
		UserManager userManager = WebApplicationContextUtils.getWebApplicationContext(
				getServletContext()).getBean(UserManager.class);
		
		UserAttributes connectedUserAttributes = userManager.getUserAttributes(connectedUserId);
		if(connectedUserAttributes == null) {
			throw new ServletException("Login before using this page");
		}
		
		if(!connectedUserAttributes.isAdministrator()) {
			throw new ServletException("You must be administrator to use this page");
		}
		
		String pageName = "user-list.jsp";
		String errorMessage = null;
		String message = null;
		String actionType = request.getParameter("actionType");
		if(StringUtils.isEmpty(actionType)) {
			// nothing to do
		} else if(actionType.equals("create")) {
			String userId = request.getParameter("userId");
			String password1 = request.getParameter("password1");
			String password2 = request.getParameter("password2");
			
			if(StringUtils.isBlank(userId)) {
				errorMessage = "User ID is empty";
			}
			
			if(StringUtils.isEmpty(errorMessage) && StringUtils.isBlank(password1)) {
				errorMessage = "Password is empty";
			}
			
			if(StringUtils.isEmpty(errorMessage) && !StringUtils.equals(password1, password2)) {
				errorMessage = "Passwords do not match";
			}
			
			if(StringUtils.isEmpty(errorMessage)) {
				try {
					userManager.createUser(userId, password1);
					message = "User " + userId + " created";
				} catch (InvalidUserIdException e) {
					errorMessage = "Invalid user ID";
				} catch (InvalidPasswordException e) {
					errorMessage = "Invalid password";
				} catch (DuplicateUserIdException e) {
					errorMessage = "User ID already exists";
				}
			}
		} else if(actionType.equals("delete")) {
			String userId = request.getParameter("userId");
			
			if(connectedUserId.equals(userId)) {
				errorMessage = "Cannot remove current user";
			} else {
				userManager.deleteUser(userId);
				message = "User " + userId + " deleted";
			}
		} else if(actionType.equals("setAttributes")) {
			String userId = request.getParameter("userId");
			boolean administrator = "true".equals(request.getParameter("administrator"));
			String label = request.getParameter("label");
			String locale = request.getParameter("locale");
			String theme = request.getParameter("theme");
			
			if(connectedUserId.equals(userId) && !administrator) {
				errorMessage = "Cannot remove administrator rights to the current user";
			} else {
				UserAttributes userAttributes = new UserAttributes();
				userAttributes.setAdministrator(administrator);
				UserPreferences userPreferences = new UserPreferences();
				userPreferences.setLabel(label);
				userPreferences.setLocale(locale);
				userPreferences.setTheme(theme);
				userAttributes.setPreferences(userPreferences);
				userManager.setUserAttributes(userId, userAttributes);
			}
		}
		
		if(errorMessage != null) {
			request.setAttribute("errorMessage", errorMessage);
		}
		
		if(message != null) {
			request.setAttribute("message", message);
		}
		
		if(pageName.equals("user-list.jsp")) {
			Set<String> userIds = userManager.getUsers();
			Map<String, UserAttributes> userAttributesMap = new HashMap<String, UserAttributes>();
			for (String userId : userIds) {
				UserAttributes userAttributes = userManager.getUserAttributes(userId);
				userAttributesMap.put(userId, userAttributes);
			}
			request.setAttribute("userAttributesMap", userAttributesMap);
		}
		getServletContext().getRequestDispatcher("/WEB-INF/views/" + pageName).forward(request, response);
	}
}
