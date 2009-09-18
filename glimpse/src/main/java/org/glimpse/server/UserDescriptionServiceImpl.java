package org.glimpse.server;

import org.apache.commons.lang.StringUtils;
import org.glimpse.client.UserDescription;
import org.glimpse.client.UserDescriptionService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class UserDescriptionServiceImpl extends RemoteServiceServlet implements
		UserDescriptionService {
	private static final long serialVersionUID = 1L;

	public UserDescription getUserDescription() {
		GlimpseManager glimpseManager = GlimpseManager.getInstance(getServletContext());
		UserManager userManager = glimpseManager.getUserManager();
		String userId = getUserId();
		if(userId == null) {
			return userManager.getDefaultUserDescription();
		} else {
			return userManager.getUserDescription(userId);
		}
	}

	public void setUserDescription(UserDescription userDescription) {
		String userId = getUserId();
		if(userId != null) {
			GlimpseManager glimpseManager = GlimpseManager.getInstance(getServletContext());
			UserManager userManager = glimpseManager.getUserManager();			
			userManager.setUserDescription(userId, userDescription);
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
