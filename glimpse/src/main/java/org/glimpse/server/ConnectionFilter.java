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

public class ConnectionFilter implements Filter {
	private ServletContext servletContext;
	
	public void init(FilterConfig config) throws ServletException {
		servletContext = config.getServletContext();
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String connectionId = GlimpseUtils.getConnectionId((HttpServletRequest)request);
		if(StringUtils.isNotEmpty(connectionId)) {
			GlimpseManager glimpseManager = GlimpseManager.getInstance(servletContext);
			ConnectionManager connectionManager = glimpseManager.getConnectionManager();
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
