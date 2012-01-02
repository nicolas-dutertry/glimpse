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
package org.glimpse.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glimpse.server.manager.UserManager;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ConnectionFilter implements Filter {
	private static final Log logger = LogFactory.getLog(ConnectionFilter.class);
	
	/*
	private static final String[] ADMIN_PATHS = {
		"/servlets/modify-user",
		"/servlets/user-admin",
		"/monitoring"
	};
	*/
	
	private ServletContext servletContext;
	
	public void init(FilterConfig config) throws ServletException {
		servletContext = config.getServletContext();
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String connectionId = GlimpseUtils.getConnectionId((HttpServletRequest)request);
		if(StringUtils.isNotEmpty(connectionId)) {
			if(logger.isDebugEnabled()) {
				logger.debug("Connection id found : <" + connectionId + ">");
			}
			
			UserManager userManager = 
				WebApplicationContextUtils.getWebApplicationContext(servletContext).getBean(
						UserManager.class);
			String userId = userManager.getUserId(connectionId);
			
			if(logger.isDebugEnabled()) {
				logger.debug("User id is : <" + userId + ">");
			}
			
			request.setAttribute(GlimpseUtils.REQUEST_ATTRIBUTE_USER_ID, userId);
		} else {
			logger.debug("No connection id found");
			request.removeAttribute(GlimpseUtils.REQUEST_ATTRIBUTE_USER_ID);
		}
		chain.doFilter(request, response);
	}
	
	public void destroy() {
	}
	
	/*
	private boolean isAdminRequest(HttpServletRequest httprequest) {
		String uri = httprequest.getRequestURI();
		for (String path : ADMIN_PATHS) {
			if(uri.startsWith(httprequest.getContextPath() + path)) {
				return true;
			}
		}
		return false;
	}
	*/

}
