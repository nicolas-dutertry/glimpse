package org.glimpse.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glimpse.server.GlimpseUtils;
import org.glimpse.server.manager.AuthenticationException;
import org.glimpse.server.manager.UserManager;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Log logger = LogFactory.getLog(LoginServlet.class);

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		UserManager userManager = 
				WebApplicationContextUtils.getWebApplicationContext(getServletContext()).getBean(
						UserManager.class);
		
		String login = request.getParameter("login");
		String password = request.getParameter("password");
		
		try {
			String connectionId = userManager.connect(login, password);
			GlimpseUtils.setConnectionId(request, response, connectionId, false);
			response.setContentType("text/plain");
			response.getWriter().print("OK");
		} catch (AuthenticationException e) {
			logger.error("Connection error", e);
		}
	}

}
