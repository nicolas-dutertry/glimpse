package org.glimpse.server.servlet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.glimpse.client.UserAttributes;
import org.glimpse.client.UserDescription;
import org.glimpse.client.UserPreferences;
import org.glimpse.server.GlimpseUtils;
import org.glimpse.server.UserManager;
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
		String actionType = request.getParameter("actionType");
		if(StringUtils.isEmpty(actionType)) {
			Set<String> userIds = userManager.getUsers();
			Set<UserDescription> userDescriptions = new HashSet<UserDescription>();
			for (String userId : userIds) {
				UserAttributes userAttributes = userManager.getUserAttributes(userId);
				UserDescription userDescription = new UserDescription(userId);
				userDescription.setAttributes(userAttributes);
				userDescriptions.add(userDescription);
			}
			request.setAttribute("userDescriptions", userDescriptions);
		} else if(actionType.equals("create")) {
			String userId = request.getParameter("userId");
			String password = request.getParameter("password");
			userManager.createUser(userId, password);
		} else if(actionType.equals("delete")) {
			String userId = request.getParameter("userId");
			userManager.deleteUser(userId);
		} else if(actionType.equals("setAttributes")) {
			String userId = request.getParameter("userId");
			boolean administrator = "true".equals(request.getParameter("administrator"));
			String label = request.getParameter("label");
			String locale = request.getParameter("locale");
			String theme = request.getParameter("theme");
			UserAttributes userAttributes = new UserAttributes();
			userAttributes.setAdministrator(administrator);
			UserPreferences userPreferences = new UserPreferences();
			userPreferences.setLabel(label);
			userPreferences.setLocale(locale);
			userPreferences.setTheme(theme);
			userAttributes.setPreferences(userPreferences);
			userManager.setUserAttributes(userId, userAttributes);
		}
		
		getServletContext().getRequestDispatcher("/WEB-INF/views/" + pageName).forward(request, response);
	}
	
	

}
