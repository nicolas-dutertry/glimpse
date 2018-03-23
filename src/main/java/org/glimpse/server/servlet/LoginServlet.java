/*
 * Copyright (C) 2009 Nicolas Dutertry
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
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
