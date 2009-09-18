package org.glimpse.server;

import org.apache.commons.lang.StringUtils;
import org.glimpse.client.PageDescriptionService;
import org.glimpse.client.layout.PageDescription;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class PageDescriptionServiceImpl extends RemoteServiceServlet implements
		PageDescriptionService {
	private static final long serialVersionUID = 1L;
	
	public PageDescription getPageDescription() {
		GlimpseManager glimpseManager = GlimpseManager.getInstance(getServletContext());
		UserManager userManager = glimpseManager.getUserManager();
		String userId = getUserId();
		if(userId == null) {
			return userManager.getDefaultPageDescription();
		} else {
			return userManager.getUserPageDescription(userId);
		}
	}
	
	public void setPageDescription(PageDescription pageDescription) {
		String userId = getUserId();
		if(userId != null) {
			GlimpseManager glimpseManager = GlimpseManager.getInstance(getServletContext());
			UserManager userManager = glimpseManager.getUserManager();			
			userManager.setUserPageDescription(userId, pageDescription);
		}
	}
		
	private String getUserId() {
		String connectionId = GlimpseUtils.getConnectionId(getThreadLocalRequest());
		if(StringUtils.isNotEmpty(connectionId)) {
			GlimpseManager glimpseManager = GlimpseManager.getInstance(getServletContext());
			ConnectionManager connectionManager = glimpseManager.getConnectionManager();
			return connectionManager.getUserId(connectionId);
		} else {
			return null;
		}
	}	

}
