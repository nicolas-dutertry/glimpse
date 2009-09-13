package org.glimpse.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class ConnectionServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		if(action.equals("connect")) {
			connect(request, response);
		} else if(action.equals("disconnect")) {
			disconnect(request, response);
		} else {
			throw new ServletException("Unknown action");
		}
	}
	
	private void connect(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String login = request.getParameter("login");
		String password = request.getParameter("password");
		
		ConnectionManager connectionManager =
			GlimpseManager.getInstance(getServletContext()).getConnectionManager();
		try {
			String connectionId = connectionManager.connect(login, password);
			GlimpseUtils.setConnectionId(response, connectionId);
		} catch (AuthenticationException e) {
			throw new ServletException("Connection failed", e);
		}
		
		response.sendRedirect(request.getContextPath() + "/index.html");
	}
	
	private void disconnect(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String connectionId = GlimpseUtils.getConnectionId(request);
		if(StringUtils.isNotEmpty(connectionId)) {
			ConnectionManager connectionManager =
				GlimpseManager.getInstance(getServletContext()).getConnectionManager();
			connectionManager.disconnect(connectionId);
			GlimpseUtils.setConnectionId(response, "");
		}
		
		response.sendRedirect(request.getContextPath() + "/index.html");
	}

}
