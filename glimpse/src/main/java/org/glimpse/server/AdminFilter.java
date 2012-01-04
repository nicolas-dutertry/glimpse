package org.glimpse.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.glimpse.client.UserAttributes;

public class AdminFilter implements Filter {
	
	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		UserAttributes userAttributes = GlimpseUtils.getUserAttributes((HttpServletRequest)request);
		if(userAttributes != null && userAttributes.isAdministrator()) {
			chain.doFilter(request, response);
		} else {
			HttpServletResponse httpresponse = (HttpServletResponse)response;
			httpresponse.setStatus(403);
			httpresponse.getWriter().println("Forbidden");
		}
	}

	@Override
	public void destroy() {
	}

}
