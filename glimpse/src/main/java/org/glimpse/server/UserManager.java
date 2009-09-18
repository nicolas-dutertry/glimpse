package org.glimpse.server;

import org.glimpse.client.UserDescription;
import org.glimpse.client.layout.PageDescription;

public interface UserManager {
	UserDescription getUserDescription(String userId);
	void setUserDescription(String userId, UserDescription userDescription);
	UserDescription getDefaultUserDescription();
	
	PageDescription getUserPageDescription(String userId);
	void setUserPageDescription(String userId, PageDescription pageDescription);
	PageDescription getDefaultPageDescription();	
}
