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
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ConnectionFilter implements Filter {
	private ServletContext servletContext;
	
	public void init(FilterConfig config) throws ServletException {
		servletContext = config.getServletContext();
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String connectionId = GlimpseUtils.getConnectionId((HttpServletRequest)request);
		if(StringUtils.isNotEmpty(connectionId)) {
			ConnectionManager connectionManager = 
				WebApplicationContextUtils.getWebApplicationContext(servletContext).getBean(
						ConnectionManager.class);
			request.setAttribute(GlimpseUtils.REQUEST_ATTRIBUTE_USER_ID,
					connectionManager.getUserId(connectionId));
		} else {
			request.removeAttribute(GlimpseUtils.REQUEST_ATTRIBUTE_USER_ID);
		}
		chain.doFilter(request, response);
	}
	
	public void destroy() {
	}

}
